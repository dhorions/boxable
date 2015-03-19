
/*
 Quodlibet.be
 */
package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
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

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class Table<T extends PDPage> {

    private final static Logger LOGGER = LoggerFactory.getLogger(Table.class);

    private final PDDocument document;
    private final static PDRectangle PAGE_SIZE = PDPage.PAGE_SIZE_A4;
    private float margin;

    private T currentPage;
    private PDPageContentStream tableContentStream;
    private List<PDOutlineItem> bookmarks;
    private static final float VerticalCellMargin = 2f;
    private static final float HorizontalCellMargin = 2f;
    private static final int xSpacing  = 7;
    private Row header;
    private List<Row> rows = new ArrayList<Row>();

    private final float yStartNewPage;
    private float yStart;
    private final float bottomMargin;
    private final float topMargin = 10;
    private final float width;
    private final boolean drawLines;
    private final boolean drawContent;

    public Table(float yStart,float yStartNewPage, float bottomMargin, float width, float margin, PDDocument document, T currentPage, boolean drawLines, boolean drawContent) throws IOException {
        this.document = document;
        this.drawLines = drawLines;
        this.drawContent = drawContent;
        //Initialize table
        this.yStartNewPage = yStartNewPage;
        this.margin = margin;
        this.width = width;
        this.yStart = yStart + topMargin;
        this.bottomMargin = bottomMargin;
        this.currentPage = currentPage;
        loadFonts();
        this.yStart = yStart;
        this.tableContentStream = createPdPageContentStream();
    }

    public Table(float yStartNewPage, float bottomMargin, float width, float margin, PDDocument document, boolean drawLines, boolean drawContent) throws IOException {
        this.document = document;
        this.drawLines = drawLines;
        this.drawContent = drawContent;
        //Initialize table
        this.yStartNewPage = yStartNewPage;
        this.margin = margin;
        this.width = width;
        this.bottomMargin = bottomMargin;

        // Fonts needs to be loaded before page creation
        loadFonts();
        this.currentPage = createPage();
        this.tableContentStream = createPdPageContentStream();
    }

    protected abstract void loadFonts() throws IOException ;

    protected PDTrueTypeFont loadFont(String fontPath) throws IOException {
        return BoxableUtils.loadFont(getDocument(),fontPath);
    }

    protected PDDocument getDocument() {
        return document;
    }

    public void drawTitle(String title, PDFont font, int fontSize) throws IOException {
        drawTitle(title, font, fontSize, null);
    }

    public void drawTitle(String title, PDFont font, int fontSize, TextType textType) throws IOException {
        PDPageContentStream articleTitle = createPdPageContentStream();

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


        if (isEndOfPage(row)) {

            //Draw line at bottom of table
            endTable();

            // Reset yStart to yStartNewPage
            this.yStart = yStartNewPage;


            //Start new table on new page
            this.currentPage = createPage();
            this.tableContentStream = createPdPageContentStream();

            //redraw all headers on each currentPage
            LOGGER.info("re-draw Header on new Page");
            if (header != null) {
                drawRow(header);
            } else {
                LOGGER.warn("No Header Row Defined.");
            }
        }

        if (drawLines) {
            drawVerticalLines(row);
        }


        if (drawContent) {
            drawCellContent(row);
        }

    }

    private PDPageContentStream createPdPageContentStream() throws IOException {
        LOGGER.info("createPdPageContentStream");
        return new PDPageContentStream(getDocument(), getCurrentPage(), true, true);
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

            this.tableContentStream.setFont(cell.getFont(), cell.getFontSize());
            this.tableContentStream.setNonStrokingColor(cell.getTextColor());

            this.tableContentStream.beginText();
            this.tableContentStream.moveTextPositionByAmount(nextX, nextY);
            List<String> lines = cell.getParagraph().getLines();
            int numLines = cell.getParagraph().getLines().size();
            this.tableContentStream.appendRawCommands(cell.getParagraph().getFontHeight() + " TL\n");

            for (String line : cell.getParagraph().getLines()) {

                //out.drawString(i.next().trim());
                this.tableContentStream.drawString(line.trim());
                if (numLines > 0) this.tableContentStream.appendRawCommands("T*\n");
                numLines--;
            }

            //this.tableContentStream.drawString(cell.getText());
            this.tableContentStream.endText();
            this.tableContentStream.closeSubPath();
            nextX += cell.getWidth() + HorizontalCellMargin;
        }
        //Set Y position for next row
        yStart = yStart - row.getHeight();
    }

    private void drawVerticalLines(Row row) throws IOException {
        float xStart = margin;

        // give an extra margin to the latest cell
        float xEnd = row.xEnd() + xSpacing;

        // Draw Row upper border
        drawLine("Row Upper Border ", xStart, yStart, xEnd, yStart);

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

        drawLine("Last Cell ",xEnd , yStart, xEnd, yEnd);
    }

    private void drawLine(String type, float xStart, float yStart, float xEnd, float yEnd) throws IOException {

        this.tableContentStream.setNonStrokingColor(Color.BLACK);
        this.tableContentStream.setStrokingColor(Color.BLACK);

        LOGGER.debug(type + "Line from X=" + xStart + " Y=" + yStart + " to X=" + xEnd + " Y=" + yEnd);
        this.tableContentStream.drawLine(xStart, yStart, xEnd, yEnd);
        this.tableContentStream.closeSubPath();
    }

    private void fillCellColor(Cell cell, float yStart, float xStart, Iterator<Cell> cellIterator) throws IOException {
        //Fill Cell Color
        if (cell.getFillColor() != null) {
            this.tableContentStream.setNonStrokingColor(cell.getFillColor());

            //y start is bottom pos
            yStart = yStart - cell.getHeight();
            float height = cell.getHeight() - 1f;

            float width = getWidth(cell, cellIterator);

            this.tableContentStream.fillRect(xStart, yStart, width + xSpacing, height);
            this.tableContentStream.closeSubPath();

            // Reset NonStroking Color to default value
            this.tableContentStream.setNonStrokingColor(Color.BLACK);
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

    protected abstract T createPage();

    private void endTable() throws IOException {
        LOGGER.info("Ending Table");
        if (drawLines) {
            //Draw line at bottom
            drawLine("Row Bottom Border ", this.margin, this.yStart, this.margin + width + xSpacing, this.yStart);
        }
        this.tableContentStream.close();
    }

    public T getCurrentPage() {
        checkNotNull(this.currentPage, "No current page defined.");
        return this.currentPage;
    }

    public boolean isEndOfPage(Row row) {

        float currentY = yStart - row.getHeight();
        boolean isEndOfPage = currentY  <= (bottomMargin + 10);
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

    protected void setYStart(float yStart) {
        this.yStart = yStart;
    }
}
