package be.quodlibet.boxable;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class StrikethroughTest {

    @Test
    public void testStrikethroughRendering() throws IOException {
        // Initialize Document
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        // Initialize Table
        float margin = 50;
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        boolean drawContent = true;
        float bottomMargin = 70;
        float yPosition = 550;
        BaseTable table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, drawContent);

        // Create Row
        Row<PDPage> row = table.createRow(20);

        // Create Cells with Strikethrough tags
        Cell<PDPage> cell = row.createCell(50, "Normal <s>Strikethrough</s> Normal");
        cell = row.createCell(50, "<s>All Strikethrough</s>");
        
        row = table.createRow(20);
        cell = row.createCell(50, "Mixed <b>Bold <s>Strike</s></b> text");
        cell = row.createCell(50, "Mixed <i>Italic <s>Strike</s></i> text");

        // Draw Table
        table.draw();

        // Save the document
        File file = new File("target/StrikethroughTest.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        doc.save(file);
        doc.close();
    }
}
