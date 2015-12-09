
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

import be.quodlibet.boxable.line.LineStyle;
import be.quodlibet.boxable.page.PageProvider;
import be.quodlibet.boxable.text.WrappingFunction;
import be.quodlibet.boxable.utils.FontUtils;
import be.quodlibet.boxable.utils.PDStreamUtils;

public abstract class Table<T extends PDPage> {

	private final static Logger LOGGER = LoggerFactory.getLogger(Table.class);

	public final PDDocument document;
	private float margin;

	private T currentPage;
	private PDPageContentStream tableContentStream;
	private List<PDOutlineItem> bookmarks;
	private Row<T> header;
	private List<Row<T>> rows = new ArrayList<>();

	private final float yStartNewPage;
	private float yStart;
	private final float width;
	private final boolean drawLines;
	private final boolean drawContent;
	private float headerBottomMargin = 4f;

	private boolean tableIsBroken = false;

	private PageProvider<T> pageProvider;

	// page margins
	private final float pageTopMargin;
	private final float pageBottomMargin;

	private boolean drawDebug;

	/**
	 * @deprecated Use one of the constructors that pass a {@link PageProvider}
	 */
	@Deprecated
	public Table(float yStart, float yStartNewPage, float pageBottomMargin, float width, float margin,
			PDDocument document, T currentPage, boolean drawLines, boolean drawContent) throws IOException {
		this(yStart, yStartNewPage, 0, pageBottomMargin, width, margin, document, currentPage, drawLines, drawContent,
				null);
	}

	/**
	 * @deprecated Use one of the constructors that pass a {@link PageProvider}
	 */
	@Deprecated
	public Table(float yStartNewPage, float pageBottomMargin, float width, float margin, PDDocument document,
			boolean drawLines, boolean drawContent) throws IOException {
		this(yStartNewPage, 0, pageBottomMargin, width, margin, document, drawLines, drawContent, null);
	}

	public Table(float yStart, float yStartNewPage, float pageTopMargin, float pageBottomMargin, float width,
			float margin, PDDocument document, T currentPage, boolean drawLines, boolean drawContent,
			PageProvider<T> pageProvider) throws IOException {
		this.pageTopMargin = pageTopMargin;
		this.document = document;
		this.drawLines = drawLines;
		this.drawContent = drawContent;
		// Initialize table
		this.yStartNewPage = yStartNewPage;
		this.margin = margin;
		this.width = width;
		this.yStart = yStart;
		this.pageBottomMargin = pageBottomMargin;
		this.currentPage = currentPage;
		this.pageProvider = pageProvider;
		loadFonts();
//		this.tableContentStream = createPdPageContentStream();
	}

	public Table(float yStartNewPage, float pageTopMargin, float pageBottomMargin, float width, float margin,
			PDDocument document, boolean drawLines, boolean drawContent, PageProvider<T> pageProvider)
					throws IOException {
		this.pageTopMargin = pageTopMargin;
		this.document = document;
		this.drawLines = drawLines;
		this.drawContent = drawContent;
		// Initialize table
		this.yStartNewPage = yStartNewPage;
		this.margin = margin;
		this.width = width;
		this.pageProvider = pageProvider;
		this.pageBottomMargin = pageBottomMargin;

		// Fonts needs to be loaded before page creation
		loadFonts();
		this.currentPage = pageProvider.createPage();
//		this.tableContentStream = createPdPageContentStream();
	}

	protected abstract void loadFonts() throws IOException;

	protected PDType0Font loadFont(String fontPath) throws IOException {
		return FontUtils.loadFont(getDocument(), fontPath);
	}

	protected PDDocument getDocument() {
		return document;
	}

	public void drawTitle(String title, PDFont font, int fontSize, float tableWidth, float height, String alignment, float freeSpaceForPageBreak, boolean drawHeaderMargin)
			throws IOException {
		drawTitle(title, font, fontSize, tableWidth, height, alignment, freeSpaceForPageBreak, null, drawHeaderMargin);
	}

	public void drawTitle(String title, PDFont font, int fontSize, float tableWidth, float height, String alignment, float freeSpaceForPageBreak,
			WrappingFunction wrappingFunction, boolean drawHeaderMargin) throws IOException {

		ensureStreamIsOpen();
		
		if (isEndOfPage(freeSpaceForPageBreak)) {
			this.tableContentStream.close();
			pageBreak();
		}

		if (title == null) {
			// if you don't have title just use the height of maxTextBox in your
			// "row"
			yStart -= height;
		} else {
			PDPageContentStream articleTitle = createPdPageContentStream();
			Paragraph paragraph = new Paragraph(title, font, fontSize, tableWidth, HorizontalAlignment.get(alignment),
					wrappingFunction);
			paragraph.setDrawDebug(drawDebug);
			yStart = paragraph.write(articleTitle, margin, yStart);
			if (paragraph.getHeight() < height) {
				yStart -= (height - paragraph.getHeight());
			}

			articleTitle.close();
			
			if (drawDebug) {
				// margin
				PDStreamUtils.rect(tableContentStream, margin, yStart, width, headerBottomMargin, Color.CYAN);
			}
		}

		if (drawHeaderMargin) {
			yStart -= headerBottomMargin;
		}
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
		ensureStreamIsOpen();
		
		for (Row<T> row : rows) {
			if (row == header) {
				// check if header row height and first data row height can fit the page
				// if not draw them on another side
				float headerHeightIncludingFirstDataRow = header.getHeight() + rows.get(1).getHeight();
				if (isEndOfPage(headerHeightIncludingFirstDataRow)) {
					pageBreak();
				}
			}
			drawRow(row);
		}

		endTable();
		return yStart;
	}

