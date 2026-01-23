package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

import be.quodlibet.boxable.line.LineStyle;
import java.awt.Color;

public class RowspanInnerTableTest {

    @Test
    public void innerTableCanReplaceRowspanForKeyValuePairs() throws IOException {
        float margin = 40f;
        float bottomMargin = 70f;

        PDDocument doc = new PDDocument();
        PDPage page = addNewPage(doc);

        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;

        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true,
                true);

        Row<PDPage> headerRow = table.createRow(15f);
        headerRow.createCell(30, "Section");
        headerRow.createCell(70, "Details");
        table.addHeaderRow(headerRow);

        yStart -= headerRow.getHeight();

        Row<PDPage> dataRow = table.createRow(12f);
        dataRow.createCell(30, "Attributes");
        
        String innerTableHtml = "<table>"
                + "<tr><th>Key</th><th>Value</th></tr>"
                + "<tr><td>Project</td><td>Boxable PDF</td></tr>"
                + "<tr><td>Owner</td><td>Quodlibet</td></tr>"
                + "<tr><td>Version</td><td>1.8.2</td></tr>"
                + "<tr><td>Status</td><td>Active</td></tr>"
                + "<tr><td>Issue</td><td>#208</td></tr></td></tr>"
                + "<tr><td>Submitted By</td><td>@mailidpankaj</td></tr></td></tr>"
                + "<tr><td>Notes</td><td>Multiple key/value rows rendered inside one cell.</td></tr>"
                + "</table>";
        TableCell<PDPage> innerTableCell = dataRow.createTableCell(70, innerTableHtml, doc, page, yStart, margin,
            bottomMargin);


        innerTableCell.setLeftPadding(0);
        innerTableCell.setRightPadding(0);
        innerTableCell.setTopPadding(0);
        innerTableCell.setBottomPadding(0);
        innerTableCell.setMarginBetweenElementsY(0);
        innerTableCell.setLeftBorderStyle(null);
        innerTableCell.setTopBorderStyle(null);

        innerTableCell.setInnerTableBorders(false, false, false, false);
        innerTableCell.setInnerTableInnerBorders(true, true);
        innerTableCell.setInnerTableStartAtTop(true);
        innerTableCell.setInnerTableBorderStyle(new LineStyle(Color.BLACK, 1));
        innerTableCell.setInnerTableCellPadding(2f, 2f, 2f, 2f);

        Row<PDPage> expectedRow = table.createRow(18f);
        expectedRow.createCell(100,
            "Expected behavior: the inner key/value table aligns to the top border without a gap, "
                + "fills the cell area, and the left border is not doubled.");

        table.draw();

        String text = new PDFTextStripper().getText(doc);

        File file = new File("target/RowspanInnerTableTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        assertTrue(text.contains("Expected behavior: the inner key/value table aligns to the top border without a gap"));
    }

    @Test
    public void innerTableWithoutTopAlignmentUsesDefaultSpacing() throws IOException {
        float margin = 40f;
        float bottomMargin = 70f;

        PDDocument doc = new PDDocument();
        PDPage page = addNewPage(doc);

        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;

        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true,
                true);

        Row<PDPage> headerRow = table.createRow(15f);
        headerRow.createCell(30, "Section");
        headerRow.createCell(70, "Details");
        table.addHeaderRow(headerRow);

        yStart -= headerRow.getHeight();

        Row<PDPage> dataRow = table.createRow(12f);
        dataRow.createCell(30, "Attributes");

        String innerTableHtml = "<table>"
                + "<tr><th>Key</th><th>Value</th></tr>"
                + "<tr><td>Project</td><td>Boxable PDF</td></tr>"
                + "<tr><td>Owner</td><td>Quodlibet</td></tr>"
                + "<tr><td>Version</td><td>1.8.2</td></tr>"
                + "<tr><td>Status</td><td>Active</td></tr>"
                + "</table>";
        dataRow.createTableCell(70, innerTableHtml, doc, page, yStart, margin, bottomMargin);

        Row<PDPage> expectedRow = table.createRow(18f);
        expectedRow.createCell(100,
                "Expected behavior: the inner table uses default spacing and does not align to the top border when not configured.");

        table.draw();

        String text = new PDFTextStripper().getText(doc);

        File file = new File("target/RowspanInnerTableDefaultSpacingTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        assertTrue(text.contains("Expected behavior: the inner table uses default spacing"));
    }

    private static PDPage addNewPage(PDDocument doc) {
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);
        return page;
    }
}
