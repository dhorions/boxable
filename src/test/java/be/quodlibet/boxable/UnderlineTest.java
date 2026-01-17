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

public class UnderlineTest {

    @Test
    public void testUnderlineRendering() throws IOException {
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

        // Create Cells with Underline tags
        Cell<PDPage> cell = row.createCell(50, "Normal <u>Underline</u> Normal");
        cell = row.createCell(50, "<u>All Underline</u>");
        
        row = table.createRow(20);
        cell = row.createCell(50, "Mixed <b>Bold <u>Underline</u></b> text");
        cell = row.createCell(50, "Mixed <i>Italic <u>Underline</u></i> text");

        // Draw Table
        table.draw();

        // Save the document
        File file = new File("target/UnderlineTest.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        doc.save(file);
        doc.close();
    }
}
