package be.quodlibet.boxable;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.Test;

import be.quodlibet.boxable.line.LineStyle;

public class ZeroWidthBorderTest {

    @Test
    public void testZeroWidthBorder() throws IOException {
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
        Cell<PDPage> cell = row.createCell(50, "Cell with 0 width left border");
        
        // Set left border explicitly to 0 width
        cell.setLeftBorderStyle(new LineStyle(Color.BLACK, 0));
        
        // Other borders normal for contrast
        cell.setRightBorderStyle(new LineStyle(Color.BLACK, 1));
        cell.setTopBorderStyle(new LineStyle(Color.BLACK, 1));
        cell.setBottomBorderStyle(new LineStyle(Color.BLACK, 1));

        Cell<PDPage> cell2 = row.createCell(50, "Normal Cell");

        table.draw();

        // Close Stream and save pdf
        File file = new File("target/ZeroWidthBorderTest.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();
    }
}
