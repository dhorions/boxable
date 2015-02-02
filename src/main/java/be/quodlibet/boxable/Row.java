
/*
 Quodlibet.be
 */
package be.quodlibet.boxable;


import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Row {
    PDOutlineItem bookmark;
    List<Cell> cells;
    float height;
    Row(List<Cell> cells, float height) {
        this.cells = cells;
        this.height = height;
    }
    
    Row(float height) {
      this.height = height;
    }
    
    public void addCell(Cell cell) {
        if (cells == null) cells = new ArrayList();
        cells.add(cell);
    }
    
    public float getHeight() throws IOException {
        //return height;
        float maxheight = new Float(0);
        for( Cell cell : this.cells)
        {
            float cellHeight =  ( cell.getParagraph().getLines().size() * this.height);
            if(cellHeight  > maxheight) maxheight = cellHeight;
        }
        return maxheight;
    }
    
    public float getLineHeight() throws IOException {
        return height;
       
    }

    public void setHeight(float height)
    {
        this.height = height;
    }

    public List<Cell> getCells()
    {
        return cells;
    }
    public int getColCount()
    {
        return cells.size();
    }
    public void setCells(List<Cell> cells)
    {
        this.cells = cells;
    }
    public float getWidth()
    {
        float totalWidth = 0;
        for(Cell cell : cells)
        {
            totalWidth += cell.getWidth();
        }
        return totalWidth;
    }

    public PDOutlineItem getBookmark()
    {
        return bookmark;
    }

    public void setBookmark(PDOutlineItem bookmark)
    {
        this.bookmark = bookmark;
    }



}
