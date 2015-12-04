
/*
 Quodlibet.be
 */
package be.quodlibet.boxable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

public class Row<T extends PDPage> {

	private final Table<T> table;
	PDOutlineItem bookmark;
	List<Cell<T>> cells;
	float height;

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
	 * alignment
	 * </p>
	 * 
	 * @param width
	 * @param value
	 * @return
	 */
	public Cell<T> createCell(float width, String value) {
		Cell<T> cell = new Cell<T>(this, width, value, true);
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
	 * @param value
	 * @param align
	 * @param valign
	 * @return
	 */
	public Cell<T> createCell(float width, String value, HorizontalAlignment align, VerticalAlignment valign) {
		Cell<T> cell = new Cell<T>(this, width, value, true, align, valign);
		cells.add(cell);
		return cell;
	}

	/**
	 * Creates a cell with the same width as the corresponding header cell
	 *
	 * @param value
	 * @return
	 */
	public Cell<T> createCell(String value) {

		float headerCellWidth = table.getHeader().getCells().get(cells.size()).getWidth();
		Cell<T> cell = new Cell<T>(this, headerCellWidth, value, false);
		cells.add(cell);
		return cell;
	}

	public float getHeight() {

		float maxheight = new Float(0);

		for (Cell<T> cell : this.cells) {
			float cellHeight = 0;
			cellHeight = cell.getTextHeight() + cell.getTopPadding() + cell.getBottomPadding();

			if (cellHeight > maxheight) {
				maxheight = cellHeight;
			}
		}
		return maxheight;
	}

	public float getLineHeight() throws IOException {
		return height;

	}

	public void setHeight(float height) {
		this.height = height;
	}

	public List<Cell<T>> getCells() {
		return cells;
	}

	public int getColCount() {
		return cells.size();
	}

	public void setCells(List<Cell<T>> cells) {
		this.cells = cells;
	}

	public float getWidth() {
		return table.getWidth();
	}

	public PDOutlineItem getBookmark() {
		return bookmark;
	}

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

	public float xEnd() {
		return table.getMargin() + getWidth();
	}
}
