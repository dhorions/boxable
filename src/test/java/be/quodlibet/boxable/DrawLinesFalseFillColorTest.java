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

public class DrawLinesFalseFillColorTest {

    @Test
    public void drawLinesFalseShouldStillRenderFillColor() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float margin = 50f;
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = 70f;

        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, false, true);

        Row<PDPage> row = table.createRow(20f);
        Cell<PDPage> cell = row.createCell(100, "Filled cell without borders");
        cell.setFillColor(Color.YELLOW);

        Row<PDPage> expectedRow = table.createRow(22f);
        expectedRow.createCell(100,
            "Expected behavior: The first row has a yellow background even though drawLines=false, and no borders are drawn.");

        table.draw();

        String text = new PDFTextStripper().getText(doc);

        File file = new File("target/DrawLinesFalseFillColorTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        assertTrue(text.contains("Filled cell without borders"));
        assertTrue(text.contains("Expected behavior"));
    }
}
