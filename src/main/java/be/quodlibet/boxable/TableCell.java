package be.quodlibet.boxable;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.awt.Color;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.quodlibet.boxable.line.LineStyle;
import be.quodlibet.boxable.text.Token;
import be.quodlibet.boxable.utils.FontUtils;
import be.quodlibet.boxable.utils.ColorUtils;
import be.quodlibet.boxable.utils.PageContentStreamOptimized;
import be.quodlibet.boxable.utils.PDStreamUtils;

public class TableCell<T extends PDPage> extends Cell<T> {

	private final static Logger logger = LoggerFactory.getLogger(TableCell.class);

	private final String tableData;
	private final float width;
	private float yStart;
	private float xStart;
	private float height = 0;
	private final PDDocument doc;
	private final PDPage page;
	private float marginBetweenElementsY = FontUtils.getHeight(getFont(), getFontSize());
	private final HorizontalAlignment align;
	private final VerticalAlignment valign;


	private PageContentStreamOptimized tableCellContentStream;

	// page margins
	private final float pageTopMargin;
	private final float pageBottomMargin;
	// default title fonts
	private int tableTitleFontSize = 8;
	private boolean innerTableDrawLines = true;
	private boolean innerTableLeftBorder = true;
	private boolean innerTableRightBorder = true;
	private boolean innerTableTopBorder = true;
	private boolean innerTableBottomBorder = true;
	private boolean innerTableInnerVerticalBorders = true;
	private boolean innerTableInnerHorizontalBorders = true;
	private LineStyle innerTableBorderStyle = null;
	private boolean innerTableStartAtTop = false;
	private Float innerTableCellLeftPadding = null;
	private Float innerTableCellRightPadding = null;
	private Float innerTableCellTopPadding = null;
	private Float innerTableCellBottomPadding = null;

	private static final Safelist INNER_TABLE_SAFELIST = Safelist.none()
			.addTags("table", "thead", "tbody", "tfoot", "tr", "td", "th", "caption",
					"colgroup", "col", "p", "br", "b", "strong", "i", "em", "u", "ul", "ol", "li",
					"h1", "h2", "h3", "h4", "h5", "h6",
					"span", "sub", "sup")
			.addAttributes("td", "colspan", "rowspan", "style", "bgcolor")
			.addAttributes("th", "colspan", "rowspan", "style", "bgcolor")
			.addAttributes("col", "span")
			.addAttributes("colgroup", "span")
			.addAttributes("table", "border");

	TableCell(Row<T> row, float width, String tableData, boolean isCalculated, PDDocument document, PDPage page,
			float yStart, float pageTopMargin, float pageBottomMargin) {
		this(row, width, tableData, isCalculated, document, page, yStart, pageTopMargin, pageBottomMargin,
				HorizontalAlignment.LEFT, VerticalAlignment.TOP);
	}

	TableCell(Row<T> row, float width, String tableData, boolean isCalculated, PDDocument document, PDPage page,
			float yStart, float pageTopMargin, float pageBottomMargin, final HorizontalAlignment align,
			final VerticalAlignment valign) {
		super(row, width, tableData, isCalculated);
		this.tableData = sanitizeTableData(tableData);
		this.width = width * row.getWidth() / 100;
		this.doc = document;
		this.page = page;
		this.yStart = yStart;
		this.pageTopMargin = pageTopMargin;
		this.pageBottomMargin = pageBottomMargin;
		this.align = align;
		this.valign = valign;
		fillTable();
	}

