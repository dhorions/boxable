package be.quodlibet.boxable;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.Test;

public class LargeRowIssueTest {

    @Test
    public void testLargeRowPageBreak() throws IOException {
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

        // Create Header row
        Row<PDPage> headerRow = table.createRow(15f);
        Cell<PDPage> cell = headerRow.createCell(100, "Large Row Test");
        table.addHeaderRow(headerRow);

        // Create a large row
        Row<PDPage> row = table.createRow(10f);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 500; i++) {
            sb.append("This text has no business meaning, no narrative arc, and no mercy for layout engines that dislike large cells. ");
            sb.append(i).append(" ");
        }
        
        // This text should be long enough to span multiple pages or at least fill one page
        cell = row.createCell(100, sb.toString());
        cell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA));
        cell.setFontSize(10);

        table.draw();

        // Verify page count
        System.out.println("Total pages generated: " + doc.getNumberOfPages());
        if (doc.getNumberOfPages() < 2) {
             throw new RuntimeException("Expected split to create additional pages. Found only " + doc.getNumberOfPages());
        }

        // Save the document
        File file = new File("target/LargeRowIssueTest.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();
    }
}
