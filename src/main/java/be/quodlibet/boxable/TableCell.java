package be.quodlibet.boxable;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.quodlibet.boxable.utils.FontUtils;

public class TableCell<T extends PDPage> extends Cell<T> {

	private final static Logger LOGGER = LoggerFactory.getLogger(FontUtils.class);

	private final String tableData;
	private final float width;
	private float yStart;
	private float xStart;
	private float height = 0;
	private final PDDocument doc;
	private final PDPage page;
	// default paddings
	private float leftPadding = 5f;
	private float rightPadding = 5f;
	// page margins
	private final float pageTopMargin;
	private final float pageBottomMargin;
	
	TableCell(Row<T> row, float width, String tableData, boolean isCalculated, PDDocument doc, PDPage page,
			float yStart, float pageTopMargin, float pageBottomMargin) {
		super(row, width, tableData, isCalculated);
		this.tableData = tableData;
		this.width = width * row.getWidth() / 100;
		this.doc = doc;
		this.page = page;
		this.yStart = yStart;
		this.pageTopMargin = pageTopMargin;
		this.pageBottomMargin = pageBottomMargin;
		fillTable();
	}

	/**
	 * <p>
	 * This method just fills up the table's content for proper table cell
	 * height calculation. Position of the table (x,y) is not relevant here.
	 * </p>
	 */
	public void fillTable() {
		try {
			// please consider the cell's paddings
			float tableWidth = this.width - leftPadding - rightPadding;
			BaseTable table = new BaseTable(yStart, PDRectangle.A4.getHeight() - pageTopMargin, pageTopMargin, pageBottomMargin,
					tableWidth, 10, doc, page, true, true);
			Document document = Jsoup.parse(tableData);
			Element htmlTable = document.select("table").first();
			Elements rows = htmlTable.select("tr");
			for (Element htmlTableRow : rows) {
				Row<PDPage> row = table.createRow(0);
				Elements cols = htmlTableRow.select("td");
				for (Element col : cols) {
					row.createCell(tableWidth / cols.size() / row.getWidth() * 100, col.text());
				}
				yStart -= row.getHeight();
			}
			this.height = table.getHeaderAndDataHeight();
		} catch (IOException e) {
			LOGGER.warn("Cannot create table in TableCell. Table data: '{}' " + tableData + e);
		}
	}

	/**
	 * <p>
	 * This method draw table cell with proper X,Y position which are determined in
	 * {@link Table#draw()} method
	 * </p>
	 * 
	 * @param page {@link PDPage} where table cell be written on
	 */
	public void draw(PDPage page) {
		try {
			// please consider the cell's paddings
			float tableWidth = this.width - leftPadding - rightPadding;
			// top and bottom table's margin are determined by cell's top and
			// bottom padding
			BaseTable table = new BaseTable(yStart, PDRectangle.A4.getHeight() - pageTopMargin, pageTopMargin, pageBottomMargin,
					tableWidth, xStart, doc, page, true, true);
			Document document = Jsoup.parse(tableData);
			Element htmlTable = document.select("table").first();
			Elements rows = htmlTable.select("tr");
			for (Element htmlTableRow : rows) {
				Row<PDPage> row = table.createRow(0);
				Elements cols = htmlTableRow.select("td");
				for (Element col : cols) {
					row.createCell(tableWidth / cols.size() / row.getWidth() * 100, col.text());
				}
			}
			this.height = table.getHeaderAndDataHeight();
			table.draw();
		} catch (IOException e) {
			LOGGER.warn("Cannot draw table for TableCell! Table data: '{}'" + tableData + e);
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
}