package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class TablePaginationTest {

    @Test
    public void testTablePaginationBehaviors() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float margin = 50;
        // A4 height is ~842.
        // Start drawing very low on the page (y=150) to simulate "not enough space"
        float yStart = 150; 
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - margin;

        // --- DESCRIPTION ---
        PDPageContentStream contentStream = new PDPageContentStream(doc, page);
        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
        contentStream.newLineAtOffset(margin, 750);
        contentStream.showText("TablePaginationTest: Verifying table placement logic.");
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("1. Small table starts at y=150. Should jump to Page 2.");
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("2. Large table follows immediately. Should break across pages.");
        contentStream.endText();
        contentStream.close();

        // --- TABLE 1: Small table, insufficient space on Page 1 ---
        // We expect this table to move entirely to Page 2.
        BaseTable table1 = new BaseTable(yStart, yStartNewPage, 30, tableWidth, margin, doc, page, true, true);
        table1.setStartNewPageIfTableDoesNotFit(true);

        Row<PDPage> headerRow1 = table1.createRow(20);
        Cell<PDPage> cell1 = headerRow1.createCell(100, "TABLE_1_HEADER_MOVED");
        cell1.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        table1.addHeaderRow(headerRow1);

        for (int i = 0; i < 6; i++) {
            table1.createRow(20).createCell(100, "T1 Row " + (i + 1));
        }

        // Draw Table 1 and capture position/page
        float yPosAfterTable1 = table1.draw(); 
        PDPage pageAfterTable1 = table1.getCurrentPage();

        // --- TABLE 2: Large table, > 1 page high ---
        // With setStartNewPageIfTableDoesNotFit(true), the library checks if the TOTAL height fits.
        // Since the table is huge (larger than the remaining space on Page 2), it will trigger 
        // a page break and start on Page 3 (a fresh page), effectively skipping the bottom of Page 2.
        
        float yStartTable2 = yPosAfterTable1 - 50; // Add some gap
        
        BaseTable table2 = new BaseTable(yStartTable2, yStartNewPage, 30, tableWidth, margin, doc, pageAfterTable1, true, true);
        table2.setStartNewPageIfTableDoesNotFit(true); 

        Row<PDPage> headerRow2 = table2.createRow(20);
        headerRow2.createCell(100, "TABLE_2_HEADER_LARGE");
        table2.addHeaderRow(headerRow2);
        
        // 60 rows * 20 = 1200 height (plus header) -> Definitely spans multiple pages
        for (int i = 0; i < 60; i++) {
            table2.createRow(20).createCell(100, "T2 Row " + (i + 1));
        }
        
        float yPosAfterTable2 = table2.draw();
        PDPage pageAfterTable2 = table2.getCurrentPage();

        // --- TABLE 3: Normal behavior (no forced break) ---
        // Should start immediately after Table 2 on whatever page Table 2 ended (Page 4 approx).
        
        float yStartTable3 = yPosAfterTable2 - 50; 
        
        BaseTable table3 = new BaseTable(yStartTable3, yStartNewPage, 30, tableWidth, margin, doc, pageAfterTable2, true, true);
        // We do NOT set setStartNewPageIfTableDoesNotFit(true), so it defaults to false.

        Row<PDPage> headerRow3 = table3.createRow(20);
        headerRow3.createCell(100, "TABLE_3_FOLLOWING");
        table3.addHeaderRow(headerRow3);
        
        for (int i = 0; i < 5; i++) {
            table3.createRow(20).createCell(100, "T3 Row " + (i + 1));
        }
        
        table3.draw();

        // --- SAVE AND ASSERT ---
        File file = new File("target/TablePaginationTest.pdf");
        System.out.println("Test PDF saved at : " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        doc.save(file);
        
        // Verify content on pages
        PDFTextStripper stripper = new PDFTextStripper();
        
        // Page 1: Should NOT have Table 1 Header (it moved)
        stripper.setStartPage(1);
        stripper.setEndPage(1);
        String p1 = stripper.getText(doc);
        Assert.assertFalse("Page 1 should not contain Table 1 header", p1.contains("TABLE_1_HEADER_MOVED"));
        Assert.assertFalse("Page 1 should not contain Table 2 header", p1.contains("TABLE_2_HEADER_LARGE"));
        
        // Page 2: Should have Table 1 Header. 
        // Table 2 is massive (1200+) and won't fit in the remaining space on Page 2, 
        // so "StartNewPageIfTableDoesNotFit" moves it to Page 3 (fresh page).
        stripper.setStartPage(2);
        stripper.setEndPage(2);
        String p2 = stripper.getText(doc);
        Assert.assertTrue("Page 2 should contain Table 1 header", p2.contains("TABLE_1_HEADER_MOVED"));
        Assert.assertFalse("Page 2 should NOT contain Table 2 header (it should move to p3)", p2.contains("TABLE_2_HEADER_LARGE"));

        // Page 3: Should contain Table 2 Header and start of rows
        stripper.setStartPage(3);
        stripper.setEndPage(3);
        String p3 = stripper.getText(doc);
        Assert.assertTrue("Page 3 should contain Table 2 header", p3.contains("TABLE_2_HEADER_LARGE"));
        Assert.assertTrue("Page 3 should contain T2 Row 1", p3.contains("T2 Row 1"));

        // Page 4: Should contain later rows of Table 2 AND Table 3
        stripper.setStartPage(4);
        stripper.setEndPage(4);
        String p4 = stripper.getText(doc);
        Assert.assertTrue("Page 4 should contain later rows of Table 2", p4.contains("T2 Row 50")); // approx
        Assert.assertTrue("Page 4 should contain Table 3 header (follows immediately)", p4.contains("TABLE_3_FOLLOWING"));

        doc.close();
    }
}
