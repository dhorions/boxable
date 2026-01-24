package be.quodlibet.boxable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Assert;
import org.junit.Test;

public class ReproductionTestCase {

    @Test
    public void testCoordinateConsistencyWithCorrectUsage() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float pageHeight = page.getMediaBox().getHeight(); // ~842
        float pageTopMargin = 50;
        float pageBottomMargin = 50;
        
        // Correct Usage:
        // yStart: Where content starts on THIS page (usually PageHeight - Margin)
        float yStart = pageHeight - pageTopMargin; 
        
        // yStartNewPage: The reference top for NEW pages (usually PageHeight).
        // The library will subtract pageTopMargin from this value.
        float yStartNewPage = pageHeight; 
        
        float tableWidth = page.getMediaBox().getWidth() - 100;
        float margin = 50;

        BaseTable table = new BaseTable(yStart, yStartNewPage, pageTopMargin, pageBottomMargin, tableWidth, margin, doc, page, true, true);

        // Header
        Row<PDPage> headerRow = table.createRow(20f);
        table.addHeaderRow(headerRow);

        final List<Float> rowYPositions = new ArrayList<>();
        final List<Integer> pageNumbers = new ArrayList<>();

        int numRows = 50;
        float rowHeight = 20f;
        for (int i = 0; i < numRows; i++) {
            Row<PDPage> row = table.createRow(rowHeight);
            Cell<PDPage> cell = row.createCell(100f, "Row " + i);
            cell.addContentDrawnListener(new CellContentDrawnListener<PDPage>() {
                @Override
                public void onContentDrawn(Cell<PDPage> cell, PDDocument document, PDPage page, PDRectangle rectangle) {
                    rowYPositions.add(rectangle.getLowerLeftY());
                    pageNumbers.add(doc.getPages().indexOf(page));
                }
            });
        }

        table.draw();

        // Check consistency
        // Get Y of first row on Page 1 (Index 0)
        float row0Y = rowYPositions.get(0);
        
        // Find first row on Page 2
        int firstRowPage2Index = -1;
        for (int i = 0; i < pageNumbers.size(); i++) {
            if (pageNumbers.get(i) == 1) { // Page index 1 is the 2nd page
                firstRowPage2Index = i;
                break;
            }
        }
        
        Assert.assertTrue("Should have spilled to second page", firstRowPage2Index != -1);
        float rowPage2Y = rowYPositions.get(firstRowPage2Index);

        System.out.println("Row 0 Y (Page 1): " + row0Y);
        System.out.println("First Row Y (Page 2): " + rowPage2Y);
        
        // They should be identical because:
        // Page 1 Start = 842 - 50 = 792.
        // Page 2 Start = 842 - 50 = 792.
        // Header height (20) is same.
        // Row height (20) is same.
        Assert.assertEquals("Y position of first row on new page should match first row on first page", row0Y, rowPage2Y, 0.01f);

        File file = new File("target/ReproductionTestCaseCorrect.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();
    }
}
