
/*
 Quodlibet.be
 */
package be.quodlibet.boxable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.util.Matrix;

import be.quodlibet.boxable.line.LineStyle;
import be.quodlibet.boxable.page.PageProvider;
import be.quodlibet.boxable.text.Token;
import be.quodlibet.boxable.text.WrappingFunction;
import be.quodlibet.boxable.utils.FontUtils;
import be.quodlibet.boxable.utils.PDStreamUtils;

public abstract class Table<T extends PDPage> {

	public final PDDocument document;
	private float margin;

	private T currentPage;
	private PDPageContentStream tableContentStream;
	private List<PDOutlineItem> bookmarks;
	private List<Row<T>> header = new ArrayList<>();
	private List<Row<T>> rows = new ArrayList<>();

	private final float yStartNewPage;
	private float yStart;
	private final float width;
	private final boolean drawLines;
	private final boolean drawContent;
	private float headerBottomMargin = 4f;
	private float lineSpacing = 1f;

	private boolean tableIsBroken = false;
	private boolean tableStartedAtNewPage = false;
	private boolean removeTopBorders = false;

	private PageProvider<T> pageProvider;

	// page margins
	private final float pageTopMargin;
	private final float pageBottomMargin;

	private boolean drawDebug;

	/**
	 * @deprecated Use one of the constructors that pass a {@link PageProvider}
	 * @param yStart Y position where {@link Table} will start
	 * @param yStartNewPage Y position where possible new page of {@link Table} will start
	 * @param pageBottomMargin bottom margin of {@link Table}
	 * @param width {@link Table} width
	 * @param margin {@link Table} margin
	 * @param document {@link PDDocument} where {@link Table} will be drawn
	 * @param currentPage current page where {@link Table} will be drawn (some tables are big and can be through multiple pages)
	 * @param drawLines draw {@link Table}'s borders
	 * @param drawContent draw {@link Table}'s content
	 * @throws IOException if fonts are not loaded correctly
	 */
	@Deprecated
	public Table(float yStart, float yStartNewPage, float pageBottomMargin, float width, float margin,
			PDDocument document, T currentPage, boolean drawLines, boolean drawContent) throws IOException {
		this(yStart, yStartNewPage, 0, pageBottomMargin, width, margin, document, currentPage, drawLines, drawContent,
				null);
	}

