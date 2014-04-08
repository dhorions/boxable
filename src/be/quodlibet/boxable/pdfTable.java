
/*
 Quodlibet.be
 */
package be.quodlibet.boxable;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

public class pdfTable {
    private float nextYpos;
    private float margin;
    private PDPage page;
    private PDPageContentStream contentStream;
    private PDDocumentOutline outline;
    private List<PDOutlineItem> bookmarks;
    private static final float VerticalCellMargin   = 2f;
    private static final float HorizontalCellMargin = 2f;
    public pdfTable(float yPos,float margin,PDPage page, PDPageContentStream contentStream)
    {
        this.nextYpos = yPos;
        this.margin = margin;
        this.page = page;
        this.contentStream = contentStream;
    }

    public  void drawRow(pdfRow row) throws IOException
    {
        Boolean bookmark = false;
         //draw the horizontal line
        float nexty = nextYpos ;
        contentStream.drawLine(margin,nexty,margin+row.getWidth(),nexty);

        //draw the bookmark
        if(row.getBookmark()!= null)
        {
            bookmark = true;
            PDPageXYZDestination bookmarkDestination = new PDPageXYZDestination();
            bookmarkDestination.setPage(page);
            bookmarkDestination.setTop((int)nextYpos);
            row.getBookmark().setDestination(bookmarkDestination);
            this.addBookmark(row.getBookmark());
        }

        //draw the vertical lines
        float nextx = margin;
        for (pdfCell cell : row.getCells())
        {
            //Fill Cell Color
            if(cell.getFillColor() != null)
            {
                contentStream.setNonStrokingColor( cell.getFillColor() );
                //y start is bottom pos
                contentStream.fillRect( nextx,nexty-row.getHeight(), cell.getWidth(), row.getHeight() - 1f );
                
                contentStream.closeSubPath();
            }

            contentStream.setNonStrokingColor( Color.BLACK);
            contentStream.drawLine(nextx,nexty,nextx,nexty-row.getHeight());
            contentStream.closeSubPath();
            
            nextx += cell.getWidth();
        }
        //draw the line at the right of the table
        contentStream.setNonStrokingColor( Color.BLACK);
        contentStream.drawLine(nextx,nexty,nextx,nexty-row.getHeight());
        contentStream.closeSubPath();

        //now add the cell content
        nextx = margin + HorizontalCellMargin;
        nexty = nextYpos  - ( row.getLineHeight()  - VerticalCellMargin );
        for (pdfCell cell : row.getCells())
        {
            contentStream.setFont(cell.getFont(),cell.getFontSize());
            if(cell.getTextColor() != null)
            {
                contentStream.setNonStrokingColor( cell.getTextColor());
            }
            else
            {
                contentStream.setNonStrokingColor( Color.BLACK);
            }
            contentStream.beginText();
            contentStream.moveTextPositionByAmount(nextx,nexty);
            List<String> lines = cell.getParagraph().getLines();
            int numLines = cell.getParagraph().getLines().size();
            contentStream.appendRawCommands(cell.getParagraph().getFontHeight() + " TL\n");
            for (String line : cell.getParagraph().getLines() )
            {
                
                //out.drawString(i.next().trim());
                contentStream.drawString(line.trim());
                if (numLines > 0) contentStream.appendRawCommands("T*\n");
                numLines--;
            }
            //contentStream.drawString(cell.getText());
            contentStream.endText();
            contentStream.closeSubPath();
            nextx += cell.getWidth() + HorizontalCellMargin;
        }
        //Set Y position for next row
        nextYpos = nextYpos - row.getHeight();
        if(isEndOfPage())
        {
            //Draw line at bottom of table
            endTable(row.getWidth());

        }
    }
    public void endTable(float width) throws IOException
    {
        //Draw line at bottom
        contentStream.drawLine(margin,nextYpos,margin+width,nextYpos);
        contentStream.closeSubPath();
    }
    public boolean isEndOfPage()
    {
        //If we are closer than 75 from bottom of page, consider this the end of the page
        //If you add rows that are higher then 75, this needs to be checked manually using getNextYPos
        if(nextYpos <= 75) return true;
        return false;
    }

    public float getNextYpos()
    {
        return nextYpos;
    }
    private void addBookmark(PDOutlineItem bookmark)
    {
        if(bookmarks == null) bookmarks = new ArrayList();
        bookmarks.add(bookmark);
    }

    public List<PDOutlineItem> getBookmarks()
    {
        return bookmarks;
    }




}
