package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class HeadingTagNormalCellTest {

    @Test
    public void headingTagsShouldRenderInNormalCells() throws IOException {
        float margin = 40f;
        float bottomMargin = 70f;

        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;

        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);

        Row<PDPage> row = table.createRow(180f);
        row.createCell(100,
            "<h1>Heading Level One</h1>"
                + "<h2>Heading Level Two</h2>"
                + "<h3>Heading Level Three</h3>"
                + "<h4>Heading Level Four</h4>"
                + "<h5>Heading Level Five</h5>"
                + "<h6>Heading Level Six</h6>"
                + "<p>Normal text</p>"
                + "<p><b>Unbalanced bold</p>"
                + "<i>Unbalanced italic"
                + "<u>Unbalanced underline</u></i>"
                + "<s>Strike <b>through</s> mixed</b>"
                + "<h1>Heading mismatch</h2>"
                + "<h2>Nested <b>bold</h2> text</b>"
                + "<h3>Heading <i>italic</h4> mismatch"
                + "<h4>Heading <u>underline</h4> mismatch"
                + "<h5>Heading <b>bold</h5> mismatch"
                + "<h6>Heading <i>italic</h6> mismatch"
                + "<h1>T<b>est</h1>"
                + "<p>Broken <b>tags <i>overlap</p></b></i>"
                + "<p>List open <ul><li>Item one<li>Item two</ul>"
                + "<ol><li>First<li><b>Second</b></ol>"
                + "<p>Extra closing tags</b></i></u></p>"
                + "<p>Unknown <unknown>tag</unknown></p>");

        Row<PDPage> expectedRow = table.createRow(28f);
        expectedRow.createCell(100,
            "Expected behavior: All headings and supported tags render without errors. "
                + "Malformed tags are tolerated, and text still appears in reading order.");

        table.draw();

        String text = new PDFTextStripper().getText(doc);
        String normalizedText = text.replaceAll("\\s+", " ").trim();

        File file = new File("target/HeadingTagNormalCellTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        assertTrue(normalizedText.contains("Heading Level One"));
        assertTrue(normalizedText.contains("Heading Level Two"));
        assertTrue(normalizedText.contains("Heading Level Three"));
        assertTrue(normalizedText.contains("Heading Level Four"));
        assertTrue(normalizedText.contains("Heading Level Five"));
        assertTrue(normalizedText.contains("Heading Level Six"));
        assertTrue(normalizedText.contains("Normal text"));
        assertTrue(normalizedText.contains("Heading mismatch"));
        assertTrue(normalizedText.contains("Unbalanced bold"));
    }
}