	private void drawRow(Row<T> row) throws IOException {
		// if it is not header row or first row in the table then remove row's top border
		if (row != header && row != rows.get(0)) {
			row.removeTopBorders();
        }
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

			// insert page break
			pageBreak();

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

	/**
	 * <p>
	 * Method to switch between the {@link PageProvider} and the abstract method
	 * {@link Table#createPage()}, preferring the {@link PageProvider}.
	 * </p>
	 * <p>
	 * Will be removed once {@link #createPage()} is removed.
	 * </p>
	 * 
	 * @return
	 */
	private T createNewPage() {
		if (pageProvider != null) {
			return pageProvider.createPage();
		}

		return createPage();
	}

	/**
	 * @deprecated Use a {@link PageProvider} instead
	 * @return
	 */
	@Deprecated
	// remove also createNewPage()
	protected T createPage() {
		throw new IllegalStateException(
				"You either have to provide a " + PageProvider.class.getCanonicalName() + " or override this method");
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
					- FontUtils.getDescent(cell.getFont(), cell.getFontSize())
					- (cell.getTopBorder() == null ? 0 : cell.getTopBorder().getWidth());
			;

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
			cursorX += cell.getLeftPadding() + (cell.getLeftBorder() == null ? 0 : cell.getLeftBorder().getWidth());;

			// remember this horizontal position, as it is the anchor for each
			// new line
			float lineStartX = cursorX;

			// font settings
			this.tableContentStream.setFont(cell.getFont(), cell.getFontSize());

			// if it is head row or if it is header cell then please use bold
			// font
			if (row.equals(header) || cell.isHeaderCell()) {
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
		float xEnd = row.xEnd();

		Iterator<Cell<T>> cellIterator = row.getCells().iterator();
		while (cellIterator.hasNext()) {
			Cell<T> cell = cellIterator.next();

			fillCellColor(cell, yStart, xStart, cellIterator);

			drawCellBorders(row, cell, xStart, xEnd);

			xStart += getWidth(cell, cellIterator);
		}

	}

	private void drawCellBorders(Row<T> row, Cell<T> cell, float xStart, float xEnd) throws IOException {

		float yEnd = yStart - row.getHeight();

		// top
		LineStyle topBorder = cell.getTopBorder();
		if (topBorder != null) {
			float y = yStart - topBorder.getWidth() / 2;
			drawLine(xStart, y, xStart + cell.getWidth(), y, topBorder);
		}

		// right
		LineStyle rightBorder = cell.getRightBorder();
		if (rightBorder != null) {
			float x = xStart + cell.getWidth() - rightBorder.getWidth() / 2;
			drawLine(x, yStart - (topBorder == null ? 0 : topBorder.getWidth()), x, yEnd, rightBorder);
		}

		// bottom
		LineStyle bottomBorder = cell.getBottomBorder();
		if (bottomBorder != null) {
			float y = yEnd + bottomBorder.getWidth() / 2;
			drawLine(xStart, y, xStart + cell.getWidth() - (rightBorder == null ? 0 : rightBorder.getWidth()), y,
					bottomBorder);
		}

		// left
		LineStyle leftBorder = cell.getLeftBorder();
		if (leftBorder != null) {
			float x = xStart + leftBorder.getWidth() / 2;
			drawLine(x, yStart, x, yEnd + (bottomBorder == null ? 0 : bottomBorder.getWidth()), leftBorder);
		}

	}

	private void drawLine(float xStart, float yStart, float xEnd, float yEnd, LineStyle border) throws IOException {
		PDStreamUtils.setLineStyles(tableContentStream, border);
		tableContentStream.moveTo(xStart, yStart);
		tableContentStream.lineTo(xEnd, yEnd);
		tableContentStream.stroke();
		tableContentStream.closePath();
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
			width = cell.getExtraWidth();
		}
		return width;
	}
	
	private void ensureStreamIsOpen() throws IOException {
		if (tableContentStream == null) {
			tableContentStream = createPdPageContentStream();
		}
	}

	private void endTable() throws IOException {
		this.tableContentStream.close();
	}

	public T getCurrentPage() {
		checkNotNull(this.currentPage, "No current page defined.");
		return this.currentPage;
	}

	private boolean isEndOfPage(Row<T> row) {
		float currentY = yStart - row.getHeight();
		boolean isEndOfPage = currentY <= pageBottomMargin;
		if (isEndOfPage) {
			setTableIsBroken(true);
			System.out.println("Its end of page. Table row height caused the problem.");
		}

		// If we are closer than bottom margin, consider this as
		// the end of the currentPage
		// If you add rows that are higher then bottom margin, this needs to be
		// checked
		// manually using getNextYPos
		return isEndOfPage;
	}

	private boolean isEndOfPage(float freeSpaceForPageBreak) {
		float currentY = yStart - freeSpaceForPageBreak;
		boolean isEndOfPage = currentY <= pageBottomMargin;
		if (isEndOfPage) {
			setTableIsBroken(true);
			System.out.println("Its end of the page. Table title caused this problem.");
		}
		return isEndOfPage;
	}

	private void pageBreak() throws IOException {
		tableContentStream.close();
		this.yStart = yStartNewPage - pageTopMargin;
		this.currentPage = createNewPage();
		this.tableContentStream = createPdPageContentStream();
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

	public boolean tableIsBroken() {
		return tableIsBroken;
	}
	
	public float getHeaderAndDataHeight() {
		return header == null ? 0 : header.getHeight() + rows.get(header == null ? 0 : 1).getHeight();
	}
	

	public void setTableIsBroken(boolean tableIsBroken) {
		this.tableIsBroken = tableIsBroken;
	}

}
