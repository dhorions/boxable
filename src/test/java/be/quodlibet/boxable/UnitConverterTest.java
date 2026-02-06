package be.quodlibet.boxable;

import be.quodlibet.boxable.utils.FontUtils;
import be.quodlibet.boxable.utils.UnitConverter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.Test;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class UnitConverterTest {

    @Test
    public void testUnitConversions() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float margin = 30f;
        float bottomMargin = 30f;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;

        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);

        // Title
        Row<PDPage> titleRow = table.createRow(20f);
        Cell<PDPage> titleCell = titleRow.createCell(100, "UnitConverter - Conversion Results");
        titleCell.setFillColor(new Color(41, 128, 185));
        titleCell.setTextColor(Color.WHITE);

        Row<PDPage> descRow = table.createRow(15f);
        descRow.createCell(100,
                "Expected: Each row shows an input, the converted output, and the expected value. All values should match.");

        // ── mm <-> points ──
        addSectionHeader(table, "Millimeters to Points (1 inch = 72pt = 25.4mm)");
        addConversionRow(table, "10 mm", fmt(UnitConverter.mmToPoints(10f)) + " pt", "~28.35 pt");
        addConversionRow(table, "25.4 mm (1 inch)", fmt(UnitConverter.mmToPoints(25.4f)) + " pt", "~72 pt");
        addConversionRow(table, "0 mm", fmt(UnitConverter.mmToPoints(0f)) + " pt", "0 pt");
        addConversionRow(table, "100 mm", fmt(UnitConverter.mmToPoints(100f)) + " pt", "~283.46 pt");

        addSectionHeader(table, "Points to Millimeters");
        addConversionRow(table, "72 pt (1 inch)", fmt(UnitConverter.pointsToMm(72f)) + " mm", "~25.4 mm");
        addConversionRow(table, "28.3465 pt", fmt(UnitConverter.pointsToMm(28.3465f)) + " mm", "~10 mm");
        addConversionRow(table, "0 pt", fmt(UnitConverter.pointsToMm(0f)) + " mm", "0 mm");

        // ── cm <-> points ──
        addSectionHeader(table, "Centimeters to Points");
        addConversionRow(table, "1 cm", fmt(UnitConverter.cmToPoints(1f)) + " pt", "~28.35 pt");
        addConversionRow(table, "2.54 cm (1 inch)", fmt(UnitConverter.cmToPoints(2.54f)) + " pt", "~72 pt");
        addConversionRow(table, "10 cm", fmt(UnitConverter.cmToPoints(10f)) + " pt", "~283.46 pt");

        addSectionHeader(table, "Points to Centimeters");
        addConversionRow(table, "72 pt (1 inch)", fmt(UnitConverter.pointsToCm(72f)) + " cm", "~2.54 cm");
        addConversionRow(table, "283.46 pt", fmt(UnitConverter.pointsToCm(283.46f)) + " cm", "~10 cm");

        // ── inches <-> points ──
        addSectionHeader(table, "Inches to Points");
        addConversionRow(table, "1 inch", fmt(UnitConverter.inchesToPoints(1f)) + " pt", "72 pt");
        addConversionRow(table, "8.5 inches (US Letter width)", fmt(UnitConverter.inchesToPoints(8.5f)) + " pt", "612 pt");
        addConversionRow(table, "11 inches (US Letter height)", fmt(UnitConverter.inchesToPoints(11f)) + " pt", "792 pt");

        addSectionHeader(table, "Points to Inches");
        addConversionRow(table, "72 pt", fmt(UnitConverter.pointsToInches(72f)) + " in", "1 in");
        addConversionRow(table, "612 pt (US Letter width)", fmt(UnitConverter.pointsToInches(612f)) + " in", "8.5 in");

        // ── Round-trip verification ──
        addSectionHeader(table, "Round-trip Verification (mm -> pt -> mm)");
        float original = 42.5f;
        float roundTrip = UnitConverter.pointsToMm(UnitConverter.mmToPoints(original));
        addConversionRow(table, "42.5 mm -> pt -> mm", fmt(roundTrip) + " mm", "~42.5 mm");

        // ── Cell font size in mm and cm ──
        addSectionHeader(table, "Cell.setFontSizeMm / getFontSizeMm");

        Row<PDPage> mmRow1 = table.createRow(13f);
        Cell<PDPage> mmCell = mmRow1.createCell(30, "setFontSizeMm(3)");
        Cell<PDPage> mmSample = mmRow1.createCell(35, "Sample text at 3mm");
        mmSample.setFontSizeMm(3f);
        float mmPtResult = mmSample.getFontSize();
        float mmBackResult = mmSample.getFontSizeMm();
        mmRow1.createCell(35, fmt(mmPtResult) + " pt / " + fmt(mmBackResult) + " mm");

        Row<PDPage> mmRow2 = table.createRow(13f);
        mmRow2.createCell(30, "setFontSizeMm(5)");
        Cell<PDPage> mmSample2 = mmRow2.createCell(35, "Sample text at 5mm");
        mmSample2.setFontSizeMm(5f);
        mmRow2.createCell(35, fmt(mmSample2.getFontSize()) + " pt / " + fmt(mmSample2.getFontSizeMm()) + " mm");

        addSectionHeader(table, "Cell.setFontSizeCm / getFontSizeCm");

        Row<PDPage> cmRow1 = table.createRow(13f);
        cmRow1.createCell(30, "setFontSizeCm(0.3)");
        Cell<PDPage> cmSample = cmRow1.createCell(35, "Sample text at 0.3cm");
        cmSample.setFontSizeCm(0.3f);
        float cmPtResult = cmSample.getFontSize();
        float cmBackResult = cmSample.getFontSizeCm();
        cmRow1.createCell(35, fmt(cmPtResult) + " pt / " + fmt(cmBackResult) + " cm");

        Row<PDPage> cmRow2 = table.createRow(13f);
        cmRow2.createCell(30, "setFontSizeCm(0.5)");
        Cell<PDPage> cmSample2 = cmRow2.createCell(35, "Sample text at 0.5cm");
        cmSample2.setFontSizeCm(0.5f);
        cmRow2.createCell(35, fmt(cmSample2.getFontSize()) + " pt / " + fmt(cmSample2.getFontSizeCm()) + " cm");

        // ── FontUtils.getStringWidthMm / getStringWidthCm ──
        PDFont helvetica = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        String testText = "Hello, World!";
        float testFontSize = 12f;
        float widthPt = FontUtils.getStringWidth(helvetica, testText, testFontSize);
        float widthMm = FontUtils.getStringWidthMm(helvetica, testText, testFontSize);
        float widthCm = FontUtils.getStringWidthCm(helvetica, testText, testFontSize);

        addSectionHeader(table, "FontUtils.getStringWidthMm / getStringWidthCm");
        addConversionRow(table, "\"" + testText + "\" @12pt",
                "Width: " + fmt(widthPt) + " pt", "Points (baseline)");
        addConversionRow(table, "getStringWidthMm",
                fmt(widthMm) + " mm", "~" + fmt(UnitConverter.pointsToMm(widthPt)) + " mm");
        addConversionRow(table, "getStringWidthCm",
                fmt(widthCm) + " cm", "~" + fmt(UnitConverter.pointsToCm(widthPt)) + " cm");

        // Another text sample
        String testText2 = "The quick brown fox";
        float widthPt2 = FontUtils.getStringWidth(helvetica, testText2, testFontSize);
        float widthMm2 = FontUtils.getStringWidthMm(helvetica, testText2, testFontSize);
        float widthCm2 = FontUtils.getStringWidthCm(helvetica, testText2, testFontSize);

        addConversionRow(table, "\"" + testText2 + "\" @12pt",
                "Width: " + fmt(widthPt2) + " pt", "Points (baseline)");
        addConversionRow(table, "getStringWidthMm",
                fmt(widthMm2) + " mm", "~" + fmt(UnitConverter.pointsToMm(widthPt2)) + " mm");
        addConversionRow(table, "getStringWidthCm",
                fmt(widthCm2) + " cm", "~" + fmt(UnitConverter.pointsToCm(widthPt2)) + " cm");

        table.draw();

        File file = new File("target/UnitConverterTest.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();

        assertTrue("PDF file should exist", file.exists());
        assertTrue("PDF file should not be empty", file.length() > 0);

        // ── Numeric assertions ──
        // mm <-> pt
        assertEquals(0f, UnitConverter.mmToPoints(0f), 0.001f);
        assertEquals(72f, UnitConverter.mmToPoints(25.4f), 0.1f);
        assertEquals(25.4f, UnitConverter.pointsToMm(72f), 0.1f);

        // cm <-> pt
        assertEquals(72f, UnitConverter.cmToPoints(2.54f), 0.1f);
        assertEquals(2.54f, UnitConverter.pointsToCm(72f), 0.1f);

        // inches <-> pt
        assertEquals(72f, UnitConverter.inchesToPoints(1f), 0.001f);
        assertEquals(1f, UnitConverter.pointsToInches(72f), 0.001f);
        assertEquals(612f, UnitConverter.inchesToPoints(8.5f), 0.001f);

        // round-trip
        assertEquals(original, UnitConverter.pointsToMm(UnitConverter.mmToPoints(original)), 0.01f);

        // double overloads
        assertEquals(72.0, UnitConverter.mmToPoints(25.4), 0.1);
        assertEquals(25.4, UnitConverter.pointsToMm(72.0), 0.1);
        assertEquals(72.0, UnitConverter.cmToPoints(2.54), 0.1);
        assertEquals(72.0, UnitConverter.inchesToPoints(1.0), 0.001);

        // Cell font size mm assertions (using cells created above, before draw)
        assertEquals(3f, mmSample.getFontSizeMm(), 0.01f);
        assertEquals(UnitConverter.mmToPoints(3f), mmSample.getFontSize(), 0.01f);
        assertEquals(5f, mmSample2.getFontSizeMm(), 0.01f);
        assertEquals(UnitConverter.mmToPoints(5f), mmSample2.getFontSize(), 0.01f);

        // Cell font size cm assertions
        assertEquals(0.3f, cmSample.getFontSizeCm(), 0.01f);
        assertEquals(UnitConverter.cmToPoints(0.3f), cmSample.getFontSize(), 0.01f);
        assertEquals(0.5f, cmSample2.getFontSizeCm(), 0.01f);
        assertEquals(UnitConverter.cmToPoints(0.5f), cmSample2.getFontSize(), 0.01f);

        // FontUtils string width mm/cm assertions
        assertEquals(UnitConverter.pointsToMm(widthPt), widthMm, 0.001f);
        assertEquals(UnitConverter.pointsToCm(widthPt), widthCm, 0.001f);
        assertEquals(UnitConverter.pointsToMm(widthPt2), widthMm2, 0.001f);
        assertEquals(UnitConverter.pointsToCm(widthPt2), widthCm2, 0.001f);
    }

    private void addSectionHeader(BaseTable table, String text) {
        Row<PDPage> row = table.createRow(15f);
        Cell<PDPage> cell = row.createCell(100, "<b>" + text + "</b>");
        cell.setFillColor(new Color(236, 240, 241));
    }

    private void addConversionRow(BaseTable table, String input, String result, String expected) {
        Row<PDPage> row = table.createRow(13f);
        row.createCell(30, input);
        row.createCell(35, result);
        row.createCell(35, expected);
    }

    private String fmt(float value) {
        return String.format("%.4f", value);
    }
}