	/**
	 * <p>
	 * This method just fills up the table's with her content for proper table
	 * cell height calculation. Position of the table (x,y) is not relevant
	 * here.
	 * </p>
	 * <p>
	 * NOTE: if entire row is not header row then use bold instead header cell (
	 * {@code
	 * 
	<th>})
	 * </p>
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	public void fillTable() {
		float originalYStart = yStart;
		try {
			// please consider the cell's paddings
			float tableWidth = this.width - getLeftPadding() - getRightPadding();
			tableCellContentStream = new PageContentStreamOptimized(new PDPageContentStream(doc, page,
					PDPageContentStream.AppendMode.APPEND, true));
			// check if there is some additional text outside inner table
			String[] outerTableText = tableData.split("<table");
			// don't forget to attach splited tag
			for (int i = 1; i < outerTableText.length; i++) {
				outerTableText[i] = "<table " + outerTableText[i];
			}
			Paragraph outerTextParagraph = null;
			String caption = "";
			height = 0;
			for (String element : outerTableText) {
				if (element.contains("</table")) {
					String[] chunks = element.split("</table>");
					for (String chunkie : chunks) {
						if (chunkie.contains("<table")) {
							// table title
							Document document = Jsoup.parse(chunkie);
							Element captionTag = document.select("caption").first();
							Paragraph tableTitle = null;
							if (captionTag != null) {
								caption = captionTag.text();
								tableTitle = new Paragraph(caption, getFontBold(), tableTitleFontSize, tableWidth,
										HorizontalAlignment.CENTER, null);
								yStart -= tableTitle.getHeight() + marginBetweenElementsY;
							}
							height += (captionTag != null ? tableTitle.getHeight() + marginBetweenElementsY : 0);
							createInnerTable(tableWidth, document, page, false, true);
						} else {
							// make paragraph and get tokens
							outerTextParagraph = new Paragraph(chunkie, getFont(), 8, (int) tableWidth);
							outerTextParagraph.getLines();
							height += (outerTextParagraph != null
									? outerTextParagraph.getHeight() + marginBetweenElementsY : 0);
							yStart = writeOrCalculateParagraph(outerTextParagraph, true);
						}
					}
				} else {
					// make paragraph and get tokens
					outerTextParagraph = new Paragraph(element, getFont(), 8, (int) tableWidth);
					outerTextParagraph.getLines();
					height += (outerTextParagraph != null ? outerTextParagraph.getHeight() + marginBetweenElementsY
							: 0);
					yStart = writeOrCalculateParagraph(outerTextParagraph, true);
				}
			}
			tableCellContentStream.close();
		} catch (IOException e) {
			logger.warn("Cannot create table in TableCell. Table data: '{}' " + tableData + e);
		} finally {
			yStart = originalYStart;
		}
	}

	private void createInnerTable(float tableWidth, Document document, PDPage currentPage, boolean drawTable,
			boolean useStartAtTop) throws IOException {

		float topBorderWidth = getTopBorder() == null ? 0 : getTopBorder().getWidth();
		float fontLineOffset = FontUtils.getHeight(getFont(), getFontSize())
				+ FontUtils.getDescent(getFont(), getFontSize());
		float startAtTopOffset = useStartAtTop && innerTableStartAtTop
				? fontLineOffset
				: getTopPadding() + topBorderWidth;
		float tableStartY = yStart + startAtTopOffset;
		BaseTable table = new BaseTable(tableStartY, PDRectangle.A4.getHeight() - pageTopMargin, pageTopMargin,
				pageBottomMargin, tableWidth, xStart, doc, currentPage, innerTableDrawLines, true);
				
		document.outputSettings().prettyPrint(false);
		Element htmlTable = document.select("table").first();
		if (htmlTable == null) {
			return;
		}

		Elements rows = htmlTable.select("tr");
		int rowIndex = 0;
		for (Element htmlTableRow : rows) {
			Row<PDPage> row = table.createRow(0);
			Elements tableCols = htmlTableRow.select("td");
			Elements tableHeaderCols = htmlTableRow.select("th");
			// do we have header columns?
			boolean tableHasHeaderColumns = tableHeaderCols.isEmpty() ? false : true;
			if (tableHasHeaderColumns) {
				// if entire row is not header row then use bold instead
				// header cell (<th>)
				row.setHeaderRow(true);
			}
			int columnsSize = tableHasHeaderColumns ? tableHeaderCols.size() : tableCols.size();
			// calculate how much really columns do you have (including
			// colspans!)
			for (Element col : tableHasHeaderColumns ? tableHeaderCols : tableCols) {
				int colSpan = parseColspan(col);
				columnsSize += colSpan - 1;
			}
			if (columnsSize <= 0) {
				columnsSize = 1;
			}
			
			int colPosition = 0;
			for (Element col : tableHasHeaderColumns ? tableHeaderCols : tableCols) {
				String cellHtml = Parser.unescapeEntities(col.html(), true);
				int colSpan = parseColspan(col);
				if (colSpan > columnsSize) {
					colSpan = columnsSize;
				}
				int startCol = colPosition;
				int endCol = Math.min(columnsSize - 1, colPosition + colSpan - 1);
				Cell<PDPage> cell;
				if (colSpan > 1) {
					cell = row.createCell(
							tableWidth / columnsSize * colSpan / row.getWidth() * 100,
							cellHtml);
				} else {
					cell = row.createCell(tableWidth / columnsSize / row.getWidth() * 100, cellHtml);
				}
				// inherit styles from the parent TableCell
				cell.setFont(getFont());
				cell.setFontBold(getFontBold());
				cell.setFontSize(getFontSize());
				cell.setTextColor(getTextColor());
				

				applyInnerTableBorderOptions(cell, rowIndex, startCol, endCol, rows.size(), columnsSize);

				// Apply HTML attributes (style, bgcolor) overrides
				applyHtmlAttributes(cell, col);
				
				colPosition += colSpan;
			}
			tableStartY -= row.getHeight();
			rowIndex++;
		}
		if (drawTable) {
			table.draw();
		}

		height += table.getHeaderAndDataHeight() + marginBetweenElementsY;
		float offset = startAtTopOffset;
		yStart = tableStartY - offset;
	}

	/**
	 * <p>
	 * Method provides writing or height calculation of possible outer text
	 * </p>
	 * 
	 * @param paragraph
	 *            Paragraph that needs to be written or whose height needs to be
	 *            calculated
	 * @param onlyCalculateHeight
	 *            if <code>true</code> the given paragraph will not be drawn
	 *            just his height will be calculated.
	 * @return Y position after calculating/writing given paragraph
	 */
	private float writeOrCalculateParagraph(Paragraph paragraph, boolean onlyCalculateHeight) throws IOException {
		int boldCounter = 0;
		int italicCounter = 0;
		float baseFontSize = getFontSize();
		float currentFontSize = baseFontSize;
		java.util.Stack<Float> headingFontSizeStack = new java.util.Stack<>();

		if (!onlyCalculateHeight) {
			tableCellContentStream.setRotated(isTextRotated());
		}

		// position at top of current cell descending by font height - font
		// descent, because we are positioning the base line here
		float firstLineFontSize = paragraph.getLineFontSize(0);
		float cursorY = yStart - getTopPadding() - FontUtils.getHeight(getFont(), firstLineFontSize)
				- FontUtils.getDescent(getFont(), firstLineFontSize) - (getTopBorder() == null ? 0 : getTopBorder().getWidth());
		float cursorX = xStart;

		// loop through tokens
		for (Map.Entry<Integer, List<Token>> entry : paragraph.getMapLineTokens().entrySet()) {

			// calculate the width of this line
			float freeSpaceWithinLine = paragraph.getMaxLineWidth() - paragraph.getLineWidth(entry.getKey());
			if (isTextRotated()) {
				switch (align) {
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
				switch (align) {
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
			PDFont currentFont = paragraph.getFont(false, false);
			for (Token token : entry.getValue()) {
				switch (token.getType()) {
				case OPEN_TAG:
					if (Paragraph.isHeadingTag(token.getData())) {
						headingFontSizeStack.push(currentFontSize);
						currentFontSize = baseFontSize * Paragraph.getHeadingScale(token.getData());
					} else if ("b".equals(token.getData())) {
						boldCounter++;
					} else if ("i".equals(token.getData())) {
						italicCounter++;
					}
					break;
				case CLOSE_TAG:
					if (Paragraph.isHeadingTag(token.getData())) {
						currentFontSize = headingFontSizeStack.isEmpty() ? baseFontSize : headingFontSizeStack.pop();
					} else if ("b".equals(token.getData())) {
						boldCounter = Math.max(boldCounter - 1, 0);
					} else if ("i".equals(token.getData())) {
						italicCounter = Math.max(italicCounter - 1, 0);
					}
					break;
				case PADDING:
					cursorX += Float.parseFloat(token.getData());
					break;
				case ORDERING:
					currentFont = paragraph.getFont(boldCounter > 0, italicCounter > 0);
					tableCellContentStream.setFont(currentFont, currentFontSize);
					if (isTextRotated()) {
						// if it is not calculation then draw it
						if (!onlyCalculateHeight) {
							tableCellContentStream.newLineAt(cursorX, cursorY);
							tableCellContentStream.showText(token.getData());
						}
						cursorY += token.getWidth(currentFont) / 1000 * currentFontSize;
					} else {
						// if it is not calculation then draw it
						if (!onlyCalculateHeight) {
							tableCellContentStream.newLineAt(cursorX, cursorY);
							tableCellContentStream.showText(token.getData());
						}
						cursorX += token.getWidth(currentFont) / 1000 * currentFontSize;
					}
					break;
				case BULLET:
					float widthOfSpace = currentFont.getSpaceWidth();
					float halfHeight = FontUtils.getHeight(currentFont, currentFontSize) / 2;
					if (isTextRotated()) {
						if (!onlyCalculateHeight) {
							PDStreamUtils.rect(tableCellContentStream, cursorX + halfHeight, cursorY,
									token.getWidth(currentFont) / 1000 * currentFontSize,
									widthOfSpace / 1000 * currentFontSize, getTextColor());
						}
						// move cursorY for two characters (one for bullet, one
						// for space after bullet)
						cursorY += 2 * widthOfSpace / 1000 * currentFontSize;
					} else {
						if (!onlyCalculateHeight) {
							PDStreamUtils.rect(tableCellContentStream, cursorX, cursorY + halfHeight,
									token.getWidth(currentFont) / 1000 * currentFontSize,
									widthOfSpace / 1000 * currentFontSize, getTextColor());
						}
						// move cursorX for two characters (one for bullet, one
						// for space after bullet)
						cursorX += 2 * widthOfSpace / 1000 * currentFontSize;
					}
					break;
				case TEXT:
					currentFont = paragraph.getFont(boldCounter > 0, italicCounter > 0);
					tableCellContentStream.setFont(currentFont, currentFontSize);
					if (isTextRotated()) {
						if (!onlyCalculateHeight) {
							tableCellContentStream.newLineAt(cursorX, cursorY);
							tableCellContentStream.showText(token.getData());
						}
						cursorY += token.getWidth(currentFont) / 1000 * currentFontSize;
					} else {
						if (!onlyCalculateHeight) {
							tableCellContentStream.newLineAt(cursorX, cursorY);
							tableCellContentStream.showText(token.getData());
						}
						cursorX += token.getWidth(currentFont) / 1000 * currentFontSize;
					}
					break;
				}
			}
			// reset
			cursorX = xStart;
			cursorY -= paragraph.getLineHeight(entry.getKey()) * paragraph.getLineSpacing();
		}
		return cursorY;
	}

	/**
	 * <p>
	 * This method draw table cell with proper X,Y position which are determined
	 * in {@link Table#draw()} method
	 * </p>
	 * <p>
	 * NOTE: if entire row is not header row then use bold instead header cell (
	 * {@code
	 * 
	<th>})
	 * </p>
	 * 
	 * @param page
	 *            {@link PDPage} where table cell be written on
	 * 
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	public void draw(PDPage page) {
		try {
			// please consider the cell's paddings
			float tableWidth = this.width - getLeftPadding() - getRightPadding();
			tableCellContentStream = new PageContentStreamOptimized(new PDPageContentStream(doc, page,
					PDPageContentStream.AppendMode.APPEND, true));
			// check if there is some additional text outside inner table
			String[] outerTableText = tableData.split("<table");
			// don't forget to attach splited tag
			for (int i = 1; i < outerTableText.length; i++) {
				outerTableText[i] = "<table " + outerTableText[i];
			}
			Paragraph outerTextParagraph = null;
			String caption = "";
			height = 0;
			for (String element : outerTableText) {
				if (element.contains("</table")) {
					String[] chunks = element.split("</table>");
					for (String chunkie : chunks) {
						if (chunkie.contains("<table")) {
							// table title
							Document document = Jsoup.parse(chunkie);
							Element captionTag = document.select("caption").first();
							Paragraph tableTitle = null;
							if (captionTag != null) {
								caption = captionTag.text();
								tableTitle = new Paragraph(caption, getFontBold(), tableTitleFontSize, tableWidth,
										HorizontalAlignment.CENTER, null);
								yStart = tableTitle.write(tableCellContentStream, xStart, yStart)
										- marginBetweenElementsY;
							}
							height += (captionTag != null ? tableTitle.getHeight() + marginBetweenElementsY : 0);
							createInnerTable(tableWidth, document, page, true, true);
						} else {
							// make paragraph and get tokens
							outerTextParagraph = new Paragraph(chunkie, getFont(), 8, (int) tableWidth);
							outerTextParagraph.getLines();
							height += (outerTextParagraph != null
									? outerTextParagraph.getHeight() + marginBetweenElementsY : 0);
							yStart = writeOrCalculateParagraph(outerTextParagraph, false);
						}
					}
				} else {
					// make paragraph and get tokens
					outerTextParagraph = new Paragraph(element, getFont(), 8, (int) tableWidth);
					outerTextParagraph.getLines();
					height += (outerTextParagraph != null ? outerTextParagraph.getHeight() + marginBetweenElementsY
							: 0);
					yStart = writeOrCalculateParagraph(outerTextParagraph, false);
				}
			}
			tableCellContentStream.close();
		} catch (IOException e) {
			logger.warn("Cannot draw table for TableCell! Table data: '{}'" + tableData + e);
		}
	}

	public float getXPosition() {
		return xStart;
	}

	/**
	 * <p>
	 * Controls whether borders are drawn for inner tables.
	 * </p>
	 *
	 * @param drawLines
	 *            {@code true} to draw borders
	 */
	public void setInnerTableDrawLines(boolean drawLines) {
		this.innerTableDrawLines = drawLines;
	}

	/**
	 * <p>
	 * Sets which sides should have borders for all inner table cells.
	 * </p>
	 *
	 * @param left
	 *            Draw left borders
	 * @param right
	 *            Draw right borders
	 * @param top
	 *            Draw top borders
	 * @param bottom
	 *            Draw bottom borders
	 */
	public void setInnerTableBorders(boolean left, boolean right, boolean top, boolean bottom) {
		this.innerTableLeftBorder = left;
		this.innerTableRightBorder = right;
		this.innerTableTopBorder = top;
		this.innerTableBottomBorder = bottom;
	}

	/**
	 * <p>
	 * Sets whether borders between inner table cells are drawn.
	 * </p>
	 *
	 * @param vertical
	 *            {@code true} to draw borders between columns
	 * @param horizontal
	 *            {@code true} to draw borders between rows
	 */
	public void setInnerTableInnerBorders(boolean vertical, boolean horizontal) {
		this.innerTableInnerVerticalBorders = vertical;
		this.innerTableInnerHorizontalBorders = horizontal;
	}

	/**
	 * <p>
	 * Sets the border style for all inner table cells.
	 * </p>
	 *
	 * @param borderStyle
	 *            Border style to apply, or {@code null} to keep defaults
	 */
	public void setInnerTableBorderStyle(LineStyle borderStyle) {
		this.innerTableBorderStyle = borderStyle;
	}

	/**
	 * <p>
	 * Sets padding for all inner table cells. Use {@code null} to keep defaults.
	 * </p>
	 *
	 * @param left
	 *            Left padding
	 * @param right
	 *            Right padding
	 * @param top
	 *            Top padding
	 * @param bottom
	 *            Bottom padding
	 */
	public void setInnerTableCellPadding(Float left, Float right, Float top, Float bottom) {
		this.innerTableCellLeftPadding = left;
		this.innerTableCellRightPadding = right;
		this.innerTableCellTopPadding = top;
		this.innerTableCellBottomPadding = bottom;
		fillTable();
	}

	/**
	 * <p>
	 * When enabled, inner tables start at the top border of the cell without extra
	 * whitespace above them.
	 * </p>
	 *
	 * @param startAtTop
	 *            {@code true} to align inner table to the top border
	 */
	public void setInnerTableStartAtTop(boolean startAtTop) {
		this.innerTableStartAtTop = startAtTop;
		fillTable();
	}

	/**
	 * <p>
	 * Sets the vertical margin between elements inside this table cell.
	 * Use {@code 0} to remove extra spacing above/below the inner table.
	 * </p>
	 *
	 * @param marginBetweenElementsY
	 *            Margin between elements in points
	 */
	public void setMarginBetweenElementsY(float marginBetweenElementsY) {
		this.marginBetweenElementsY = marginBetweenElementsY;
		fillTable();
	}

	public void setXPosition(float xStart) {
		this.xStart = xStart;
	}

	public float getYPosition() {
		return yStart;
	}

	public void setYPosition(float yStart) {
		this.yStart = yStart;
	}

	private void applyInnerTableBorderOptions(Cell<?> cell, int rowIndex, int startCol, int endCol,
			int rowCount, int columnCount) {
		if (innerTableBorderStyle != null) {
			cell.setBorderStyle(innerTableBorderStyle);
		}

		boolean isFirstRow = rowIndex == 0;
		boolean isLastRow = rowIndex == rowCount - 1;
		boolean isFirstCol = startCol == 0;
		boolean isLastCol = endCol == columnCount - 1;

		// Left border: only outer frame on first column
		if (isFirstCol) {
			if (!innerTableLeftBorder) {
				cell.setLeftBorderStyle(null);
			}
		} else {
			cell.setLeftBorderStyle(null);
		}

		// Right border: outer frame on last column, inner separators on internal columns
		if (isLastCol) {
			if (!innerTableRightBorder) {
				cell.setRightBorderStyle(null);
			}
		} else {
			if (!innerTableInnerVerticalBorders) {
				cell.setRightBorderStyle(null);
			}
		}

		// Top border: outer frame on first row, inner separators on other rows
		if (isFirstRow) {
			if (!innerTableTopBorder) {
				cell.setTopBorderStyle(null);
			}
		} else {
			if (!innerTableInnerHorizontalBorders) {
				cell.setTopBorderStyle(null);
			}
		}

		// Bottom border: only outer frame on last row
		if (isLastRow) {
			if (!innerTableBottomBorder) {
				cell.setBottomBorderStyle(null);
			}
		} else {
			cell.setBottomBorderStyle(null);
		}

		if (innerTableCellLeftPadding != null) {
			cell.setLeftPadding(innerTableCellLeftPadding);
		}
		if (innerTableCellRightPadding != null) {
			cell.setRightPadding(innerTableCellRightPadding);
		}
		if (innerTableCellTopPadding != null) {
			cell.setTopPadding(innerTableCellTopPadding);
		}
		if (innerTableCellBottomPadding != null) {
			cell.setBottomPadding(innerTableCellBottomPadding);
		}
	}

	private int parseColspan(Element col) {
		String value = col.attr("colspan");
		if (value == null || value.isEmpty()) {
			return 1;
		}
		try {
			int parsed = Integer.parseInt(value.trim());
			return parsed > 0 ? parsed : 1;
		} catch (NumberFormatException e) {
			return 1;
		}
	}

	/**
	 * <p>
	 * Sanitizes the provided HTML for safe inner table rendering. If no
	 * {@code <table>} element is found, the input is converted into a single-cell
	 * table containing the plain text content.
	 * </p>
	 *
	 * @param input raw HTML table content
	 * @return sanitized HTML containing a table
	 */
	private String sanitizeTableData(String input) {
		if (input == null || input.trim().isEmpty()) {
			return "<table></table>";
		}
		Document.OutputSettings outputSettings = new Document.OutputSettings().prettyPrint(false);
		String cleaned = Jsoup.clean(input, "", INNER_TABLE_SAFELIST, outputSettings);
		Document document = Jsoup.parse(cleaned);
		if (document.select("table").isEmpty()) {
			String textOnly = Jsoup.clean(input, Safelist.none());
			return "<table><tr><td>" + textOnly + "</td></tr></table>";
		}
		return cleaned;
	}

	@Override
	public float getTextHeight() {
		return height;
	}

	@Override
	public float getHorizontalFreeSpace() {
		return getInnerWidth() - width;
	}

	@Override
	public float getVerticalFreeSpace() {
		return getInnerHeight() - width;
	}

	private void applyHtmlAttributes(Cell<PDPage> cell, Element col) {
		// 1. Attributes
		String bgcolor = col.attr("bgcolor");
		if (hasValue(bgcolor)) {
			Color color = ColorUtils.parseColor(bgcolor);
			if (color != null) {
				cell.setFillColor(color);
			}
		}

		// 2. Style
		String style = col.attr("style");
		if (hasValue(style)) {
			String[] props = style.split(";");
			for (String prop : props) {
				String[] keyVal = prop.split(":");
				if (keyVal.length == 2) {
					String key = keyVal[0].trim().toLowerCase();
					String val = keyVal[1].trim();
					switch (key) {
					case "color":
						Color textColor = ColorUtils.parseColor(val);
						if (textColor != null) {
							cell.setTextColor(textColor);
						}
						break;
					case "background-color":
						Color fillColor = ColorUtils.parseColor(val);
						if (fillColor != null) {
							cell.setFillColor(fillColor);
						}
						break;
					case "border-color":
						Color borderColor = ColorUtils.parseColor(val);
						if (borderColor != null) {
							if (cell.getTopBorder() != null)
								cell.setTopBorderStyle(new LineStyle(borderColor, cell.getTopBorder().getWidth()));
							if (cell.getBottomBorder() != null)
								cell.setBottomBorderStyle(new LineStyle(borderColor, cell.getBottomBorder().getWidth()));
							if (cell.getLeftBorder() != null)
								cell.setLeftBorderStyle(new LineStyle(borderColor, cell.getLeftBorder().getWidth()));
							if (cell.getRightBorder() != null)
								cell.setRightBorderStyle(new LineStyle(borderColor, cell.getRightBorder().getWidth()));
						}
						break;
					case "font-family":
						PDFont font = getStandardFont(val);
						if (font != null) {
							cell.setFont(font);
						}
						break;
					}
				}
			}
		}
	}

	private boolean hasValue(String s) {
		return s != null && !s.isEmpty();
	}



	private PDFont getStandardFont(String fontName) {
		// Map common names to Standard14Fonts
		try {
			// Simplest is manual map or switch
			switch (fontName) {
			case "Helvetica":
				return new PDType1Font(Standard14Fonts.FontName.HELVETICA);
			case "Helvetica-Bold":
				return new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
			case "Helvetica-Oblique":
				return new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);
			case "Helvetica-BoldOblique":
				return new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD_OBLIQUE);
			case "Times-Roman":
				return new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);
			case "Times-Bold":
				return new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD);
			case "Times-Italic":
				return new PDType1Font(Standard14Fonts.FontName.TIMES_ITALIC);
			case "Times-BoldItalic":
				return new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD_ITALIC);
			case "Courier":
				return new PDType1Font(Standard14Fonts.FontName.COURIER);
			case "Courier-Bold":
				return new PDType1Font(Standard14Fonts.FontName.COURIER_BOLD);
			case "Courier-Oblique":
				return new PDType1Font(Standard14Fonts.FontName.COURIER_OBLIQUE);
			case "Courier-BoldOblique":
				return new PDType1Font(Standard14Fonts.FontName.COURIER_BOLD_OBLIQUE);
			case "Symbol":
				return new PDType1Font(Standard14Fonts.FontName.SYMBOL);
			case "ZapfDingbats":
				return new PDType1Font(Standard14Fonts.FontName.ZAPF_DINGBATS);
			default:
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

}