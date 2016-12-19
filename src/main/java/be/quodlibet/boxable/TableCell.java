package be.quodlibet.boxable;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.quodlibet.boxable.utils.FontUtils;

public class TableCell<T extends PDPage> extends Cell<T> {

	private final static Logger logger = LoggerFactory.getLogger(TableCell.class);

	private final String tableData;
	private final float width;
	private float yStart;
	private float xStart;
	private float height = 0;
	private final PDDocument doc;
	private final PDPage page;
	// keeping margin pattern as normal table
	private float marginBetweenElementsY = 15;
	private final HorizontalAlignment align;
	private final VerticalAlignment valign;
	// default FreeSans font
	private PDFont font =  FontUtils.getDefaultfonts().get("font");
	private PDFont fontBold = FontUtils.getDefaultfonts().get("fontBold");
	private PDPageContentStream tableCellContentStream;
	
	// default paddings
	private float leftPadding = 5f;
	private float rightPadding = 5f;
	// page margins
	private final float pageTopMargin;
	private final float pageBottomMargin;
	// default title fonts
	private int tableTitleFontSize = 10;
	
	TableCell(Row<T> row, float width, String tableData, boolean isCalculated, PDDocument document, PDPage page,
			float yStart, float pageTopMargin, float pageBottomMargin) {
		this(row, width, tableData, isCalculated, document, page, yStart, pageTopMargin, pageBottomMargin, HorizontalAlignment.LEFT, VerticalAlignment.TOP);
	}
	
