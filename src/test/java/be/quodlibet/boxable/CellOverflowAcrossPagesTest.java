package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class CellOverflowAcrossPagesTest {

    @Test
    public void longCellContentShouldFlowAcrossPages() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float margin = 40f;
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = 50f;

        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);

        Row<PDPage> expectedRow = table.createRow(18f);
        expectedRow.createCell(100,
            "Expected behavior: the long text in the cell flows across multiple pages without overflowing the page bounds.");

        StringBuilder longText = new StringBuilder();
        longText.append("OVERFLOW START ");
        for (int i = 0; i < 3000; i++) {
            longText.append("word").append(i).append(' ');
        }
        longText.append("OVERFLOW END");

        Row<PDPage> row = table.createRow(12f);
        row.createCell(100, longText.toString());

        table.draw();

        String text = new PDFTextStripper().getText(doc);

        File file = new File("target/CellOverflowAcrossPagesTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        assertTrue(text.contains("OVERFLOW START"));
        assertTrue(text.contains("OVERFLOW END"));
    }
}
