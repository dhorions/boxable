package be.quodlibet.boxable;

import be.quodlibet.boxable.line.LineStyle;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class OuterBorderNoInnerLinesTest {

    @Test
    public void drawLinesFalseWithOuterBorderOnly() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float margin = 50f;
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = 70f;

        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin,
                doc, page, false, true);
        table.setOuterBorderStyle(new LineStyle(Color.BLACK, 1f));

        Row<PDPage> row = table.createRow(18f);
        row.createCell(33.33f, "A1");
        row.createCell(33.33f, "A2");
        row.createCell(33.34f, "A3");

        Row<PDPage> expectedRow = table.createRow(28f);
        expectedRow.createCell(100f,
                "Expected behavior: only the outer table border is visible; no internal grid lines appear.");

        table.draw();

        String text = new PDFTextStripper().getText(doc);

        File file = new File("target/OuterBorderNoInnerLinesTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        assertTrue(text.contains("Expected behavior"));
        assertTrue(text.contains("A1"));
        assertTrue(text.contains("A2"));
        assertTrue(text.contains("A3"));
    }
}
