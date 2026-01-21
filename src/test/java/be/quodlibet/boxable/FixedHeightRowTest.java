package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class FixedHeightRowTest {

    @Test
    public void cellHeightSmallerThanTextHeight() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float margin = 50f;
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = 70f;
        boolean drawContent = true;

        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true,
                drawContent);

        // Header row with fixed small height (text should shrink to fit)
        Row<PDPage> headerRow = table.createRow(12f);
        headerRow.setHeight(12f);
        headerRow.setFixedHeight(true);
        Cell<PDPage> headerCell = headerRow.createCell(100, "Header with longer text");
        headerCell.setFontSize(14f);
        headerCell.setTopPadding(1f);
        headerCell.setBottomPadding(1f);
        table.addHeaderRow(headerRow);

        // Fixed-height data row (text should shrink to fit)
        Row<PDPage> fixedRow = table.createRow(12f);
        fixedRow.setFixedHeight(true);
        Cell<PDPage> fixedLeft = fixedRow.createCell(30, "Fixed row");
        Cell<PDPage> fixedRight = fixedRow.createCell(70, "Some value that should be reduced to fit in 12pt height");
        fixedLeft.setFontSize(14f);
        fixedRight.setFontSize(14f);
        fixedLeft.setTopPadding(1f);
        fixedLeft.setBottomPadding(1f);
        fixedRight.setTopPadding(1f);
        fixedRight.setBottomPadding(1f);

        // Non-fixed row (height should grow to fit text)
        Row<PDPage> flexibleRow = table.createRow(12f);
        flexibleRow.createCell(30, "Flexible row");
        Cell<PDPage> flexibleRight = flexibleRow.createCell(70,
                "Some value that should keep its font size and expand the row height");
        flexibleRight.setFontSize(14f);

        // Expected behavior description for manual verification
        Row<PDPage> expectedRow = table.createRow(18f);
        expectedRow.createCell(100,
            "Expected behavior after fix: header row and the fixed-height data row shrink text to fit their 12pt heights. The flexible row keeps its font size and grows taller to fit content.");

        float yAfterFirstTable = table.draw();

        // Second table with different font sizes and multi-line content
        float secondTableStart = yAfterFirstTable - 20f;
        BaseTable secondTable = new BaseTable(secondTableStart, yStartNewPage, bottomMargin, tableWidth, margin, doc,
                page, true, drawContent);

        Row<PDPage> secondHeader = secondTable.createRow(14f);
        secondHeader.setHeight(14f);
        secondHeader.setFixedHeight(true);
        Cell<PDPage> secondHeaderCell = secondHeader.createCell(100, "Second table header with longer text");
        secondHeaderCell.setFontSize(16f);
        secondHeaderCell.setTopPadding(1f);
        secondHeaderCell.setBottomPadding(1f);
        secondTable.addHeaderRow(secondHeader);

        Row<PDPage> multiLineFixed = secondTable.createRow(14f);
        multiLineFixed.setFixedHeight(true);
        Cell<PDPage> multiLineLeft = multiLineFixed.createCell(40, "Fixed row");
        Cell<PDPage> multiLineRight = multiLineFixed.createCell(60,
                "This is a longer value that should wrap to multiple lines and shrink to fit within a fixed height. It should clearly span more than one line in the fixed-height row.");
        multiLineLeft.setFontSize(12f);
        multiLineRight.setFontSize(12f);
        multiLineLeft.setTopPadding(1f);
        multiLineLeft.setBottomPadding(1f);
        multiLineRight.setTopPadding(1f);
        multiLineRight.setBottomPadding(1f);

        Row<PDPage> multiLineFlexible = secondTable.createRow(12f);
        Cell<PDPage> flexLeft = multiLineFlexible.createCell(40, "Flexible row");
        Cell<PDPage> flexRight = multiLineFlexible.createCell(60,
                "This is a longer value that should keep its font size and expand the row height to show all lines");
        flexLeft.setFontSize(12f);
        flexRight.setFontSize(12f);

        Row<PDPage> expectedRow2 = secondTable.createRow(18f);
        expectedRow2.createCell(100,
                "Expected behavior after fix (table 2): header and fixed-height rows shrink text to fit. Flexible rows keep font size and grow to display multiple lines.");

        float yAfterSecondTable = secondTable.draw();

        // Third table with header fixed-height disabled
        float thirdTableStart = yAfterSecondTable - 20f;
        BaseTable thirdTable = new BaseTable(thirdTableStart, yStartNewPage, bottomMargin, tableWidth, margin, doc,
                page, true, drawContent);

        Row<PDPage> thirdHeader = thirdTable.createRow(12f);
        Cell<PDPage> thirdHeaderCell = thirdHeader.createCell(100,
                "Third table header (fixed height disabled): this header should expand its height instead of shrinking text");
        thirdHeaderCell.setFontSize(14f);
        thirdTable.addHeaderRow(thirdHeader);

        Row<PDPage> thirdRow = thirdTable.createRow(12f);
        thirdRow.createCell(100, "Normal row content below header");

        Row<PDPage> expectedRow3 = thirdTable.createRow(18f);
        expectedRow3.createCell(100,
                "Expected behavior after fix (table 3): header fixed-height disabled, so header grows taller to fit text at the original font size.");

        thirdTable.draw();

        File file = new File("target/FixedHeightRowTest.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }
}
