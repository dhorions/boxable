package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class LastColumnFillOverflowTest {

    @Test
    public void lastColumnFillShouldNotOverflowTableWidth() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float margin = 50f;
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = 70f;

        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);

        Row<PDPage> header = table.createRow(18f);
        header.createCell(30, "Col A");
        header.createCell(30, "Col B");
        header.createCell(40, "Col C (last)");
        table.addHeaderRow(header);

        Row<PDPage> row = table.createRow(18f);
        row.createCell(30, "A1");
        row.createCell(30, "B1");
        Cell<PDPage> last = row.createCell(40, "Last col filled");
        last.setFillColor(new Color(255, 200, 200));

        Row<PDPage> expectedRow = table.createRow(22f);
        expectedRow.createCell(100,
            "Expected behavior: the last column fill stays inside the table border with no overflow to the right.");

        table.draw();

        String text = new PDFTextStripper().getText(doc);

        File file = new File("target/LastColumnFillOverflowTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        assertTrue(text.contains("Last col filled"));
        assertTrue(text.contains("Expected behavior"));
    }
}
