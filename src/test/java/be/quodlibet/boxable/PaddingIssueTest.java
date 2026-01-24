package be.quodlibet.boxable;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class PaddingIssueTest {

    @Test
    public void testPaddingIssue() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);

        // Table width 200
        float tableWidth = 200f;
        float margin = 10f;
        
        BaseTable table = new BaseTable(500, 500, 50, tableWidth, margin, doc, page, true, true);
        Row<PDPage> row = table.createRow(20f);
        
        // Create a long word "Liiii...ng" 
        // 100 chars 'i'
        StringBuilder sb = new StringBuilder("Vikram L");
        for(int i=0; i<100; i++) {
            sb.append("i");
        }
        String longText = sb.toString() + "ng";
        
        // 50% width = 100 points
        Cell<PDPage> cell = row.createCell(50f, longText);
        cell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA));
        cell.setFontSize(12);

        // Default padding is 5 left, 5 right.
        // Inner width = 90.
        
        Paragraph p = cell.getParagraph();
        
        table.draw();

        File file = new File("target/PaddingIssueTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();
        System.out.println("PDF saved to " + file.getAbsolutePath());
    }

    @Test
    public void testWrappingScenarios() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);
        
        float margin = 10;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStart = 700;
        float bottomMargin = 50;

        BaseTable table = new BaseTable(yStart, yStart, bottomMargin, tableWidth, margin, doc, page, true, true);

        // Header
        Row<PDPage> headerRow = table.createRow(15f);
        headerRow.createCell(100, "Wrapping Scenarios").setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        table.addHeaderRow(headerRow);

        // Scenario 1: Normal text with padding
        Row<PDPage> row = table.createRow(20f);
        Cell<PDPage> cell = row.createCell(50, "This is a normal text that should wrap properly with padding.");
        cell.setTopPadding(10);
        cell.setBottomPadding(10);
        cell.setLeftPadding(10);
        cell.setRightPadding(10);
        row.createCell(50, "Padding: 10, 10, 10, 10");

        // Scenario 2: Long word with padding
        row = table.createRow(20f);
        cell = row.createCell(50, "ThisIsAVeryLongWordThatShouldWrapEvenWithPaddingAppliedToTheCell");
        cell.setLeftPadding(20);
        cell.setRightPadding(20);
        row.createCell(50, "Padding: Left/Right 20");

        // Scenario 3: Mixed text
        row = table.createRow(20f);
        cell = row.createCell(50, "Start ThisIsAVeryLongWordThatShouldWrapEvenWithPaddingAppliedToTheCell End");
        cell.setLeftPadding(5);
        cell.setRightPadding(5);
        row.createCell(50, "Mixed content, padding 5");

        // Scenario 4: No Padding, Narrow Column
        row = table.createRow(20f);
        cell = row.createCell(30, "This text has zero padding and is in a narrow column to force wrapping multiple times. " +
                "It should go right up to the border.");
        cell.setTopPadding(0);
        cell.setBottomPadding(0);
        cell.setLeftPadding(0);
        cell.setRightPadding(0);
        row.createCell(70, "Padding: 0, Width: 30%");
        
        table.draw();
        
        File file = new File("target/WrappingScenariosTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();
        System.out.println("PDF saved to " + file.getAbsolutePath());
    }
}
