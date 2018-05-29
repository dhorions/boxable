
/*
 Quodlibet.be
 */
package be.quodlibet.boxable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

import be.quodlibet.boxable.html.TableCell;
import be.quodlibet.boxable.image.Image;
import be.quodlibet.boxable.image.ImageCell;

public class Row {

	private final Table table;
	PDOutlineItem bookmark;
	List<Cell> cells;
	private boolean headerRow = false;
	float height;
	float lineSpacing = 1;

	Row(Table table, List<Cell> cells, float height) {
		this.table = table;
		this.cells = cells;
		this.height = height;
	}

	Row(Table table, float height) {
		this.table = table;
		this.cells = new ArrayList<>();
		this.height = height;
	}

	/**
	 * <p>
	 * Creates a cell with provided width, cell value and default left top
	 * alignment
	 * </p>
	 * 
	 * @param width
	 *            Absolute width in points or in % of table width
	 * @param value
	 *            Cell's value (content)
	 * @return New {@link Cell}
	 */
	public Cell createCell(float width, String value) {
		Cell cell = new Cell(this, width, value, true);
		if (headerRow) {
			// set all cell as header cell
			cell.setHeaderCell(true);
		}
		setBorders(cell, cells.isEmpty());
		cell.setLineSpacing(lineSpacing);
		cells.add(cell);
		return cell;
	}

	/**
	 * <p>
	 * Creates an image cell with provided width and {@link Image}
	 * </p>
	 * 
	 * @param width
	 *            Cell's width
	 * @param img
	 *            {@link Image} in the cell
	 * @return {@link ImageCell}
	 */
	public ImageCell createImageCell(float width, Image img) {
		ImageCell cell = new ImageCell(this, width, img, true);
		setBorders(cell, cells.isEmpty());
		cells.add(cell);
		return cell;
	}

	public Cell createImageCell(float width, Image img, HorizontalAlignment align, VerticalAlignment valign) {
		Cell cell = new ImageCell(this, width, img, true, align, valign);
		setBorders(cell, cells.isEmpty());
		cells.add(cell);
		return cell;
	}

	/**
	 * <p>
	 * Creates a table cell with provided width and table data
	 * </p>
	 * 
	 * @param width
	 *            Table width
	 * @param tableData
	 *            Table's data (HTML table tags)
	 * @param doc
	 *            {@link PDDocument} where this table will be drawn
	 * @param page
	 *            {@link PDPage} where this table cell will be drawn
	 * @param yStart
	 *            Y position from which table will be drawn
	 * @param pageTopMargin
	 *            {@link TableCell}'s top margin
	 * @param pageBottomMargin
	 *            {@link TableCell}'s bottom margin
	 * @return {@link TableCell} with provided width and table data
	 */
	public TableCell createTableCell(float width, String tableData, PDDocument doc, PDPage page, float yStart,
			float pageTopMargin, float pageBottomMargin) {
		TableCell cell = new TableCell(this, width, tableData, true, doc, page, yStart, pageTopMargin,
				pageBottomMargin);
		setBorders(cell, cells.isEmpty());
		cells.add(cell);
		return cell;
	}

	/**
	 * <p>
	 * Creates a cell with provided width, cell value, horizontal and vertical
	 * alignment
	 * </p>
	 * 
	 * @param width
	 *            Absolute width in points or in % of table width
	 * @param value
	 *            Cell's value (content)
	 * @param align
	 *            Cell's {@link HorizontalAlignment}
	 * @param valign
	 *            Cell's {@link VerticalAlignment}
	 * @return New {@link Cell}
	 */
	public Cell createCell(float width, String value, HorizontalAlignment align, VerticalAlignment valign) {
		Cell cell = new Cell(this, width, value, true, align, valign);
		if (headerRow) {
			// set all cell as header cell
			cell.setHeaderCell(true);
		}
		setBorders(cell, cells.isEmpty());
		cell.setLineSpacing(lineSpacing);
		cells.add(cell);
		return cell;
	}

	/**
	 * <p>
	 * Creates a cell with the same width as the corresponding header cell
	 * </p>
	 *
	 * @param value
	 *            Cell's value (content)
	 * @return new {@link Cell}
	 */
	public Cell createCell(String value) {
		List<Row> headers = table.getHeader();
		float headerCellWidth = headers.get(headers.size()-1).getCells().get(cells.size()).getWidth();
		Cell cell = new Cell(this, headerCellWidth, value, false);
		setBorders(cell, cells.isEmpty());
		cells.add(cell);
		return cell;
	}

	/**
	 * <p>
	 * Remove left border to avoid double borders from previous cell's right
	 * border. In most cases left border will be removed.
	 * </p>
	 * 
	 * @param cell
	 *            {@link Cell}
	 * @param leftBorder
	 *            boolean for drawing cell's left border. If {@code true} then
	 *            the left cell's border will be drawn.
	 */
	private void setBorders(final Cell cell, final boolean leftBorder) {
		if (!leftBorder) {
			cell.setLeftBorderStyle(null);
		}
	}

	/**
	 * <p>
	 * remove top borders of cells to avoid double borders from cells in
	 * previous row
	 * </p>
	 */
	void removeTopBorders() {
		for (final Cell cell : cells) {
			cell.setTopBorderStyle(null);
		}
	}

	/**
	 * <p>
	 * Remove all borders of cells.
	 * </p>
	 */
	void removeAllBorders() {
		for (final Cell cell : cells) {
			cell.setBorderStyle(null);
			;
		}
	}

	/**
	 * <p>
	 * Gets maximal height of the cells in current row therefore row's height.
	 * </p>
	 * 
	 * @return Row's height
	 */
	public float getHeight() {
		float maxheight = 0.0f;
		for (Cell cell : this.cells) {
			float cellHeight = cell.getCellHeight();

			if (cellHeight > maxheight) {
				maxheight = cellHeight;
			}
		}

		if (maxheight > height) {
			this.height = maxheight;
		}
		return height;
	}

	public float getLineHeight() throws IOException {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public List<Cell> getCells() {
		return cells;
	}

	public int getColCount() {
		return cells.size();
	}

	public void setCells(List<Cell> cells) {
		this.cells = cells;
	}
	
	/*
	public float getWidth() {
		return table.getWidth();
	}
	*/

	public PDOutlineItem getBookmark() {
		return bookmark;
	}

	public void setBookmark(PDOutlineItem bookmark) {
		this.bookmark = bookmark;
	}

	protected float getLastCellExtraWidth() {
		float cellWidth = 0;
		for (Cell cell : cells) {
			cellWidth += cell.getWidth();
		}

		float lastCellExtraWidth = this.getWidth() - cellWidth;
		return lastCellExtraWidth;
	}

	public float getWidth() {
		return table.getWidth();
	}

	public boolean isHeaderRow() {
		return headerRow;
	}

	public void setHeaderRow(boolean headerRow) {
		this.headerRow = headerRow;
	}
	
	public float getLineSpacing() {
		return lineSpacing;
	}
	
	public void setLineSpacing(float f) {
		this.lineSpacing = f;
	}
}
