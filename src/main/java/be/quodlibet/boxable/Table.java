
/*
 Quodlibet.be
 */
package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Table {

    private final static Logger LOGGER = LoggerFactory.getLogger(Table.class);

    private final PDDocument document;
    private final static PDRectangle PAGE_SIZE = PDPage.PAGE_SIZE_A4;
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
    private float yStart;
    private float width;
    private final boolean drawLines;
    private final boolean drawContent;

    public Table(float top, float width, float margin, PDDocument document, PDPage currentPage, boolean drawLines, boolean drawContent) throws IOException {
        this.document = document;
        this.currentPage = currentPage;
        this.drawLines = drawLines;
        this.drawContent = drawContent;
        this.contentStream = getPdPageContentStream();

        //Initialize table
        this.top = top;

        initializeTable();
        this.margin = margin;
        this.width = width;
    }

    public void drawTitle(String title, PDFont font, int fontSize) throws IOException {
        drawTitle(title, font, fontSize, null);
    }

    public void drawTitle(String title, PDFont font, int fontSize, TextType textType) throws IOException {
        PDPageContentStream articleTitle = new PDPageContentStream(this.document, this.currentPage, true, true);

        articleTitle.beginText();
        articleTitle.setFont(font, fontSize);
        articleTitle.moveTextPositionByAmount(getMargin(), yStart);
        articleTitle.setNonStrokingColor(Color.black);
        articleTitle.drawString(title);
        articleTitle.endText();

        if (textType != null) {
            switch (textType) {
                case HIGHLIGHT:
                    throw new NotImplementedException();
                case SQUIGGLY:
                    throw new NotImplementedException();
                case STRIKEOUT:
                    throw new NotImplementedException();
                case UNDERLINE:
                    float y = (float) (yStart - 1.5);
                    float titleWidth = font.getStringWidth(title) / 1000 * fontSize;
                    articleTitle.drawLine(getMargin(), y, getMargin() + titleWidth, y);
                    break;
                default:
                    break;
            }
        }
        articleTitle.close();

        yStart = (float) (yStart - (fontSize / 1.5));

    }

    private void initializeTable() {
        this.tableWidth = this.currentPage.findMediaBox().getWidth() - (2 * this.margin);
        this.yStart = top - (1 * 20f);
    }

    public float getWidth() {
        return width;
    }


    public Row createRow(float height) {
        Row row = new Row(this, height);
        this.rows.add(row);
        return row;
    }

    public Row createRow(List<Cell> cells, float height) {
        Row row = new Row(this, cells, height);
        this.rows.add(row);
        return row;
    }

    public float draw() throws IOException {
        for (Row row : rows) {
            drawRow(row);
        }
        endTable();

        return yStart;
    }

    private void drawRow(Row row) throws IOException {
        Boolean bookmark = false;


        //draw the bookmark
        if (row.getBookmark() != null) {
            bookmark = true;
            PDPageXYZDestination bookmarkDestination = new PDPageXYZDestination();
            bookmarkDestination.setPage(currentPage);
            bookmarkDestination.setTop((int) yStart);
            row.getBookmark().setDestination(bookmarkDestination);
            this.addBookmark(row.getBookmark());
        }

        if (drawLines) {
            drawVerticalLines(row);
        }


        if (drawContent) {
            drawCellContent(row);
        }

        if (isEndOfPage()) {

            //Draw line at bottom of table
            endTable();

            //Start new table on new currentPage
            this.currentPage = addNewPage();
            this.contentStream = getPdPageContentStream();
            initializeTable();

            //redraw all headers on each currentPage
            LOGGER.info("re-draw Header on new Page");
            if (header != null) {
                drawRow(header);
            } else {
                LOGGER.warn("No Header Row Defined.");
            }
        }
    }

    private PDPageContentStream getPdPageContentStream() throws IOException {
        LOGGER.info("getPdPageContentStream");
        return new PDPageContentStream(this.document, this.currentPage, true, true);
    }

    private void drawCellContent(Row row) throws IOException {

        float nextX = margin + HorizontalCellMargin;
        float nextY = yStart - (row.getLineHeight() - VerticalCellMargin);

        Iterator<Cell> cellIterator = row.getCells().iterator();
        while (cellIterator.hasNext()) {

            Cell cell = cellIterator.next();

            if (cell.getFont() == null) {
                throw new IllegalArgumentException("Font is null on Cell=" + cell.getText());
            }

            LOGGER.info("Draw Cell=" + cell.getText());

            this.contentStream.setFont(cell.getFont(), cell.getFontSize());
            this.contentStream.setNonStrokingColor(cell.getTextColor());

            this.contentStream.beginText();
            this.contentStream.moveTextPositionByAmount(nextX, nextY);
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
            nextX += cell.getWidth() + HorizontalCellMargin;
        }
        //Set Y position for next row
        yStart = yStart - row.getHeight();
    }

    private void drawVerticalLines(Row row) throws IOException {
        float xStart = margin;

        // Draw Row upper border
        drawLine("Row Upper Border ", xStart, yStart, row.xEnd(), yStart);

        Iterator<Cell> cellIterator = row.getCells().iterator();
        while (cellIterator.hasNext()) {

            Cell cell = cellIterator.next();

            fillCellColor(cell, yStart, xStart, cellIterator);

            float yEnd = yStart - row.getHeight();

            //draw the vertical line to separate cells
            drawLine("Cell Separator ", xStart, yStart, xStart, yEnd);

            xStart += getWidth(cell, cellIterator);
        }

        //draw the last vertical line at the right of the table
        float yEnd = yStart - row.getHeight();
        drawLine("Last Cell ", row.xEnd(), yStart, row.xEnd(), yEnd);
    }

    private void drawLine(String type, float xStart, float yStart, float xEnd, float yEnd) throws IOException {

        this.contentStream.setNonStrokingColor(Color.BLACK);
        this.contentStream.setStrokingColor(Color.BLACK);

        LOGGER.debug(type + "Line from X=" + xStart + " Y=" + yStart + " to X=" + xEnd + " Y=" + yEnd);
        this.contentStream.drawLine(xStart, yStart, xEnd, yEnd);
        this.contentStream.closeSubPath();
    }

    private void fillCellColor(Cell cell, float yStart, float xStart, Iterator<Cell> cellIterator) throws IOException {
        //Fill Cell Color
        if (cell.getFillColor() != null) {
            this.contentStream.setNonStrokingColor(cell.getFillColor());

            //y start is bottom pos
            yStart = yStart - cell.getHeight();
            float height = cell.getHeight() - 1f;

            float width = getWidth(cell, cellIterator);

            this.contentStream.fillRect(xStart, yStart, width, height);
            this.contentStream.closeSubPath();

            // Reset NonStroking Color to default value
            this.contentStream.setNonStrokingColor(Color.BLACK);
        }
    }

    private float getWidth(Cell cell, Iterator<Cell> cellIterator) {
        float width;
        if (cellIterator.hasNext()) {
            width = cell.getWidth();
        } else {
            width = cell.getExtraWidth();
        }
        return width;
    }

    private PDPage addNewPage() {
        PDPage page = new PDPage();
        this.document.addPage(page);
        return page;
    }


    private void endTable() throws IOException {
        LOGGER.info("Ending Table");
        if (drawLines) {
            //Draw line at bottom
            drawLine("Row Bottom Border ", this.margin, this.yStart, this.margin + width, this.yStart);
        }
        this.contentStream.close();
    }

    public PDPage getCurrentPage() {
        return this.currentPage;
    }

    public boolean isEndOfPage() {

        boolean isEndOfPage = yStart <= 75;
        LOGGER.info("isEndOfPage=" + isEndOfPage);

        //If we are closer than 75 from bottom of currentPage, consider this the end of the currentPage
        //If you add rows that are higher then 75, this needs to be checked manually using getNextYPos
        return isEndOfPage;
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
        if (header == null) {
            throw new IllegalArgumentException("Header Row not set on table");
        }
        return header;
    }

    float getMargin() {
        return margin;
    }
}