	/**
	 * @deprecated Use one of the constructors that pass a {@link PageProvider}
	 * @param yStartNewPage Y position where possible new page of {@link Table} will start
	 * @param pageBottomMargin bottom margin of {@link Table}
	 * @param width {@link Table} width
	 * @param margin {@link Table} margin
	 * @param document {@link PDDocument} where {@link Table} will be drawn
	 * @param drawLines draw {@link Table}'s borders
	 * @param drawContent draw {@link Table}'s content
	 * @throws IOException if fonts are not loaded correctly
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
		this.currentPage = pageProvider.nextPage();
	}

	protected abstract void loadFonts() throws IOException;

	protected PDType0Font loadFont(String fontPath) throws IOException {
		return FontUtils.loadFont(getDocument(), fontPath);
	}

	protected PDDocument getDocument() {
		return document;
	}

	public void drawTitle(String title, PDFont font, int fontSize, float tableWidth, float height, String alignment,
			float freeSpaceForPageBreak, boolean drawHeaderMargin) throws IOException {
		drawTitle(title, font, fontSize, tableWidth, height, alignment, freeSpaceForPageBreak, null, drawHeaderMargin);
	}

	public void drawTitle(String title, PDFont font, int fontSize, float tableWidth, float height, String alignment,
			float freeSpaceForPageBreak, WrappingFunction wrappingFunction, boolean drawHeaderMargin)
					throws IOException {

		ensureStreamIsOpen();

		if (isEndOfPage(freeSpaceForPageBreak)) {
			this.tableContentStream.close();
			pageBreak();
			tableStartedAtNewPage = true;
		}

		if (title == null) {
			// if you don't have title just use the height of maxTextBox in your "row"
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
		row.setLineSpacing(lineSpacing);
		this.rows.add(row);
		return row;
	}

	public Row<T> createRow(List<Cell<T>> cells, float height) {
		Row<T> row = new Row<T>(this, cells, height);
		row.setLineSpacing(lineSpacing);
		this.rows.add(row);
		return row;
	}

	public float draw() throws IOException {
		ensureStreamIsOpen();

		for (Row<T> row : rows) {
			if (header.contains(row)) {
				// check if header row height and first data row height can fit the page
				// if not draw them on another side
				if (isEndOfPage(getMinimumHeight())) {
					pageBreak();
					tableStartedAtNewPage = true;
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
			if(!isEndOfPage(row)){
				row.removeTopBorders();
			}
		}

		// draw the bookmark
		if (row.getBookmark() != null) {
			PDPageXYZDestination bookmarkDestination = new PDPageXYZDestination();
			bookmarkDestination.setPage(currentPage);
			bookmarkDestination.setTop((int) yStart);
			row.getBookmark().setDestination(bookmarkDestination);
			this.addBookmark(row.getBookmark());
		}

		// we want to remove the borders as often as possible
		removeTopBorders = true;

		if (isEndOfPage(row)) {

			// Draw line at bottom of table
			endTable();

			// insert page break
			pageBreak();

			// redraw all headers on each currentPage
			if (!header.isEmpty()) {
				for (Row<T> headerRow : header) {
					drawRow(headerRow);
				}
				// after you draw all header rows on next page please keep removing top borders to avoid double border drawing
				removeTopBorders = true;
			} else {
				// after a page break, we have to ensure that top borders get drawn
				removeTopBorders = false;
			}
		}
		// if it is first row in the table, we have to draw the top border
		if (row == rows.get(0)) {
			removeTopBorders = false;
		}

		if (removeTopBorders) {
			row.removeTopBorders();
		}

		// if it is header row or first row in the table, we have to draw the top border
		if (row == rows.get(0)) {
			removeTopBorders = false;
		}

		if (removeTopBorders) {
			row.removeTopBorders();
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
			return pageProvider.nextPage();
		}

		return createPage();
	}

	/**
	 * @deprecated Use a {@link PageProvider} instead
	 * @return new {@link PDPage}
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
		float cursorY;

		for (Cell<T> cell : row.getCells()) {
			// remember horizontal cursor position, so we can advance to the next cell easily later
			float cellStartX = cursorX;
			if (cell instanceof ImageCell) {
				final ImageCell<T> imageCell = (ImageCell<T>) cell;

				cursorY = yStart - cell.getTopPadding()
						- (cell.getTopBorder() == null ? 0 : cell.getTopBorder().getWidth());

				// image cell vertical alignment
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

				cursorX += cell.getLeftPadding() + (cell.getLeftBorder() == null ? 0 : cell.getLeftBorder().getWidth());

				// image cell horizontal alignment
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
				imageCell.getImage().draw(document, tableContentStream, cursorX, cursorY);

			} else {
				// no text without font
				if (cell.getFont() == null) {
					throw new IllegalArgumentException("Font is null on Cell=" + cell.getText());
				}

				// font settings
				this.tableContentStream.setFont(cell.getFont(), cell.getFontSize());

				if (cell.isTextRotated()) {
					// debugging mode - drawing (default!) padding of rotated cells
					//left
					//					PDStreamUtils.rect(tableContentStream, cursorX, yStart, 5, cell.getHeight(), Color.GREEN);
					//top
					//					PDStreamUtils.rect(tableContentStream, cursorX, yStart, cell.getWidth(), 5 , Color.GREEN);
					// bottom
					//					PDStreamUtils.rect(tableContentStream, cursorX, yStart - cell.getHeight(), cell.getWidth(), -5 , Color.GREEN);
					//right 
					//					PDStreamUtils.rect(tableContentStream, cursorX + cell.getWidth() - 5, yStart, 5, cell.getHeight(), Color.GREEN);

					cursorY = yStart - cell.getInnerHeight() - cell.getTopPadding()
							- (cell.getTopBorder() != null ? cell.getTopBorder().getWidth() : 0);

					switch (cell.getAlign()) {
					case CENTER:
						cursorY += cell.getVerticalFreeSpace() / 2;
						break;
					case LEFT:
						break;
					case RIGHT:
						cursorY += cell.getVerticalFreeSpace();
						break;
					}
					// respect left padding and descend by font height to get position of the base line
					cursorX += cell.getLeftPadding()
							+ (cell.getLeftBorder() == null ? 0 : cell.getLeftBorder().getWidth())
							+ FontUtils.getHeight(cell.getFont(), cell.getFontSize())
							+ FontUtils.getDescent(cell.getFont(), cell.getFontSize());

					switch (cell.getValign()) {
					case TOP:
						break;
					case MIDDLE:
						cursorX += cell.getHorizontalFreeSpace() / 2;
						break;
					case BOTTOM:
						cursorX += cell.getHorizontalFreeSpace();
						break;
					}

				} else {
					// debugging mode - drawing (default!) padding of rotated cells
					//left
					//					PDStreamUtils.rect(tableContentStream, cursorX, yStart, 5, cell.getHeight(), Color.RED);
					//top
					//					PDStreamUtils.rect(tableContentStream, cursorX, yStart, cell.getWidth(), 5 , Color.RED);
					// bottom
					//					PDStreamUtils.rect(tableContentStream, cursorX, yStart - cell.getHeight(), cell.getWidth(), -5 , Color.RED);
					//right 
					//					PDStreamUtils.rect(tableContentStream, cursorX + cell.getWidth() - 5, yStart, 5, cell.getHeight(), Color.RED);

					// position at top of current cell descending by font height - font descent, because we are
					// positioning the base line here
					cursorY = yStart - cell.getTopPadding() - FontUtils.getHeight(cell.getFont(), cell.getFontSize())
							- FontUtils.getDescent(cell.getFont(), cell.getFontSize())
							- (cell.getTopBorder() == null ? 0 : cell.getTopBorder().getWidth());

					if (drawDebug) {
						// @formatter:off 
						// top padding
						PDStreamUtils.rect(tableContentStream, cursorX + (cell.getLeftBorder() == null ? 0 : cell.getLeftBorder().getWidth()), yStart - (cell.getTopBorder() == null ? 0 : cell.getTopBorder().getWidth()), cell.getWidth() - (cell.getLeftBorder() == null ? 0 : cell.getLeftBorder().getWidth()) - (cell.getRightBorder() == null ? 0 : cell.getRightBorder().getWidth()), cell.getTopPadding(), Color.RED);
						// bottom padding
						PDStreamUtils.rect(tableContentStream, cursorX + (cell.getLeftBorder() == null ? 0 : cell.getLeftBorder().getWidth()), yStart - cell.getHeight() +  (cell.getBottomBorder() == null ? 0 : cell.getBottomBorder().getWidth()) + cell.getBottomPadding(), cell.getWidth() - (cell.getLeftBorder() == null ? 0 : cell.getLeftBorder().getWidth()) - (cell.getRightBorder() == null ? 0 : cell.getRightBorder().getWidth()), cell.getBottomPadding(), Color.RED);
						// left padding
						PDStreamUtils.rect(tableContentStream, cursorX + (cell.getLeftBorder() == null ? 0 : cell.getLeftBorder().getWidth()), yStart - (cell.getTopBorder() == null ? 0 : cell.getTopBorder().getWidth()), cell.getLeftPadding(), cell.getHeight() - (cell.getTopBorder() == null ? 0 : cell.getTopBorder().getWidth()) - (cell.getBottomBorder() == null ? 0 : cell.getBottomBorder().getWidth()), Color.RED);
						// right padding
						PDStreamUtils.rect(tableContentStream, cursorX + cell.getWidth() - (cell.getRightBorder() == null ? 0 : cell.getRightBorder().getWidth()) , yStart - (cell.getTopBorder() == null ? 0 : cell.getTopBorder().getWidth()), -cell.getRightPadding(), cell.getHeight() - (cell.getTopBorder() == null ? 0 : cell.getTopBorder().getWidth()) - (cell.getBottomBorder() == null ? 0 : cell.getBottomBorder().getWidth()), Color.RED);
						// @formatter:on 
					}
					
					
					// respect left padding
					cursorX += cell.getLeftPadding()
							+ (cell.getLeftBorder() == null ? 0 : cell.getLeftBorder().getWidth());

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
					
				}

				// remember this horizontal position, as it is the anchor for each
				// new line
				float lineStartX = cursorX;
				float lineStartY = cursorY;
				
				// if it is head row or if it is header cell then please use bold font
				if (row.equals(header) || cell.isHeaderCell()) {
					this.tableContentStream.setFont(cell.getParagraph().getFont(true, false), cell.getFontSize());
				}
				this.tableContentStream.setNonStrokingColor(cell.getTextColor());

				int italicCounter = 0;
				int boldCounter = 0;

				// print all lines of the cell
				for (Map.Entry<Integer, List<Token>> entry : cell.getParagraph().getMapLineTokens().entrySet()) {

					// calculate the width of this line
					float freeSpaceWithinLine = cell.getParagraph().getMaxLineWidth()
							- cell.getParagraph().getLineWidth(entry.getKey());
					//TODO: need to implemented rotated text yo!
					if (cell.isTextRotated()) {
						cursorY = lineStartY;
						switch (cell.getAlign()) {
						case CENTER:
							cursorY += freeSpaceWithinLine / 2;
							break;
						case LEFT:
							break;
						case RIGHT:
							cursorY += freeSpaceWithinLine;
							break;
						}
					} else {
						cursorX = lineStartX;
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
					}

					// iterate through tokens in current line
					PDFont currentFont = cell.getParagraph().getFont(false, false);
					for (Token token : entry.getValue()) {
						switch (token.getType()) {
						case OPEN_TAG:
							if ("b".equals(token.getData())) {
								boldCounter++;
							} else if ("i".equals(token.getData())) {
								italicCounter++;
							}
							break;
						case CLOSE_TAG:
							if ("b".equals(token.getData())) {
								boldCounter = Math.max(boldCounter - 1, 0);
							} else if ("i".equals(token.getData())) {
								italicCounter = Math.max(italicCounter - 1, 0);
							}
							break;
						case PADDING:
							cursorX += Float.parseFloat(token.getData());
							break;
						case ORDERING:
							this.tableContentStream.beginText();
							currentFont = cell.getParagraph().getFont(boldCounter > 0, italicCounter > 0);
							this.tableContentStream.setFont(currentFont, cell.getFontSize());
							if (cell.isTextRotated()) {
								final AffineTransform transform = AffineTransform.getTranslateInstance(cursorX,
										cursorY);
								transform.concatenate(AffineTransform.getRotateInstance(Math.PI * 0.5f));
								transform.concatenate(AffineTransform.getTranslateInstance(-cursorX, -cursorY));
								tableContentStream.setTextMatrix(new Matrix(transform));
								tableContentStream.newLineAtOffset(cursorX, cursorY);
								this.tableContentStream.showText(token.getData());
								this.tableContentStream.endText();
								this.tableContentStream.closePath();
								cursorY += currentFont.getStringWidth(token.getData()) / 1000 * cell.getFontSize();
							} else {
								this.tableContentStream.newLineAtOffset(cursorX, cursorY);
								this.tableContentStream.showText(token.getData());
								this.tableContentStream.endText();
								this.tableContentStream.closePath();
								cursorX += currentFont.getStringWidth(token.getData()) / 1000 * cell.getFontSize();
							}
							break;
						case BULLET:
							// if cell is not left aligned then don't draw the bullet
							if(!cell.getAlign().equals(HorizontalAlignment.LEFT)){
								continue;
							}
							if (cell.isTextRotated()) {
								// move cursorX up because bullet needs to be in the middle of font height
								cursorX += FontUtils.getHeight(currentFont, cell.getFontSize()) / 2;
								PDStreamUtils.rect(tableContentStream, cursorX, cursorY,
										currentFont.getStringWidth(token.getData()) / 1000 * cell.getFontSize(),
										currentFont.getStringWidth(" ") / 1000 * cell.getFontSize(),
										cell.getTextColor());
								// move cursorY for two characters (one for bullet, one for space after bullet)
								cursorY += 2 * currentFont.getStringWidth(" ") / 1000 * cell.getFontSize();
								// return cursorY to his original place
								cursorX -= FontUtils.getHeight(currentFont, cell.getFontSize()) / 2;
							} else {
								// move cursorY up because bullet needs to be in the middle of font height
								cursorY += FontUtils.getHeight(currentFont, cell.getFontSize()) / 2;
								PDStreamUtils.rect(tableContentStream, cursorX, cursorY,
										currentFont.getStringWidth(token.getData()) / 1000 * cell.getFontSize(),
										currentFont.getStringWidth(" ") / 1000 * cell.getFontSize(),
										cell.getTextColor());
								// move cursorX for two characters (one for bullet, one for space after bullet)
								cursorX += 2 * currentFont.getStringWidth(" ") / 1000 * cell.getFontSize();
								// return cursorY to his original place
								cursorY -= FontUtils.getHeight(currentFont, cell.getFontSize()) / 2;
							}
							break;
						case TEXT:
							this.tableContentStream.beginText();
							currentFont = cell.getParagraph().getFont(boldCounter > 0, italicCounter > 0);
							this.tableContentStream.setFont(currentFont, cell.getFontSize());
							if (cell.isTextRotated()) {
								final AffineTransform transform = AffineTransform.getTranslateInstance(cursorX,
										cursorY);
								transform.concatenate(AffineTransform.getRotateInstance(Math.PI * 0.5f));
								transform.concatenate(AffineTransform.getTranslateInstance(-cursorX, -cursorY));
								tableContentStream.setTextMatrix(new Matrix(transform));
								tableContentStream.newLineAtOffset(cursorX, cursorY);
								this.tableContentStream.showText(token.getData());
								this.tableContentStream.endText();
								this.tableContentStream.closePath();
								cursorY += currentFont.getStringWidth(token.getData()) / 1000 * cell.getFontSize();
							} else {
								try {
									this.tableContentStream.newLineAtOffset(cursorX, cursorY);
									this.tableContentStream.showText(token.getData());
									this.tableContentStream.endText();
									this.tableContentStream.closePath();
									cursorX += currentFont.getStringWidth(token.getData()) / 1000 * cell.getFontSize();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							break;
						}
					}
					if (cell.isTextRotated()) {
						cursorX = cursorX + cell.getParagraph().getFontHeight()* cell.getLineSpacing();
					} else {
						cursorY = cursorY - cell.getParagraph().getFontHeight()* cell.getLineSpacing();
					}
				}
			}
			// set cursor to the start of this cell plus its width to advance to the next cell
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

		if (cell.getFillColor() != null) {
			this.tableContentStream.setNonStrokingColor(cell.getFillColor());

			// y start is bottom pos
			yStart = yStart - cell.getHeight();
			float height = cell.getHeight() - (cell.getTopBorder() == null ? 0 : cell.getTopBorder().getWidth());

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

	/**
	 * /**
	 * 
	 * @deprecated Use {@link #addHeaderRow(Row)} instead, as it supports
	 *             multiple header rows
	 * @param header row that will be set as table's header row
	 */
	@Deprecated
	public void setHeader(Row<T> header) {
		this.header.clear();
		addHeaderRow(header);
	}

