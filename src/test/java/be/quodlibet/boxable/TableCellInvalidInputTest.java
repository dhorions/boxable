package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class TableCellInvalidInputTest {

    @Test
    public void invalidColspanValueShouldNotThrow() throws IOException {
        float margin = 40f;
        float bottomMargin = 70f;

        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;

        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);

        Row<PDPage> headerRow = table.createRow(15f);
        headerRow.createCell(30, "Section");
        headerRow.createCell(70, "Details");
        table.addHeaderRow(headerRow);

        yStart -= headerRow.getHeight();

        Row<PDPage> dataRow = table.createRow(12f);
        dataRow.createCell(30, "Attributes");

        String innerTableHtml = "<table>"
                + "<tr><td colspan=\"foo\">Project</td><td>Boxable PDF</td></tr>"
                + "<tr><td>Owner</td><td>Quodlibet</td></tr>"
                + "</table>";
        dataRow.createTableCell(70, innerTableHtml, doc, page, yStart, margin, bottomMargin);

        Row<PDPage> expectedRow = table.createRow(18f);
        expectedRow.createCell(100,
                "Expected behavior: invalid colspan values should not throw and should be treated as 1.");

        Exception error = null;
        try {
            table.draw();
        } catch (Exception e) {
            error = e;
        }

        String text = new PDFTextStripper().getText(doc);

        File file = new File("target/TableCellInvalidColspanTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        assertTrue(text.contains("Expected behavior: invalid colspan values should not throw"));
        assertTrue("Did not expect exception for invalid colspan", error == null);
    }

    @Test
    public void zeroColspanShouldNotThrow() throws IOException {
        float margin = 40f;
        float bottomMargin = 70f;

        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;

        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);

        Row<PDPage> headerRow = table.createRow(15f);
        headerRow.createCell(30, "Section");
        headerRow.createCell(70, "Details");
        table.addHeaderRow(headerRow);

        yStart -= headerRow.getHeight();

        Row<PDPage> dataRow = table.createRow(12f);
        dataRow.createCell(30, "Attributes");

        String innerTableHtml = "<table>"
                + "<tr><td colspan=\"0\">Project</td><td>Boxable PDF</td></tr>"
                + "<tr><td>Owner</td><td>Quodlibet</td></tr>"
                + "</table>";
        dataRow.createTableCell(70, innerTableHtml, doc, page, yStart, margin, bottomMargin);

        Row<PDPage> expectedRow = table.createRow(18f);
        expectedRow.createCell(100,
                "Expected behavior: zero colspan should be treated as 1 and not throw.");

        Exception error = null;
        try {
            table.draw();
        } catch (Exception e) {
            error = e;
        }

        String text = new PDFTextStripper().getText(doc);

        File file = new File("target/TableCellZeroColspanTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        assertTrue(text.contains("Expected behavior: zero colspan should be treated as 1"));
        assertTrue("Did not expect exception for zero colspan", error == null);
    }
}
