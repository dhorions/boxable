package be.quodlibet.boxable;

import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class Table {
	public List<Row> header;
	public List<Row> rows;
	private float margin;
	private float width;
	private float pageTopMargin;
	private float pageBottomMargin;
	private float yStart;
	private float yStartNewPage;
	private boolean drawLines;
	private boolean drawContent;
	
	private Table(Builder builder) {
		this.header = builder.header;
		this.rows = builder.rows;
		this.margin = builder.margin;
		this.width = builder.width;
		this.pageTopMargin = builder.pageTopMargin;
		this.pageBottomMargin = builder.pageBottomMargin;
		this.yStart = builder.yStart;
		this.yStartNewPage = builder.yStartNewPage;
		this.drawLines = builder.drawLines;
		this.drawContent = builder.drawContent;
	}
	
	/**
	 * Builder for the Table class.
	 * 
	 * Fields:
	 * margin: horizontal margin size
	 * width: table width
	 * pageTopMargin: top margin of table
	 * pageBottomMargin: bottom margin of table
	 * yStart: Y position where table will start
	 * yStartNewPage: Y position where possible new page of table will start
	 * drawLines: draw table borders
	 * drawContent: draw table content
	 * 
	 * Example:
	 * Table table = new Table.Builder.margin(12).pageBottomMargin(5).build();
	 * This will create a table using the default settings but with custom margins.
	 * @author Finn Welsford-Ackroyd
	 */
	public static class Builder {
		// this field is used to calculate the default options used below
		private final PDRectangle defaultDimensions = new PDPage().getMediaBox();
		// These are the default values for a basic table. You can modify any of them by
		// using the builder's methods.
		private List<Row> header = new ArrayList<Row>();
		private List<Row> rows = new ArrayList<Row>();
		private float margin = 10;
		private float width = defaultDimensions.getWidth() - (2 * margin);
		private float pageTopMargin = 2*margin;
		private float pageBottomMargin = 70;
		private float yStart = defaultDimensions.getHeight() - (2 * margin);
		private float yStartNewPage = yStart;
		private boolean drawLines = true;
		private boolean drawContent = true;
		
		public Builder rows(List<Row> rows){
			this.rows = rows;
			return this;
		}
		
		public Builder header(List<Row> header){
			this.header = header;
			return this;
		}
		
		public Builder margin(float margin) {
			this.margin = margin;
			return this;
		}
		
		public Builder width(float width) {
			this.width = width;
			return this;
		}
		
		public Builder pageTopMargin(float pageTopMargin) {
			this.pageTopMargin = pageTopMargin;
			return this;
		}
		
		public Builder pageBottomMargin(float pageBottomMargin) {
			this.pageBottomMargin = pageBottomMargin;
			return this;
		}
		
		public Builder yStart(float yStart) {
			this.yStart = yStart;
			return this;
		}
		
		public Builder yStartNewPage(float yStartNewPage) {
			this.yStartNewPage = yStartNewPage;
			return this;
		}
		
		public Builder drawLines(boolean drawLines) {
			this.drawLines = drawLines;
			return this;
		}
		
		public Builder drawContent(boolean drawContent) {
			this.drawContent = drawContent;
			return this;
		}
		
		public Table build() {
			return new Table(this);
		}
	}
	
	public Row createRow(float height) {
		Row row = new Row(this, height);
		rows.add(row);
		return row;
	}

	public Row createRow(List<Cell> cells, float height) {
		Row row = new Row(this, cells, height);
		rows.add(row);
		return row;
	}
	
	/**
	 * @param row
	 *            The row that would be added as table's header row
	 */
	public void addHeaderRow(Row row) {
		row.setHeaderRow(true);
		header.add(row);
	}
	
	// Getters and setters

	public List<Row> getHeader() {
		return header;
	}

	public void setHeader(List<Row> header) {
		this.header = header;
	}

	public List<Row> getRows() {
		return rows;
	}

	public void setRows(List<Row> rows) {
		this.rows = rows;
	}

	public float getMargin() {
		return margin;
	}

	public void setMargin(float margin) {
		this.margin = margin;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getPageTopMargin() {
		return pageTopMargin;
	}

	public void setPageTopMargin(float pageTopMargin) {
		this.pageTopMargin = pageTopMargin;
	}

	public float getPageBottomMargin() {
		return pageBottomMargin;
	}

	public void setPageBottomMargin(float pageBottomMargin) {
		this.pageBottomMargin = pageBottomMargin;
	}

	public float getyStart() {
		return yStart;
	}

	public void setyStart(float yStart) {
		this.yStart = yStart;
	}

	public float getyStartNewPage() {
		return yStartNewPage;
	}

	public void setyStartNewPage(float yStartNewPage) {
		this.yStartNewPage = yStartNewPage;
	}

	public boolean isDrawLines() {
		return drawLines;
	}

	public void setDrawLines(boolean drawLines) {
		this.drawLines = drawLines;
	}

	public boolean isDrawContent() {
		return drawContent;
	}

	public void setDrawContent(boolean drawContent) {
		this.drawContent = drawContent;
	}
}