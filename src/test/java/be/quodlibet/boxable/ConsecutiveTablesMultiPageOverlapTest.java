package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertTrue;

public class ConsecutiveTablesMultiPageOverlapTest {

    @Test
    public void consecutiveTablesAfterMultiPageDrawOverlap() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float margin = 40f;
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = 60f;
        boolean drawContent = true;

        BaseTable firstTable = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true,
                drawContent);

        Row<PDPage> header = firstTable.createRow(16f);
        header.createCell(100, "Table 1: Large table that spans multiple pages");
        firstTable.addHeaderRow(header);

        for (int i = 1; i <= 180; i++) {
            Row<PDPage> row = firstTable.createRow(12f);
            row.createCell(100, "Row " + i + " of Table 1");
        }

        Table.DrawResult<PDPage> firstTablePosition = firstTable.drawAndGetPosition();

        float secondTableStart = firstTablePosition.getYStart() - 10f;
        BaseTable secondTable = new BaseTable(secondTableStart, yStartNewPage, bottomMargin, tableWidth, margin, doc,
            firstTablePosition.getPage(), true, drawContent);

        Row<PDPage> secondHeader = secondTable.createRow(16f);
        secondHeader.createCell(100, "Table 2: should appear on the last page, below Table 1");
        secondTable.addHeaderRow(secondHeader);

        Row<PDPage> expectedRow = secondTable.createRow(22f);
        expectedRow.createCell(100,
            "Expected behavior: Table 2 is placed after the last row of Table 1 on the final page when using "
                + "drawAndGetPosition(). It should not overlap Table 1 on page 1.");

        Row<PDPage> dataRow = secondTable.createRow(12f);
        dataRow.createCell(100, "Table 2 content row");

        secondTable.draw();

        File file = new File("target/ConsecutiveTablesMultiPageOverlapTest.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        file.getParentFile().mkdirs();

        int pageCount = doc.getNumberOfPages();
        doc.save(file);
        doc.close();

        assertTrue(pageCount > 1);
        assertTrue(file.exists());
        assertTrue(Files.size(file.toPath()) > 0);
    }
}