	TableCell(Row<T> row, float width, String tableData, boolean isCalculated, PDDocument document, PDPage page,
			float yStart, float pageTopMargin, float pageBottomMargin, final HorizontalAlignment align, final VerticalAlignment valign) {
		super(row, width, tableData, isCalculated);
		this.tableData = tableData;
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
	 * <th>})
	 * </p>
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	public void fillTable() {
		try {
			// please consider the cell's paddings
			float tableWidth = this.width - leftPadding - rightPadding;
			tableCellContentStream = new PDPageContentStream(doc, page, true, true);
			Document document = Jsoup.parse(tableData);
			Element captionTag = document.select("caption").first();
			Paragraph tableTitle = null;
			String caption = "";
			if(captionTag != null){
				caption = captionTag.text();
				tableTitle = new Paragraph(caption, fontBold, tableTitleFontSize, tableWidth, align,
						null);
				yStart -= tableTitle.getHeight() + marginBetweenElementsY;
			}
						
			BaseTable table = new BaseTable(yStart, PDRectangle.A4.getHeight() - pageTopMargin, pageTopMargin,
					pageBottomMargin, tableWidth, xStart, doc, page, true, true);
			
			document.outputSettings().prettyPrint(false);
			Element htmlTable = document.select("table").first();
			
			Elements rows = htmlTable.select("tr");
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
				// calculate how much really columns do you have (including colspans!)
				for (Element col : tableHasHeaderColumns ? tableHeaderCols : tableCols) {
					if (col.attr("colspan") != null && !col.attr("colspan").isEmpty()) {
						columnsSize += Integer.parseInt(col.attr("colspan")) - 1;
					}
				}
				for (Element col : tableHasHeaderColumns ? tableHeaderCols : tableCols) {
					if (col.attr("colspan") != null && !col.attr("colspan").isEmpty()) {
						Cell<T> cell = (Cell<T>) row.createCell(
								tableWidth / columnsSize * Integer.parseInt(col.attr("colspan")) / row.getWidth() * 100,
								col.html().replace("&amp;", "&"));
					} else {
						Cell<T> cell = (Cell<T>) row.createCell(tableWidth / columnsSize / row.getWidth() * 100,
								col.html().replace("&amp;", "&"));
					}
				}
				yStart -= row.getHeight();
			}
			height = table.getHeaderAndDataHeight() + (captionTag != null ? tableTitle.getHeight() : 0) + marginBetweenElementsY;
			tableCellContentStream.close();
		} catch (IOException e) {
			logger.warn("Cannot create table in TableCell. Table data: '{}' " + tableData + e);
		}
	}
	
	/**
	 * <p>
	 * This method draw table cell with proper X,Y position which are determined
	 * in {@link Table#draw()} method
	 * </p>
	 * <p>
	 * NOTE: if entire row is not header row then use bold instead header cell (
	 * {@code
	 * <th>})
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
			float tableWidth = this.width - leftPadding - rightPadding;
			
			// draw table title first
			Document document = Jsoup.parse(tableData);
			Element tableCaptionTag = document.select("caption").first();
			String caption = "";
			Paragraph tableTitle = null;
			tableCellContentStream = new PDPageContentStream(doc, page, true, true);
			if(tableCaptionTag != null){
				caption = tableCaptionTag.text();
				tableTitle = new Paragraph(caption, fontBold, tableTitleFontSize, tableWidth, align,
						null);
				yStart = tableTitle.write(tableCellContentStream, xStart, yStart) - marginBetweenElementsY;
			}
			

			// top and bottom table's margin are determined by cell's top and
			// bottom padding
			BaseTable table = new BaseTable(yStart, PDRectangle.A4.getHeight() - pageTopMargin, pageTopMargin,
					pageBottomMargin, tableWidth, xStart, doc, page, true, true);
			document.outputSettings().prettyPrint(false);
			Element htmlTable = document.select("table").first();
			Elements rows = htmlTable.select("tr");
			for (Element htmlTableRow : rows) {
				Row<PDPage> row = table.createRow(0);
				Elements tableCols = htmlTableRow.select("td");
				Elements tableHeaderCols = htmlTableRow.select("th");
				// do we have header columns?
				boolean tableHasHeaderColumns = tableHeaderCols.isEmpty() ? false : true;
				if (tableHasHeaderColumns) {
					row.setHeaderRow(true);
				}
				int columnsSize = tableHasHeaderColumns ? tableHeaderCols.size() : tableCols.size();
				// calculate how much really columns do you have (including
				// colspans!)
				for (Element col : tableHasHeaderColumns ? tableHeaderCols : tableCols) {
					// FIXME: row can have mixed td and th cells
					if (col.attr("colspan") != null && !col.attr("colspan").isEmpty()) {
						columnsSize += Integer.parseInt(col.attr("colspan")) - 1;
					}
				}
				for (Element col : tableHasHeaderColumns ? tableHeaderCols : tableCols) {
					// TODO: enable horizontal/vertical alignments, background color, border color
					if (col.attr("colspan") != null && !col.attr("colspan").isEmpty()) {
						Cell<T> cell = (Cell<T>) row.createCell(
								tableWidth / columnsSize * Integer.parseInt(col.attr("colspan")) / row.getWidth() * 100,
								col.html().replace("&amp;", "&"));
					} else {
						Cell<T> cell = (Cell<T>) row.createCell(tableWidth / columnsSize / row.getWidth() * 100,
								col.html().replace("&amp;", "&"));
					}
				}
			}
			height = table.getHeaderAndDataHeight() + (tableCaptionTag != null ? tableTitle.getHeight() : 0) + marginBetweenElementsY ;
			table.draw();
			tableCellContentStream.close();
		} catch (IOException e) {
			logger.warn("Cannot draw table for TableCell! Table data: '{}'" + tableData + e);
		}
	}

	public float getXPosition() {
		return xStart;
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

	public void setLeftPadding(float leftPadding) {
		this.leftPadding = leftPadding;
	}

	public void setRightPadding(float rightPadding) {
		this.rightPadding = rightPadding;
	}

	public void setFont(PDFont font) {
		this.font = font;
	}

	public void setFontBold(PDFont fontBold) {
		this.fontBold = fontBold;
	}

}