
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

import be.quodlibet.boxable.image.Image;

public class Row<T extends PDPage> {

	private final Table<T> table;
	PDOutlineItem bookmark;
	List<Cell<T>> cells;
	private boolean headerRow = false;
	private boolean fixedHeight = false;
	float height;
	private float lineSpacing = 1;
	private float wrapHeight = -1;

	Row(Table<T> table, List<Cell<T>> cells, float height) {
		this.table = table;
		this.cells = cells;
		this.height = height;
	}

	Row(Table<T> table, float height) {
		this.table = table;
		this.cells = new ArrayList<>();
		this.height = height;
	}

	/**
	 * <p>
	 * Creates a cell with provided width, cell value and default left top
	 * alignment.
	 * </p>
	 * 
	 * @param width
	 *            Absolute width in points or in % of table width (depending on
	 *            {@code isPercent})
	 * @param value
	 *            Cell's value (content)
	 * @param isPercent
	 *            If true, width is percentage of table width; otherwise absolute points
	 * @return New {@link Cell}
	 */
	public Cell<T> createCell(float width, String value, boolean isPercent) {
		Cell<T> cell = new Cell<T>(this, width, value, isPercent);
		if (headerRow) {
			cell.setHeaderCell(true);
		}
		setBorders(cell, cells.isEmpty());
		cell.setLineSpacing(lineSpacing);
		cells.add(cell);
		return cell;
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
	public Cell<T> createCell(float width, String value) {
		return createCell(width, value, true);
	}

	/**
	 * <p>
	 * Creates an image cell with provided width and {@link Image}.
	 * </p>
	 * 
	 * @param width
	 *            Cell's width
	 * @param img
	 *            {@link Image} in the cell
	 * @param isPercent
	 *            If true, width is percentage of table width; otherwise absolute points
	 * @return {@link ImageCell}
	 */
	public ImageCell<T> createImageCell(float width, Image img, boolean isPercent) {
		ImageCell<T> cell = new ImageCell<>(this, width, img, isPercent);
		setBorders(cell, cells.isEmpty());
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
	public ImageCell<T> createImageCell(float width, Image img) {
		return createImageCell(width, img, true);
	}

	/**
	 * <p>
	 * Creates an image cell with provided width, image, and alignments.
	 * </p>
	 *
	 * @param width
	 *            Cell's width
	 * @param img
	 *            {@link Image} in the cell
	 * @param align
	 *            Cell's {@link HorizontalAlignment}
	 * @param valign
	 *            Cell's {@link VerticalAlignment}
	 * @param isPercent
	 *            If true, width is percentage of table width; otherwise absolute points
	 * @return {@link ImageCell}
	 */
	public Cell<T> createImageCell(float width, Image img, HorizontalAlignment align, VerticalAlignment valign, boolean isPercent) {
		Cell<T> cell = new ImageCell<T>(this, width, img, isPercent, align, valign);
		setBorders(cell, cells.isEmpty());
		cells.add(cell);
		return cell;
	}

	/**
	 * <p>
	 * Creates an image cell with provided width, image, and alignments.
	 * </p>
	 *
	 * @param width
	 *            Cell's width
	 * @param img
	 *            {@link Image} in the cell
	 * @param align
	 *            Cell's {@link HorizontalAlignment}
	 * @param valign
	 *            Cell's {@link VerticalAlignment}
	 * @return {@link ImageCell}
	 */
	public Cell<T> createImageCell(float width, Image img, HorizontalAlignment align, VerticalAlignment valign) {
		return createImageCell(width, img, align, valign, true);
	}

	/**
	 * <p>
	 * Creates a table cell with provided width and table data.
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
	 * @param isPercent
	 *            If true, width is percentage of table width; otherwise absolute points
	 * @return {@link TableCell} with provided width and table data
	 */
	public TableCell<T> createTableCell(float width, String tableData, PDDocument doc, PDPage page, float yStart,
			float pageTopMargin, float pageBottomMargin, boolean isPercent) {
		TableCell<T> cell = new TableCell<T>(this, width, tableData, isPercent, doc, page, yStart, pageTopMargin,
				pageBottomMargin);
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
	public TableCell<T> createTableCell(float width, String tableData, PDDocument doc, PDPage page, float yStart,
			float pageTopMargin, float pageBottomMargin) {
		return createTableCell(width, tableData, doc, page, yStart, pageTopMargin, pageBottomMargin, true);
	}

	/**
	 * <p>
	 * Creates a cell with provided width, cell value, horizontal and vertical
	 * alignment.
	 * </p>
	 * 
	 * @param width
	 *            Absolute width in points or in % of table width (depending on
	 *            {@code isPercent})
	 * @param value
	 *            Cell's value (content)
	 * @param align
	 *            Cell's {@link HorizontalAlignment}
	 * @param valign
	 *            Cell's {@link VerticalAlignment}
	 * @param isPercent
	 *            If true, width is percentage of table width; otherwise absolute points
	 * @return New {@link Cell}
	 */
	public Cell<T> createCell(float width, String value, HorizontalAlignment align, VerticalAlignment valign, boolean isPercent) {
		Cell<T> cell = new Cell<T>(this, width, value, isPercent, align, valign);
		if (headerRow) {
			cell.setHeaderCell(true);
		}
		setBorders(cell, cells.isEmpty());
		cell.setLineSpacing(lineSpacing);
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
	public Cell<T> createCell(float width, String value, HorizontalAlignment align, VerticalAlignment valign) {
		return createCell(width, value, align, valign, true);
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
	public Cell<T> createCell(String value) {
		float headerCellWidth = table.getHeader().getCells().get(cells.size()).getWidth();
		Cell<T> cell = new Cell<T>(this, headerCellWidth, value, false);
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
	private void setBorders(final Cell<T> cell, final boolean leftBorder) {
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
		for (final Cell<T> cell : cells) {
			cell.setTopBorderStyle(null);
		}
	}

	/**
	 * <p>
	 * Remove all borders of cells.
	 * </p>
	 */
	void removeAllBorders() {
		for (final Cell<T> cell : cells) {
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
		if (fixedHeight) {
			return height;
		}
		float maxheight = 0.0f;
		for (Cell<T> cell : this.cells) {
			float cellHeight = cell.getCellHeight();

			if (cellHeight > maxheight) {
				maxheight = cellHeight;
			}
		}

		return Math.max(height, maxheight);
	}

	/**
	 * <p>
	 * Returns the base row height configured by {@link #setHeight(float)}.
	 * </p>
	 *
	 * @return Base row height
	 */
	public float getLineHeight() throws IOException {
		return height;
	}

	/**
	 * <p>
	 * Sets the base row height in points. When {@link #isFixedHeight()} is
	 * {@code true}, this is the fixed row height. Otherwise it is the minimum
	 * row height and the row can grow to fit content.
	 * </p>
	 *
	 * @param height
	 *            Base row height in points
	 */
	public void setHeight(float height) {
		this.height = height;
	}

	/**
	 * <p>
	 * Returns whether the row height is fixed. Default is {@code false}.
	 * </p>
	 *
	 * @return {@code true} when height is fixed
	 */
	public boolean isFixedHeight() {
		return fixedHeight;
	}

	/**
	 * <p>
	 * Sets whether the row height is fixed. When enabled, the row height equals
	 * the configured height and text is shrunk to fit.
	 * </p>
	 *
	 * @param fixedHeight
	 *            {@code true} to fix the row height
	 */
	public void setFixedHeight(boolean fixedHeight) {
		this.fixedHeight = fixedHeight;
	}

	/**
	 * <p>
	 * Returns the list of cells in this row.
	 * </p>
	 *
	 * @return Cells in the row
	 */
	public List<Cell<T>> getCells() {
		return cells;
	}

	/**
	 * <p>
	 * Returns the number of columns (cells) in the row.
	 * </p>
	 *
	 * @return Column count
	 */
	public int getColCount() {
		return cells.size();
	}

	/**
	 * <p>
	 * Replaces the row's cell list.
	 * </p>
	 *
	 * @param cells
	 *            New cell list
	 */
	public void setCells(List<Cell<T>> cells) {
		this.cells = cells;
	}

	/**
	 * <p>
	 * Returns the available table width for this row.
	 * </p>
	 *
	 * @return Row width in points
	 */
	public float getWidth() {
		return table.getWidth();
	}

	/**
	 * <p>
	 * Returns the bookmark associated with this row, if any.
	 * </p>
	 *
	 * @return Row bookmark or {@code null}
	 */
	public PDOutlineItem getBookmark() {
		return bookmark;
	}

	/**
	 * <p>
	 * Sets the bookmark for this row.
	 * </p>
	 *
	 * @param bookmark
	 *            Bookmark to use
	 */
	public void setBookmark(PDOutlineItem bookmark) {
		this.bookmark = bookmark;
	}

	protected float getLastCellExtraWidth() {
		float cellWidth = 0;
		for (Cell<T> cell : cells) {
			cellWidth += cell.getWidth();
		}

		float lastCellExtraWidth = this.getWidth() - cellWidth;
		return lastCellExtraWidth;
	}

	/**
	 * <p>
	 * Returns the absolute X end position of the row (margin + width).
	 * </p>
	 *
	 * @return Row end X position
	 */
	public float xEnd() {
		return table.getMargin() + getWidth();
	}

	/**
	 * <p>
	 * Returns whether this row is a header row.
	 * </p>
	 *
	 * @return {@code true} when header row
	 */
	public boolean isHeaderRow() {
		return headerRow;
	}

	/**
	 * <p>
	 * Marks this row as header or non-header. This does not change fixed-height
	 * behavior.
	 * </p>
	 *
	 * @param headerRow
	 *            {@code true} to mark as header row
	 */
	public void setHeaderRow(boolean headerRow) {
		this.headerRow = headerRow;
	}

	void fitTextToHeight(float rowHeight) {
		if (!fixedHeight) {
			return;
		}
		for (Cell<T> cell : cells) {
			if (cell instanceof ImageCell || cell instanceof TableCell) {
				continue;
			}
			cell.fitFontSizeToHeight(rowHeight);
		}
	}

	/**
	 * <p>
	 * Returns the row line spacing multiplier. Default is {@code 1}.
	 * </p>
	 *
	 * @return Line spacing multiplier
	 */
	public float getLineSpacing() {
		return lineSpacing;
	}

	/**
	 * <p>
	 * Sets the row line spacing multiplier. Applies to newly created cells.
	 * </p>
	 *
	 * @param lineSpacing
	 *            Line spacing multiplier
	 */
	public void setLineSpacing(float lineSpacing) {
		this.lineSpacing = lineSpacing;
	}


	/**
	 * Finds out, taking restricted row wrapping into account, how much vertical space this row,
	 * together with all un-wrappable rows that follow, will take.
	 *
	 * @return wrapHeight
	 */
	public float getWrapHeight() {
		return table.calcWrapHeight(this);
	}

	protected void setWrapHeight(float wrapHeight) {
		this.wrapHeight = wrapHeight;
	}

	protected float getSavedWrapHeight() {
		return this.wrapHeight;
	}

	/**
	 * <p>
	 * Returns whether all cells in the row have a bottom border.
	 * </p>
	 *
	 * @return {@code true} when all cells define a bottom border
	 */
	public boolean hasBottomBorder() {
		for (Cell<T> cell : cells) {
			if (cell.getBottomBorder() == null) {
				return false;
			}
		}
		return !cells.isEmpty();
	}
}
