package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ParagraphTest {

    private static PDType1Font HELVETICA;
    private static PDType1Font COURIER;

    @BeforeClass
    public static void setUpClass() {
        HELVETICA = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        COURIER   = new PDType1Font(Standard14Fonts.FontName.COURIER);
    }

    // ── Memoization ──────────────────────────────────────────────────────────

    @Test
    public void getLines_isMemoized() {
        Paragraph p = new Paragraph("Test text", HELVETICA, 12f, 200f, HorizontalAlignment.LEFT);
        List<String> lines1 = p.getLines();
        List<String> lines2 = p.getLines();
        assertSame(lines1, lines2);
    }

    @Test
    public void getLines_emptyText_returnsNonNull() {
        Paragraph p = new Paragraph("", HELVETICA, 12f, 200f, HorizontalAlignment.LEFT);
        assertNotNull(p.getLines());
    }

    // ── Height ───────────────────────────────────────────────────────────────

    @Test
    public void emptyParagraph_hasNonNegativeHeight() {
        Paragraph p = new Paragraph("", HELVETICA, 12f, 200f, HorizontalAlignment.LEFT);
        assertTrue(p.getHeight() >= 0);
    }

    @Test
    public void multipleLines_haveGreaterHeightThanSingleLine() {
        Paragraph single = new Paragraph("Short", HELVETICA, 12f, 200f, HorizontalAlignment.LEFT);
        Paragraph multi  = new Paragraph("Line1<br>Line2<br>Line3", HELVETICA, 12f, 200f, HorizontalAlignment.LEFT);
        assertTrue(multi.getHeight() > single.getHeight());
    }

    // ── maxLineWidth ─────────────────────────────────────────────────────────

    @Test
    public void getMaxLineWidth_returnsPositiveValue() {
        Paragraph p = new Paragraph("Short text", HELVETICA, 12f, 500f, HorizontalAlignment.LEFT);
        p.getLines(); // force computation
        assertTrue(p.getMaxLineWidth() > 0);
        assertTrue(p.getMaxLineWidth() <= 500f);
    }

    // ── Wrapping ─────────────────────────────────────────────────────────────

    @Test
    public void narrowerWidthCausesMoreWrapping() {
        String text = "This is some text that will need to wrap at some point";
        Paragraph wide   = new Paragraph(text, HELVETICA, 12f, 400f, HorizontalAlignment.LEFT);
        Paragraph narrow = new Paragraph(text, HELVETICA, 12f, 100f, HorizontalAlignment.LEFT);
        assertTrue(narrow.getLines().size() >= wide.getLines().size());
    }

    // ── Ordered list numbering ────────────────────────────────────────────────

    @Test
    public void orderedList_generatesCorrectNumbering() {
        Paragraph p = new Paragraph(
            "<ol><li>First</li><li>Second</li><li>Third</li></ol>",
            HELVETICA, 12f, 300f, HorizontalAlignment.LEFT);

        List<String> lines = p.getLines();
        assertNotNull(lines);
        assertTrue(lines.size() >= 3);
        assertTrue(lines.stream().anyMatch(l -> l.contains("1.")));
        assertTrue(lines.stream().anyMatch(l -> l.contains("2.")));
        assertTrue(lines.stream().anyMatch(l -> l.contains("3.")));
    }

    @Test
    public void orderedList_twentyItems_numbersCorrectly() {
        StringBuilder html = new StringBuilder("<ol>");
        for (int i = 1; i <= 20; i++) {
            html.append("<li>Item ").append(i).append("</li>");
        }
        html.append("</ol>");

        Paragraph p = new Paragraph(html.toString(), HELVETICA, 12f, 300f, HorizontalAlignment.LEFT);
        List<String> lines = p.getLines();
        assertTrue(lines.size() >= 20);
        assertTrue(lines.stream().anyMatch(l -> l.contains("1.")));
        assertTrue(lines.stream().anyMatch(l -> l.contains("20.")));
    }

    // ── ASCII character cache ─────────────────────────────────────────────────

    @Test
    public void asciiCharacters_processedWithoutError() {
        String ascii = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Paragraph p = new Paragraph(ascii, HELVETICA, 12f, 500f, HorizontalAlignment.LEFT);
        assertNotNull(p.getLines());
        assertTrue(p.getMaxLineWidth() > 0);
    }

    @Test
    public void printableAsciiRange_processedWithoutError() {
        StringBuilder sb = new StringBuilder();
        for (int i = 32; i <= 126; i++) {
            sb.append((char) i);
        }
        Paragraph p = new Paragraph(sb.toString(), HELVETICA, 10f, 800f, HorizontalAlignment.LEFT);
        assertNotNull(p.getLines());
        assertTrue(p.getMaxLineWidth() > 0);
    }

    @Test
    public void repeatedAsciiCharacters_processedWithoutError() {
        Paragraph p = new Paragraph("aaaaaaaaaa bbbbbbbbbb cccccccccc", HELVETICA, 12f, 300f, HorizontalAlignment.LEFT);
        assertNotNull(p.getLines());
        assertTrue(p.getMaxLineWidth() > 0);
    }

    // ── Edge cases ────────────────────────────────────────────────────────────

    @Test
    public void whitespaceOnlyText_processedWithoutError() {
        Paragraph p = new Paragraph("   ", HELVETICA, 12f, 200f, HorizontalAlignment.LEFT);
        assertNotNull(p.getLines());
    }

    @Test
    public void multipleBreakTags_createMultipleLines() {
        Paragraph p = new Paragraph("A<br>B<br>C<br>D", HELVETICA, 12f, 200f, HorizontalAlignment.LEFT);
        assertTrue(p.getLines().size() >= 4);
    }

    @Test
    public void differentFonts_bothProcessedWithoutError() {
        Paragraph p1 = new Paragraph("Test", HELVETICA, 12f, 200f, HorizontalAlignment.LEFT);
        Paragraph p2 = new Paragraph("Test", COURIER,   12f, 200f, HorizontalAlignment.LEFT);
        assertNotNull(p1.getLines());
        assertNotNull(p2.getLines());
    }

    @Test
    public void setAlign_changesAlignment() {
        Paragraph p = new Paragraph("Test", HELVETICA, 12f, 200f, HorizontalAlignment.LEFT);
        p.setAlign(HorizontalAlignment.CENTER);
        assertEquals(HorizontalAlignment.CENTER, p.getAlign());
    }
}
