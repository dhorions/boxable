
/*
 Quodlibet.be
 */
package be.quodlibet.boxable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Table<T extends PDPage> {

	private final static Logger LOGGER = LoggerFactory.getLogger(Table.class);

	public final PDDocument document;
	private float margin;

	private T currentPage;
	private PDPageContentStream tableContentStream;
	private List<PDOutlineItem> bookmarks;
	private static final int xSpacing = 0;
	private Row<T> header;
	private List<Row<T>> rows = new ArrayList<>();

	private final float yStartNewPage;
	private float yStart;
	private final float bottomMargin;
	private final float width;
	private final boolean drawLines;
	private final boolean drawContent;
	private float headerBottomMargin = 4f;
	
	private boolean drawDebug;

	public Table(float yStart, float yStartNewPage, float bottomMargin, float width, float margin, PDDocument document,
			T currentPage, boolean drawLines, boolean drawContent) throws IOException {
		this.document = document;
		this.drawLines = drawLines;
		this.drawContent = drawContent;
		// Initialize table
		this.yStartNewPage = yStartNewPage;
		this.margin = margin;
		this.width = width;
		this.yStart = yStart;
		this.bottomMargin = bottomMargin;
		this.currentPage = currentPage;
		loadFonts();
		this.tableContentStream = createPdPageContentStream();
	}

	public Table(float yStartNewPage, float bottomMargin, float width, float margin, PDDocument document,
			boolean drawLines, boolean drawContent) throws IOException {
		this.document = document;
		this.drawLines = drawLines;
		this.drawContent = drawContent;
		// Initialize table
		this.yStartNewPage = yStartNewPage;
		this.margin = margin;
		this.width = width;
		this.bottomMargin = bottomMargin;

		// Fonts needs to be loaded before page creation
		loadFonts();
		this.currentPage = createPage();
		this.tableContentStream = createPdPageContentStream();
	}

	protected abstract void loadFonts() throws IOException;

	protected PDType0Font loadFont(String fontPath) throws IOException {
		return BoxableUtils.loadFont(getDocument(), fontPath);
	}

	protected PDDocument getDocument() {
		return document;
	}

	public void drawTitle(String title, PDFont font, int fontSize, float tableWidth, float height, String alignment) throws IOException {

		if(title == null){
			// if you don't have title just use height from sublock with max textBox
			yStart -= height;
		} else {
			PDPageContentStream articleTitle = createPdPageContentStream();
			// TODO: why do we need to cast to int?
			Paragraph paragraph = new Paragraph(title, font, fontSize, tableWidth, HorizontalAlignment.get(alignment));
			paragraph.setDrawDebug(drawDebug);
			yStart = paragraph.write(articleTitle, margin, yStart);
			if(paragraph.getHeight() < height){
				yStart -= (height -paragraph.getHeight());
			}
			
			articleTitle.close();
		}

		if (drawDebug) {
			// margin
			PDStreamUtils.rect(tableContentStream, margin, yStart, width, headerBottomMargin, Color.CYAN);
		}

		yStart -= headerBottomMargin;
	}

	public float getWidth() {
		return width;
	}

	public Row<T> createRow(float height) {
		Row<T> row = new Row<T>(this, height);
		this.rows.add(row);
		return row;
	}

	public Row<T> createRow(List<Cell<T>> cells, float height) {
		Row<T> row = new Row<T>(this, cells, height);
		this.rows.add(row);
		return row;
	}

	public float draw() throws IOException {
		for (Row<T> row : rows) {
			drawRow(row);
		}
		endTable();

		return yStart;
	}

	private void drawRow(Row<T> row) throws IOException {
		// draw the bookmark
		if (row.getBookmark() != null) {
			PDPageXYZDestination bookmarkDestination = new PDPageXYZDestination();
			bookmarkDestination.setPage(currentPage);
			bookmarkDestination.setTop((int) yStart);
			row.getBookmark().setDestination(bookmarkDestination);
			this.addBookmark(row.getBookmark());
		}

		if (isEndOfPage(row)) {

			// Draw line at bottom of table
			endTable();

			// Reset yStart to yStartNewPage
			this.yStart = yStartNewPage;

			// Start new table on new page
			this.currentPage = createPage();
			this.tableContentStream = createPdPageContentStream();

			// redraw all headers on each currentPage
			if (header != null) {
				drawRow(header);
			} else {
				LOGGER.warn("No Header Row Defined.");
			}
		}

		if (drawLines) {
			drawVerticalLines(row);
		}

		if (drawContent) {
			drawCellContent(row);
		}

	}

	private PDPageContentStream createPdPageContentStream() throws IOException {
		return new PDPageContentStream(getDocument(), getCurrentPage(), true, true);
	}

	private void drawCellContent(Row<T> row) throws IOException {

		// position into first cell (horizontal)
		float cursorX = margin;

		for (Cell<T> cell : row.getCells()) {
			// no text without font
			if (cell.getFont() == null) {
				throw new IllegalArgumentException("Font is null on Cell=" + cell.getText());
			}

			// position at top of current cell
			// descending by font height - font descent, because we are
			// positioning the base line here
			float cursorY = yStart - cell.getTopPadding() - FontUtils.getHeight(cell.getFont(), cell.getFontSize())
					- FontUtils.getDescent(cell.getFont(), cell.getFontSize());

			switch (cell.getValign()) {
			case TOP:
				break;
			case MIDDLE:
				cursorY -= cell.getVerticalFreeSpace() / 2;
				break;
			case BOTTOM:
				cursorY -= cell.getVerticalFreeSpace();
				break;
			}

			// remember horizontal cursor position, so we can advance to the
			// next cell easily later
			float cellStartX = cursorX;

			// respect left padding
			cursorX += cell.getLeftPadding();

			// remember this horizontal position, as it is the anchor for each
			// new line
			float lineStartX = cursorX;

			// font settings
			this.tableContentStream.setFont(cell.getFont(), cell.getFontSize());

			// if it is head row then please use bold font
			if (row.equals(header)) {
				this.tableContentStream.setFont(cell.getFontBold(), cell.getFontSize());
			}
			this.tableContentStream.setNonStrokingColor(cell.getTextColor());

			// print all lines of the cell
			float tw = 0.0f;
			for (String line : cell.getParagraph().getLines()) {
				// screw you, whitespace!
				line = line.trim();

				// we start at the line start ... seems legit
				cursorX = lineStartX;

				// the widest text does not fill the inner width of the cell? no
				// problem, just add it ;)
				switch (cell.getAlign()) {
				case CENTER:
					cursorX += cell.getHorizontalFreeSpace() / 2;
					break;
				case LEFT:
					break;
				case RIGHT:
					cursorX += cell.getHorizontalFreeSpace();
					break;
				}

				// calculate the width of this line
				tw = Math.max(tw, cell.getFont().getStringWidth(line));
				tw = tw / 1000 * cell.getFontSize();
				float freeSpaceWithinLine = cell.getInnerWidth() - cell.getHorizontalFreeSpace() - tw;
				switch (cell.getAlign()) {
				case CENTER:
					cursorX += freeSpaceWithinLine / 2;
					break;
				case LEFT:
					// it doesn't matter because X position is always the same
					// as row above
					break;
				case RIGHT:
					cursorX += freeSpaceWithinLine;
					break;
				}

				// finally draw the line
				this.tableContentStream.beginText();
				this.tableContentStream.newLineAtOffset(cursorX, cursorY);

				// check if it is header cell
				if (cell.isHeaderCell()) {
					this.tableContentStream.setFont(cell.getFontBold(), cell.getFontSize());
				}

				this.tableContentStream.showText(line);
				this.tableContentStream.endText();
				this.tableContentStream.closePath();

				// advance a line vertically
				cursorY = cursorY - cell.getParagraph().getFontHeight();
			}

			// set cursor to the start of this cell plus its width to advance to
			// the next cell
			cursorX = cellStartX + cell.getWidth();

		}
		// Set Y position for next row
		yStart = yStart - row.getHeight();
	}

	private void drawVerticalLines(Row<T> row) throws IOException {
		float xStart = margin;

		// give an extra margin to the latest cell
		float xEnd = row.xEnd() + xSpacing;

		// Draw Row upper border
		drawLine("Row Upper Border ", xStart, yStart, xEnd, yStart);

		Iterator<Cell<T>> cellIterator = row.getCells().iterator();
		while (cellIterator.hasNext()) {

			Cell<T> cell = cellIterator.next();

			fillCellColor(cell, yStart, xStart, cellIterator);

			float yEnd = yStart - row.getHeight();

			// draw the vertical line to separate cells
			drawLine("Cell Separator ", xStart, yStart, xStart, yEnd);

			xStart += getWidth(cell, cellIterator);
		}

		// draw the last vertical line at the right of the table
		float yEnd = yStart - row.getHeight();

		drawLine("Last Cell ", xEnd, yStart, xEnd, yEnd);
	}

	private void drawLine(String type, float xStart, float yStart, float xEnd, float yEnd) throws IOException {

		this.tableContentStream.setNonStrokingColor(Color.BLACK);
		this.tableContentStream.setStrokingColor(Color.BLACK);

		this.tableContentStream.moveTo(xStart, yStart);
		this.tableContentStream.lineTo(xEnd, yEnd);
		this.tableContentStream.stroke();
		this.tableContentStream.closePath();
	}

	private void fillCellColor(Cell<T> cell, float yStart, float xStart, Iterator<Cell<T>> cellIterator)
			throws IOException {
		// Fill Cell Color
		if (cell.getFillColor() != null) {
			this.tableContentStream.setNonStrokingColor(cell.getFillColor());

			// y start is bottom pos
			yStart = yStart - cell.getHeight();
			float height = cell.getHeight() - 1f;

			float cellWidth = getWidth(cell, cellIterator);
			this.tableContentStream.addRect(xStart, yStart, cellWidth, height);
			this.tableContentStream.fill();
			this.tableContentStream.closePath();

			// Reset NonStroking Color to default value
			this.tableContentStream.setNonStrokingColor(Color.BLACK);
		}
	}

	private float getWidth(Cell<T> cell, Iterator<Cell<T>> cellIterator) {
		float width;
		if (cellIterator.hasNext()) {
			width = cell.getWidth();
		} else {
			width = cell.getExtraWidth() + xSpacing;
		}
		return width;
	}

	protected abstract T createPage();

	private void endTable() throws IOException {
		if (drawLines) {
			// Draw line at bottom
			drawLine("Row Bottom Border ", this.margin, this.yStart, this.margin + width + xSpacing, this.yStart);
		}
		this.tableContentStream.close();
	}

	public T getCurrentPage() {
		checkNotNull(this.currentPage, "No current page defined.");
		return this.currentPage;
	}

	public boolean isEndOfPage(Row<T> row) {

		float currentY = yStart - row.getHeight();
		boolean isEndOfPage = currentY <= (bottomMargin + 10);
		// LOGGER.info("isEndOfPage=" + isEndOfPage);

		// If we are closer than 75 from bottom of currentPage, consider this
		// the end of the currentPage
		// If you add rows that are higher then 75, this needs to be checked
		// manually using getNextYPos
		return isEndOfPage;
	}

	private void addBookmark(PDOutlineItem bookmark) {
		if (bookmarks == null)
			bookmarks = new ArrayList<>();
		bookmarks.add(bookmark);
	}

	public List<PDOutlineItem> getBookmarks() {
		return bookmarks;
	}

	public void setHeader(Row<T> header) {
		this.header = header;
	}

	public Row<T> getHeader() {
		if (header == null) {
			throw new IllegalArgumentException("Header Row not set on table");
		}
		return header;
	}

	public float getMargin() {
		return margin;
	}

	protected void setYStart(float yStart) {
		this.yStart = yStart;
	}

	public boolean isDrawDebug() {
		return drawDebug;
	}

	public void setDrawDebug(boolean drawDebug) {
		this.drawDebug = drawDebug;
	}

}
