package be.quodlibet.boxable;

import java.io.File;
import java.io.IOException;
import be.quodlibet.boxable.page.PageProvider;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BottomTablePlacementTest {
  

    @Test
    public void bottomTableMovesToNextPageWhenTooTall() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float margin = 40f;
        float bottomMargin = 70f;
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);

        // Top table at the top of page 1.
        BaseTable topTable = new BaseTable(yStartNewPage, yStartNewPage, bottomMargin, tableWidth, margin,
                doc, page, true, true);
        Row<PDPage> topHeader = topTable.createRow(16f);
        topHeader.createCell(100, "Top table header");
        topTable.addHeaderRow(topHeader);

        for (int i = 1; i <= 20; i++) {
            Row<PDPage> row = topTable.createRow(12f);
            row.createCell(100, "TOP ROW " + i);
        }

        Table.DrawResult<PDPage> topResult = topTable.drawAndGetPosition();

        // Measure how many rows will fit on the next page
        BaseTable bottomMeasure = new BaseTable(yStartNewPage, yStartNewPage, bottomMargin, tableWidth, margin,
                doc, page, true, true);
        Row<PDPage> bottomHeader = bottomMeasure.createRow(16f);
        bottomHeader.createCell(100, "Bottom table header (measurement)");
        bottomMeasure.addHeaderRow(bottomHeader);
        Row<PDPage> expectedRowMeasure = bottomMeasure.createRow(22f);
        expectedRowMeasure.createCell(100,
                "Expected behavior: if the bottom table is too tall to fit below the top table, it is drawn at the bottom of the next page.");
        float rowHeight = 12f;
        int maxRows = 20;
        // Calculate available space for bottom table on page 2 (no top table)
        float availableSpacePage2 = yStartNewPage - bottomMargin - bottomHeader.getHeight() - expectedRowMeasure.getHeight();
        int rowsFitPage2 = (int) (availableSpacePage2 / rowHeight);
        if (rowsFitPage2 > maxRows) rowsFitPage2 = maxRows;
        for (int i = 1; i <= rowsFitPage2; i++) {
            bottomMeasure.createRow(rowHeight).createCell(100, "BOTTOM ROW " + i);
        }
        float bottomHeight = bottomMeasure.getHeaderAndDataHeight();

        float availableSpaceOnPage1 = topResult.getYStart() - bottomMargin;
        boolean placeOnNextPage = bottomHeight > availableSpaceOnPage1;

        PDPage targetPage = topResult.getPage();
        int targetPageIndex = 1;
        if (placeOnNextPage) {
            targetPage = new PDPage(PDRectangle.A4);
            doc.addPage(targetPage);
            targetPageIndex = 2;
        }

        // Place the bottom table so it fits exactly on page 2
        float bottomYStart = bottomMargin + bottomMeasure.getHeaderAndDataHeight();
        BaseTable bottomTable = new BaseTable(bottomYStart, yStartNewPage, bottomMargin, tableWidth, margin,
            doc, targetPage, true, true);

        Row<PDPage> bottomHeaderDraw = bottomTable.createRow(16f);
        bottomHeaderDraw.createCell(100, "Bottom table page " + targetPageIndex);
        bottomTable.addHeaderRow(bottomHeaderDraw);

        Row<PDPage> expectedRow = bottomTable.createRow(22f);
        expectedRow.createCell(100,
                "Expected behavior: if the bottom table is too tall to fit below the top table, it is drawn at the bottom of the next page.");

        for (int i = 1; i <= rowsFitPage2; i++) {
            Row<PDPage> row = bottomTable.createRow(rowHeight);
            row.createCell(100, "BOTTOM ROW " + i);
        }

        bottomTable.draw();

        String text = new PDFTextStripper().getText(doc);

        File file = new File("target/BottomTablePlacementTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
            assertTrue(file.exists());
            assertTrue(file.length() > 0);
            assertTrue(text.contains("Top table header"));
            assertTrue(text.contains("Bottom table page 2"));
            assertTrue(text.contains("Expected behavior"));
            assertTrue(doc.getNumberOfPages() == 2);
            doc.close();
    }
}
