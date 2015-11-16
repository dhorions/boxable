
/*
 Quodlibet.be
 */
package be.quodlibet.boxable;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

public class Row {

    private final Table table;
    PDOutlineItem bookmark;
    List<Cell> cells;
    float height;

    Row(Table table, List<Cell> cells, float height) {
        this.table = table;
        this.cells = cells;
        this.height = height;
    }

    Row(Table table, float height) {
        this.table = table;
        this.cells = new ArrayList<Cell>();
        this.height = height;
    }

    public Cell createCell(float width, String value) {
        Cell cell = new Cell(this, width, value,true);
        cells.add(cell);
        return cell;
    }

    /**
     * Creates a cell with the same width as the corresponding header cell
     *
     * @param value
     * @return
     */
    public Cell createCell(String value) {

        float headerCellWidth = table.getHeader().getCells().get(cells.size()).getWidth();

        Cell cell = new Cell(this, headerCellWidth, value,false);
        cells.add(cell);
        return cell;
    }

    public float getHeight() {

        float maxheight = new Float(0);

        for (Cell cell : this.cells) {
            float cellHeight = 0;
            cellHeight = (cell.getParagraph().getLines().size() * this.height);

            if (cellHeight > maxheight){
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

    public List<Cell> getCells() {
        return cells;
    }

    public int getColCount() {
        return cells.size();
    }

    public void setCells(List<Cell> cells) {
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
        for (Cell cell : cells) {
            cellWidth += cell.getWidth();
        }

        float lastCellExtraWidth = this.getWidth() - cellWidth;
        return lastCellExtraWidth;
    }

    public float xEnd() {
        return table.getMargin() + getWidth();
    }
}
