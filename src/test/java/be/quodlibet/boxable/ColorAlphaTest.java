package be.quodlibet.boxable;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.Test;

public class ColorAlphaTest {

    @Test
    public void testAlphaColor() throws IOException {
        // Set margins
        float margin = 10;

        // Initialize Document
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);

        // Initialize table
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        boolean drawContent = true;
        boolean drawLines = true;
        float yStart = yStartNewPage;
        float bottomMargin = 70;
        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, drawLines,
                drawContent);

        // Create row
        Row<PDPage> row = table.createRow(20f);
        Cell<PDPage> cell = row.createCell(50, "Cell with transparent background");
        cell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA));
        // Set fill color with alpha (red with transparency)
        cell.setFillColor(new Color(255, 0, 0, 100));

         Cell<PDPage> cell2 = row.createCell(50, "Cell with solid background");
        cell2.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA));
        cell2.setFillColor(new Color(255, 0, 0));

        table.draw();

        // Save the document
        File file = new File("target/ColorAlphaTest.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();
    }
}
