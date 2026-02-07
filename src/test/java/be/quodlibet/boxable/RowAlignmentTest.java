package be.quodlibet.boxable;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for row-level horizontal alignment (left, center, right).
 * <p>
 * Demonstrates rows that don't fill the full table width and are
 * anchored to the left, center, or right of the table.
 * </p>
 */
public class RowAlignmentTest {

    @Test
    public void testRowAlignment() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float pageWidth = page.getMediaBox().getWidth();
        float margin = 50;
        float tableWidth = pageWidth - 2 * margin;
        float yStart = page.getMediaBox().getHeight() - margin;

        BaseTable table = new BaseTable(yStart, yStart, margin, margin, tableWidth, margin,
                doc, page, true, true);

        // ============================================
        // Description row (full width)
        // ============================================
        Row<PDPage> descRow = table.createRow(20);
        Cell<PDPage> descCell = descRow.createCell(100, 
            "Row Alignment Test: Rows below use only 60% of table width. "
            + "Expect LEFT rows flush-left, RIGHT rows flush-right, "
            + "CENTER rows centered within the table.");
        descCell.setFontSize(9);

        // ============================================
        // Section 1: LEFT alignment (default)
        // ============================================
        Row<PDPage> labelLeft = table.createRow(15);
        Cell<PDPage> labelLeftCell = labelLeft.createCell(100, "LEFT alignment (default)");
        labelLeftCell.setFillColor(new Color(220, 220, 220));
        labelLeftCell.setFontSize(10);

        Row<PDPage> leftRow = table.createRow(15);
        leftRow.setRowAlignment(HorizontalAlignment.LEFT);
        Cell<PDPage> leftCell1 = leftRow.createCell(20, "Left-1");
        leftCell1.setFillColor(new Color(173, 216, 230));
        Cell<PDPage> leftCell2 = leftRow.createCell(20, "Left-2");
        leftCell2.setFillColor(new Color(144, 238, 144));
        Cell<PDPage> leftCell3 = leftRow.createCell(20, "Left-3");
        leftCell3.setFillColor(new Color(255, 255, 153));

        // ============================================
        // Section 2: RIGHT alignment
        // ============================================
        Row<PDPage> labelRight = table.createRow(15);
        Cell<PDPage> labelRightCell = labelRight.createCell(100, "RIGHT alignment");
        labelRightCell.setFillColor(new Color(220, 220, 220));
        labelRightCell.setFontSize(10);

        Row<PDPage> rightRow = table.createRow(15);
        rightRow.setRowAlignment(HorizontalAlignment.RIGHT);
        Cell<PDPage> rightCell1 = rightRow.createCell(20, "Right-1");
        rightCell1.setFillColor(new Color(173, 216, 230));
        Cell<PDPage> rightCell2 = rightRow.createCell(20, "Right-2");
        rightCell2.setFillColor(new Color(144, 238, 144));
        Cell<PDPage> rightCell3 = rightRow.createCell(20, "Right-3");
        rightCell3.setFillColor(new Color(255, 255, 153));

        // ============================================
        // Section 3: CENTER alignment
        // ============================================
        Row<PDPage> labelCenter = table.createRow(15);
        Cell<PDPage> labelCenterCell = labelCenter.createCell(100, "CENTER alignment");
        labelCenterCell.setFillColor(new Color(220, 220, 220));
        labelCenterCell.setFontSize(10);

        Row<PDPage> centerRow = table.createRow(15);
        centerRow.setRowAlignment(HorizontalAlignment.CENTER);
        Cell<PDPage> centerCell1 = centerRow.createCell(20, "Center-1");
        centerCell1.setFillColor(new Color(173, 216, 230));
        Cell<PDPage> centerCell2 = centerRow.createCell(20, "Center-2");
        centerCell2.setFillColor(new Color(144, 238, 144));
        Cell<PDPage> centerCell3 = centerRow.createCell(20, "Center-3");
        centerCell3.setFillColor(new Color(255, 255, 153));

        // ============================================
        // Section 4: Mixed - full-width row then right-aligned row
        // ============================================
        Row<PDPage> labelMixed = table.createRow(15);
        Cell<PDPage> labelMixedCell = labelMixed.createCell(100,
                "Mixed: full-width row above, right-aligned row below");
        labelMixedCell.setFillColor(new Color(220, 220, 220));
        labelMixedCell.setFontSize(10);

        Row<PDPage> fullRow = table.createRow(15);
        Cell<PDPage> fullCell1 = fullRow.createCell(50, "Full width col 1");
        fullCell1.setFillColor(new Color(255, 200, 200));
        Cell<PDPage> fullCell2 = fullRow.createCell(50, "Full width col 2");
        fullCell2.setFillColor(new Color(200, 255, 200));

        Row<PDPage> rightRow2 = table.createRow(15);
        rightRow2.setRowAlignment(HorizontalAlignment.RIGHT);
        Cell<PDPage> rightCell2a = rightRow2.createCell(15, "Total:");
        rightCell2a.setFillColor(new Color(255, 228, 196));
        rightCell2a.setAlign(HorizontalAlignment.RIGHT);
        Cell<PDPage> rightCell2b = rightRow2.createCell(15, "$1,234.56");
        rightCell2b.setFillColor(new Color(255, 228, 196));
        rightCell2b.setAlign(HorizontalAlignment.RIGHT);

        // ============================================
        // Section 5: Multiple widths with right alignment
        // ============================================
        Row<PDPage> labelWidths = table.createRow(15);
        Cell<PDPage> labelWidthsCell = labelWidths.createCell(100,
                "RIGHT alignment with 40% total width (two cells: 15% + 25%)");
        labelWidthsCell.setFillColor(new Color(220, 220, 220));
        labelWidthsCell.setFontSize(10);

        Row<PDPage> wideRightRow = table.createRow(15);
        wideRightRow.setRowAlignment(HorizontalAlignment.RIGHT);
        Cell<PDPage> wideRight1 = wideRightRow.createCell(15, "Label");
        wideRight1.setFillColor(new Color(221, 160, 221));
        Cell<PDPage> wideRight2 = wideRightRow.createCell(25, "Value field");
        wideRight2.setFillColor(new Color(176, 224, 230));

        table.draw();

        // ============================================
        // Assertions
        // ============================================

        // LEFT alignment: offset should be 0
        assertEquals(HorizontalAlignment.LEFT, leftRow.getRowAlignment());
        assertEquals(0, leftRow.getAlignmentOffset(), 0.01f);

        // RIGHT alignment: offset should equal gap (40% of table width)
        assertEquals(HorizontalAlignment.RIGHT, rightRow.getRowAlignment());
        float expectedRightOffset = tableWidth * 0.40f;
        assertEquals(expectedRightOffset, rightRow.getAlignmentOffset(), 1.0f);

        // CENTER alignment: offset should be half the gap
        assertEquals(HorizontalAlignment.CENTER, centerRow.getRowAlignment());
        float expectedCenterOffset = tableWidth * 0.20f;
        assertEquals(expectedCenterOffset, centerRow.getAlignmentOffset(), 1.0f);

        // Full-width row should have zero offset regardless of alignment
        assertEquals(0, fullRow.getAlignmentOffset(), 0.01f);

        // Right-aligned with 30% width: offset = 70% of table width
        assertEquals(HorizontalAlignment.RIGHT, rightRow2.getRowAlignment());
        float expected70Offset = tableWidth * 0.70f;
        assertEquals(expected70Offset, rightRow2.getAlignmentOffset(), 1.0f);

        File file = new File("target/RowAlignmentTest.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();
    }
}
