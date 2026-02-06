package be.quodlibet.boxable;

import be.quodlibet.boxable.utils.FontUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Test;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class DefaultFontsTest {

    private static final String SAMPLE = "The quick brown fox jumps over the lazy dog. 0123456789";

    @Test
    public void allFontVariations() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float margin = 30f;
        float bottomMargin = 30f;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;
        float tableGap = 15f;

        // ── 1. Default fonts (Helvetica) ──
        FontUtils.getDefaultfonts().clear();
        yStart = drawFontSection(doc, page, yStart, yStartNewPage, bottomMargin, tableWidth, margin,
                "1. Default (Helvetica) - built-in, no custom fonts set",
                new Color(52, 152, 219));
        yStart -= tableGap;

        if (yStart < 200) {
            page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            yStart = yStartNewPage;
        }

        // ── 2. Sans fonts (Liberation Sans) ──
        FontUtils.setSansFontsAsDefault(doc);
        yStart = drawFontSection(doc, page, yStart, yStartNewPage, bottomMargin, tableWidth, margin,
                "2. Sans (Liberation Sans) - embedded TTF via setSansFontsAsDefault",
                new Color(46, 204, 113));
        yStart -= tableGap;

        if (yStart < 200) {
            page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            yStart = yStartNewPage;
        }

        // ── 3. Serif fonts (Times Roman) ──
        FontUtils.setSerifFontsAsDefault(doc);
        yStart = drawFontSection(doc, page, yStart, yStartNewPage, bottomMargin, tableWidth, margin,
                "3. Serif (Times Roman) - built-in via setSerifFontsAsDefault",
                new Color(155, 89, 182));
        yStart -= tableGap;

        if (yStart < 200) {
            page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            yStart = yStartNewPage;
        }

        // ── 4. Monospace fonts (Courier) ──
        FontUtils.setMonoSpaceFontsAsDefault(doc);
        yStart = drawFontSection(doc, page, yStart, yStartNewPage, bottomMargin, tableWidth, margin,
                "4. Monospace (Courier) - built-in via setMonoSpaceFontsAsDefault",
                new Color(231, 76, 60));

        // Clean up thread-local
        FontUtils.getDefaultfonts().clear();

        File file = new File("target/DefaultFontsTest.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();

        assertTrue("PDF file should exist", file.exists());
        assertTrue("PDF file should not be empty", file.length() > 0);
    }

    private float drawFontSection(PDDocument doc, PDPage page,
                                  float yStart, float yStartNewPage, float bottomMargin,
                                  float tableWidth, float margin,
                                  String title, Color titleColor) throws IOException {

        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);

        // Title
        Row<PDPage> titleRow = table.createRow(18f);
        Cell<PDPage> titleCell = titleRow.createCell(100, title);
        titleCell.setFillColor(titleColor);
        titleCell.setTextColor(Color.WHITE);

        // Header
        Row<PDPage> headerRow = table.createRow(14f);
        headerRow.createCell(20, "<b>Style</b>");
        headerRow.createCell(80, "<b>Sample Text</b>");
        table.addHeaderRow(headerRow);

        // Normal
        Row<PDPage> r1 = table.createRow(14f);
        r1.createCell(20, "Normal");
        r1.createCell(80, SAMPLE);

        // Bold
        Row<PDPage> r2 = table.createRow(14f);
        r2.createCell(20, "Bold");
        r2.createCell(80, "<b>" + SAMPLE + "</b>");

        // Italic
        Row<PDPage> r3 = table.createRow(14f);
        r3.createCell(20, "Italic");
        r3.createCell(80, "<i>" + SAMPLE + "</i>");

        // Bold Italic
        Row<PDPage> r4 = table.createRow(14f);
        r4.createCell(20, "Bold Italic");
        r4.createCell(80, "<b><i>" + SAMPLE + "</i></b>");

        // Mixed
        Row<PDPage> r5 = table.createRow(14f);
        r5.createCell(20, "Mixed");
        r5.createCell(80, "Normal <b>bold</b> <i>italic</i> <b><i>bold-italic</i></b> normal again");

        return table.draw();
    }
}
