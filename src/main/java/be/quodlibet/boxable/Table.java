
/*
 Quodlibet.be
 */
package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Table {
    private final PDDocument document;
    private float nextYpos;
    private float margin;
    private PDPage currentPage;
    private PDPageContentStream contentStream;
    private List<PDOutlineItem> bookmarks;
    private static final float VerticalCellMargin = 2f;
    private static final float HorizontalCellMargin = 2f;
    private Row header;
    private List<Row> rows = new ArrayList<Row>();

    private float tableWidth;
    private float top;
    private float yPos;

    public Table(float margin, PDDocument document, PDPage currentPage) throws IOException {
        this.document = document;
        this.currentPage = currentPage;
        this.contentStream = new PDPageContentStream(document, currentPage);

        //Initialize table
        initializeTable();
        this.margin = margin;
    }

    private void initializeTable() {
        this.tableWidth = this.currentPage.findMediaBox().getWidth() - (2 * this.margin);
        this.top = this.currentPage.findMediaBox().getHeight() - (2 * this.margin);
        this.yPos = top - (1 * 20f);
        this.nextYpos = yPos;
    }

    public Row createRow(float height) {
        Row row = new Row(height);
        this.rows.add(row);
        return row;
    }
    
    public Row createRow(List<Cell> cells,float height) {
        Row row = new Row(cells,height);
        this.rows.add(row);
        return row;
    }

    public void draw() throws IOException {
        for (Row row : rows) {
            drawRow(row);
        }
    }

    private void drawRow(Row row) throws IOException {
        Boolean bookmark = false;

        //draw the horizontal line
        float nexty = nextYpos;
        this.contentStream.drawLine(margin, nexty, margin + row.getWidth(), nexty);

        //draw the bookmark
        if (row.getBookmark() != null) {
            bookmark = true;
            PDPageXYZDestination bookmarkDestination = new PDPageXYZDestination();
            bookmarkDestination.setPage(currentPage);
            bookmarkDestination.setTop((int) nextYpos);
            row.getBookmark().setDestination(bookmarkDestination);
            this.addBookmark(row.getBookmark());
        }

        //draw the vertical lines
        float nextx = margin;
        for (Cell cell : row.getCells()) {

            //Fill Cell Color
            if (cell.getFillColor() != null) {
                this.contentStream.setNonStrokingColor(cell.getFillColor());
                //y start is bottom pos
                this.contentStream.fillRect(nextx, nexty - row.getHeight(), cell.getWidth(), row.getHeight() - 1f);
                this.contentStream.closeSubPath();
            }

            this.contentStream.setNonStrokingColor(Color.BLACK);
            this.contentStream.drawLine(nextx, nexty, nextx, nexty - row.getHeight());
            this.contentStream.closeSubPath();

            nextx += cell.getWidth();
        }
        //draw the line at the right of the table
        this.contentStream.setNonStrokingColor(Color.BLACK);
        this.contentStream.drawLine(nextx, nexty, nextx, nexty - row.getHeight());
        this.contentStream.closeSubPath();

        //now add the cell content
        nextx = margin + HorizontalCellMargin;
        nexty = nextYpos - (row.getLineHeight() - VerticalCellMargin);

        for (Cell cell : row.getCells()) {

            this.contentStream.setFont(cell.getFont(), cell.getFontSize());
            if (cell.getTextColor() != null) {

                this.contentStream.setNonStrokingColor(cell.getTextColor());
            } else {
                this.contentStream.setNonStrokingColor(Color.BLACK);
            }

            this.contentStream.beginText();
            this.contentStream.moveTextPositionByAmount(nextx, nexty);
            List<String> lines = cell.getParagraph().getLines();
            int numLines = cell.getParagraph().getLines().size();
            this.contentStream.appendRawCommands(cell.getParagraph().getFontHeight() + " TL\n");

            for (String line : cell.getParagraph().getLines()) {

                //out.drawString(i.next().trim());
                this.contentStream.drawString(line.trim());
                if (numLines > 0) this.contentStream.appendRawCommands("T*\n");
                numLines--;
            }

            //this.contentStream.drawString(cell.getText());
            this.contentStream.endText();
            this.contentStream.closeSubPath();
            nextx += cell.getWidth() + HorizontalCellMargin;
        }
        //Set Y position for next row
        nextYpos = nextYpos - row.getHeight();

        if (isEndOfPage()) {

            //Draw line at bottom of table
            endTable(row.getWidth());

            //Start new table on new currentPage
            this.currentPage = addNewPage();
            this.contentStream = new PDPageContentStream(this.document, currentPage);
            initializeTable();

            //redraw all headers on each currentPage
            drawRow(header);
        }
    }

    private PDPage addNewPage() {
        PDPage page = new PDPage();
        this.document.addPage(page);
        return page;
    }


    public void endTable(float width) throws IOException {
        //Draw line at bottom
        this.contentStream.drawLine(this.margin, this.nextYpos, this.margin + width, this.nextYpos);
        this.contentStream.closeSubPath();
        this.contentStream.close();
    }

    public boolean isEndOfPage() {
        //If we are closer than 75 from bottom of currentPage, consider this the end of the currentPage
        //If you add rows that are higher then 75, this needs to be checked manually using getNextYPos
        if (nextYpos <= 75) return true;
        return false;
    }

    public float getNextYpos() {
        return nextYpos;
    }

    private void addBookmark(PDOutlineItem bookmark) {
        if (bookmarks == null) bookmarks = new ArrayList();
        bookmarks.add(bookmark);
    }

    public List<PDOutlineItem> getBookmarks() {
        return bookmarks;
    }


    public void setHeader(Row header) {
        this.header = header;
    }

    public Row getHeader() {
        return header;
    }
}