	/**
	 * <p>
	 * Calculate height of all table cells (essentially, table height).
	 * </p>
	 * <p>
	 * IMPORTANT: Doesn't acknowledge possible page break. Use with caution.
	 * </p>
	 * 
	 * @return {@link Table}'s height
	 */
	public float getHeaderAndDataHeight() {
		float height = 0;
		for (Row<T> row : rows) {
			height += row.getHeight();
		}
		return height;
	}

	/**
	 * <p>
	 * Calculates minimum table height that needs to be drawn (all header rows +
	 * first data row heights).
	 * </p>
	 * 
	 * @return height
	 */
	public float getMinimumHeight() {
		float height = 0.0f;
		int firstDataRowIndex = 0;
		if (!header.isEmpty()) {
			for (Row<T> headerRow : header) {
				// count all header rows height
				height += headerRow.getHeight();
				firstDataRowIndex++;
			}
		}

		if (rows.size() > firstDataRowIndex) {
			height += rows.get(firstDataRowIndex).getHeight();
		}

		return height;
	}

	/**
	 * <p>
	 * Setting current row as table header row
	 * </p>
	 * 
	 * @param row The row that would be added as table's header row
	 */
	public void addHeaderRow(Row<T> row) {
		this.header.add(row);
		row.setHeaderRow(true);
	}

	/**
	 * <p>
	 * Retrieves last table's header row
	 * </p>
	 * 
	 * @return header row
	 */
	public Row<T> getHeader() {
		if (header == null) {
			throw new IllegalArgumentException("Header Row not set on table");
		}
		
		return header.get(header.size() - 1);
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

	public void setTableIsBroken(boolean tableIsBroken) {
		this.tableIsBroken = tableIsBroken;
	}

	public List<Row<T>> getRows() {
		return rows;
	}

	public boolean tableStartedAtNewPage() {
		return tableStartedAtNewPage;
	}

	public float getLineSpacing() {
		return lineSpacing;
	}

	public void setLineSpacing(float lineSpacing) {
		this.lineSpacing = lineSpacing;
	}

}
