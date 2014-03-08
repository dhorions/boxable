
/*
 Quodlibet.be
 */
package be.quodlibet.boxable;


import java.util.ArrayList;
import java.util.List;

public class pdfRow {

    List<pdfCell> cells;
    float height;
    public pdfRow(List<pdfCell> cells, float height)
    {
        this.cells = cells;
        this.height = height;
    }
    public pdfRow( float height)
    {
      this.height = height;
    }
    public void addCell(pdfCell cell)
    {
        if (cells == null) cells = new ArrayList();
        cells.add(cell);
    }
    public float getHeight()
    {
        return height;
    }

    public void setHeight(float height)
    {
        this.height = height;
    }

    public List<pdfCell> getCells()
    {
        return cells;
    }
    public int getColCount()
    {
        return cells.size();
    }
    public void setCells(List<pdfCell> cells)
    {
        this.cells = cells;
    }
    public float getWidth()
    {
        float totalWidth = 0;
        for(pdfCell cell : cells)
        {
            totalWidth += cell.getWidth();
        }
        return totalWidth;
    }



}
