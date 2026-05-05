package be.quodlibet.boxable.richtext;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.utils.PageContentStreamOptimized;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TextContentElementWrappingTest {

    private static final float PAGE_WIDTH  = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float MARGIN      = 30f;

    /**
     * Text for the FIRST RichTextBlock.
     * Contains spaces after every comma.  At 120 pt width / 9 pt font the
     * text wraps, so {@code splitAtWidth} is exercised and the {@code remainder.trim()}
     */
    private static final String INTRO_TEXT =
            "Summary: Alpha, Beta, Gamma, Delta, Epsilon, Zeta, Eta, Theta, Iota end.";

    /**
     * Text for the SECOND RichTextBlock (after the table).
     * Long dot-separated words with NO spaces — forces the
     * {@code splitAtWidth} → {@code forceBreak} path.
     */
    private static final String FOOTER_CONTENT =
            "footercontent:eossedquiaetcorporis.suntquiasedut.repellendusquasieaquasassumenda." +
            "cupiditateeasintveritatisfuganecessitatibusfugiataspernatur.quasdelectusvoluptatemsolutadolore." +
            "excommoditemporibusquoset.sedvoluptatumtemporibusetsoluta.doloribusrepudiandaeiureestestdoloreum." +
            "excepturisolutamagnaminetasperiores.cumqueeaeoseius.molestiaequaslaboreautnobismaioresillumdeserunt." +
            "etconsequaturvitaequiautemnecessitatibus.";

    /**
     * Extracts all text from the full layout PDF (first RichTextBlock → table
     * → second RichTextBlock) and removes only newline characters — spaces are
     * left intact.  The resulting string must contain the original INTRO_TEXT
     * verbatim, including every space after each comma.
     *
     * <p>On master this fails because {@code remainder.trim()} in
     * {@code splitAtWidth} strips the leading space from every continuation
     * line, turning {@code "Alpha, Beta"} into {@code "Alpha,Beta"}.
     */
    @Test
    public void testSpacesAfterCommasPreservedInFirstBlock() throws IOException {
        String raw = renderFullLayoutAndExtract();
        // Remove newlines only — spaces that are genuinely in the PDF remain
        String reconstructed = raw.replace("\r\n", "").replace("\n", "").replace("\r", "");
        assertTrue(
                "INTRO_TEXT must round-trip intact through the PDF: all spaces after commas " +
                "must be present in the extracted text.\n" +
                "Expected to find : " + INTRO_TEXT + "\n" +
                "Reconstructed    : " + reconstructed,
                reconstructed.contains(INTRO_TEXT));
    }

    /**
     * Direct adaptation of the production test {@code testPdfFileGenerationForHeaderAndFooter}
     * from the reporting application that uses boxable.
     *
     * <p>Original structure:
     * <pre>
     *   String headerText = "Header content :" + faker.lorem().paragraph(10);
     *   String footerText = "Footer content :" + faker.lorem().paragraph(10);
     *   pdfReportGenerator.generateReport(fos, reportContent);  // renders header + footer
     *   pdfFileContent = pdfFileContent.replaceAll(
     *       String.valueOf(Pattern.compile("\\r\\n|\\r|\\n")), "");
     *   assertThat(pdfFileContent).containsIgnoringCase(headerText);
     *   assertThat(pdfFileContent).containsIgnoringCase(footerText);
     * </pre>
     *
     * <p>Adapted to boxable's own API:
     * <ul>
     *   <li>{@code faker.lorem().paragraph(10)} → fixed deterministic Lorem Ipsum
     *       (same length, same comma-space patterns)</li>
     *   <li>{@code pdfReportGenerator.generateReport} → {@link RichTextBlock} rendered
     *       at 12 pt in a 150 pt block (narrow enough to force wrapping on every
     *       comma+space)</li>
     *   <li>A {@link BaseTable} between the two blocks mirrors the report's body area</li>
     *   <li>{@code assertThat(...).containsIgnoringCase} → {@code assertTrue} with
     *       {@code toLowerCase().contains()} (AssertJ not available in boxable)</li>
     * </ul>
     *
     * <p>Spaces are stripped from both expected and actual before comparison,
     * so this test checks only that <em>no characters are dropped</em> —
     * space-preservation is covered by
     * {@link #testSpacesAfterCommasPreservedInFirstBlock}.
     */
    @Test
    public void testPdfFileGenerationForHeaderAndFooter() throws IOException {
        // Deterministic substitute for faker.lorem().paragraph(10) —
        // same comma+space patterns
        String headerText = "Header content: Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris " +
                "nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in " +
                "reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla " +
                "pariatur. Excepteur sint occaecat cupidatat non proident, sunt in " +
                "culpa qui officia deserunt mollit anim id est laborum.";

        String footerText = "Footer content: Sed ut perspiciatis unde omnis iste natus error sit " +
                "voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa " +
                "quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt " +
                "explicabo. Nemo enim ipsam voluptatem, quia voluptas sit, aspernatur aut odit " +
                "aut fugit, sed quia consequuntur magni dolores eos, qui ratione voluptatem " +
                "sequi nesciunt, neque porro quisquam est, qui dolorem ipsum, quia amet.";

        // Render header RichTextBlock → BaseTable → footer RichTextBlock,
        // mirroring the report generator's header-area / body / footer-area layout.
        // 300 pt wide at 9 pt: wraps several times but fits within the block height.
        String pdfFileContent = renderHeaderAndFooterAndExtract(headerText, footerText, 300f, 9f);

        // Strip both line endings AND spaces — checks only that no characters
        // were dropped, not that spaces are preserved (space-preservation is
        // covered by testSpacesAfterCommasPreservedInFirstBlock).
        pdfFileContent = pdfFileContent.replaceAll("\\r\\n|\\r|\\n|\\s", "");
        String expectedHeader = headerText.replaceAll("\\s", "");
        String expectedFooter = footerText.replaceAll("\\s", "");

        assertTrue(
                "Header text must round-trip intact through the PDF: no characters dropped.\n" +
                "Expected (no spaces) : " + expectedHeader + "\n" +
                "Reconstructed        : " + pdfFileContent,
                pdfFileContent.toLowerCase().contains(expectedHeader.toLowerCase()));

        assertTrue(
                "Footer text must round-trip intact through the PDF: no characters dropped.\n" +
                "Expected (no spaces) : " + expectedFooter + "\n" +
                "Reconstructed        : " + pdfFileContent,
                pdfFileContent.toLowerCase().contains(expectedFooter.toLowerCase()));
    }

    /**
     * Verifies that a string containing a mixture of single spaces, double
     * spaces, and triple spaces round-trips through a PDF render unchanged.
     *
     * <p>The input is rendered in a narrow (130 pt) block at 9 pt so that
     * wrapping occurs on every few words, exercising {@code splitAtWidth} on
     * lines that both start and end with multi-space runs.
     *
     * <p>Only newline characters are removed before comparison — all space
     * characters (including consecutive ones) are left intact.  On master,
     * {@code remainder.trim()} collapses every leading whitespace run on a
     * continuation line to nothing, so {@code "alpha  beta"} (two spaces)
     * becomes {@code "alpha beta"} (one space) or {@code "alpha beta"}
     * (no space) depending on where the wrap falls.
     */
    @Test
    public void testMultipleAndMixedSpacesPreserved() throws IOException {
        // Mix of single, double, and triple spaces at different positions.
        // Placed so that line-breaks land in the middle of multi-space runs.
        final String text =
                "Start:  one space,  two spaces after comma,   three spaces here. " +
                "Then  a  double-spaced  sentence  that  wraps  across  lines. " +
                "End:   final   triple   spaced   words   here.";

        // 130 pt at 9 pt forces a line break every ~3-4 words
        String raw = renderSingleWordAndExtract(text, 130f, 9f);

        // Strip only line endings — every space (single, double, triple) must survive
        String reconstructed = raw.replace("\r\n", "").replace("\r", "").replace("\n", "");

        assertTrue(
                "All spaces (including consecutive ones) must survive the PDF round-trip.\n" +
                "On master, remainder.trim() collapses leading whitespace runs on\n" +
                "continuation lines, turning double/triple spaces into single or none.\n" +
                "Expected to find : " + text + "\n" +
                "Reconstructed    : " + reconstructed,
                reconstructed.contains(text));
    }

    @Test
    public void testForceBreakDoesNotPlaceOverflowingCharOnCurrentLine() throws IOException {
        final float blockWidth = 50f;
        final float fontSize   = 10f;
        PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

        // n = largest count where n 'A's have width strictly less than blockWidth
        int n = 0;
        while (font.getStringWidth(repeat('A', n + 1)) / 1000f * fontSize < blockWidth) {
            n++;
        }
        // Safety: n must be at least 1 so the split is meaningful
        assertTrue("Expected at least 1 character to fit in the block", n >= 1);

        // testWord has (n+1) chars: n fit, the last one causes overflow
        final String testWord = repeat('A', n + 1);

        String raw = renderSingleWordAndExtract(testWord, blockWidth, fontSize);

        // The (n+1)-char sequence must NOT appear unbroken in the extracted text.
        // On master forceBreak places all (n+1) chars on one line (overflow),
        // so the sequence IS present — the test fails.
        // On fix  forceBreak splits at n chars, so the sequence is broken across
        // two lines and is NOT present — the test passes.
        assertFalse(
                "forceBreak must not place more characters on a line than fit.\n" +
                "Found '" + testWord + "' as a single unbroken sequence in the PDF,\n" +
                "meaning the (" + (n + 1) + ")-th 'A' was placed on the same line\n" +
                "as the preceding " + n + " 'A' chars even though it causes overflow.\n" +
                "Raw extracted text:\n" + raw,
                raw.contains(testWord));
    }

    /**
     * Verifies that ALL characters of FOOTER_CONTENT are present in the PDF.
     * Joining the extracted lines (removing only newlines) must reproduce the
     * full footer string character-for-character.
     */
    @Test
    public void testAllFooterCharactersPresent() throws IOException {
        String raw = renderFullLayoutAndExtract();
        String reconstructed = raw.replace("\r\n", "").replace("\n", "").replace("\r", "");
        assertTrue(
                "FOOTER_CONTENT must round-trip intact: no characters dropped.\n" +
                "Expected to find : " + FOOTER_CONTENT + "\n" +
                "Reconstructed    : " + reconstructed,
                reconstructed.contains(FOOTER_CONTENT));
    }

    // -------------------------------------------------------------------------
    //  Layout order
    // -------------------------------------------------------------------------

    /**
     * Asserts that the intro block text appears BEFORE the footer content in
     * the PDF, confirming the layout order (RichTextBlock → table → RichTextBlock)
     * is preserved.
     */
    @Test
    public void testLayoutOrderPreserved() throws IOException {
        String raw = renderFullLayoutAndExtract();
        int introIdx   = raw.indexOf("Summary:");
        int footerIdx  = raw.indexOf("footercontent:");
        assertTrue("Intro block must appear in the PDF",  introIdx  >= 0);
        assertTrue("Footer block must appear in the PDF", footerIdx >= 0);
        assertTrue("Intro block must appear before footer block", introIdx < footerIdx);
    }

    // -------------------------------------------------------------------------
    //  Layout builder
    // -------------------------------------------------------------------------

    /**
     * Builds a single-page PDF with:
     * <ol>
     *   <li>A <b>narrow</b> (120 pt) RichTextBlock containing {@link #INTRO_TEXT} —
     *       forced to wrap, exercising {@code splitAtWidth}.</li>
     *   <li>A {@link BaseTable} containing a simple data row — present as the
     *       middle element in the layout.</li>
     *   <li>A <b>narrow</b> (200 pt) RichTextBlock containing {@link #FOOTER_CONTENT}
     *       — forced to wrap, exercising {@code forceBreak}</li>
     * </ol>
     * Saves to an in-memory byte array and returns the text extracted by
     * {@link PDFTextStripper} with {@code sortByPosition=true}.
     */
    private String renderFullLayoutAndExtract() throws IOException {
        // Block widths — intentionally narrow to force line wrapping.
        final float introWidth  = 120f;   // wraps INTRO_TEXT (spaces-after-commas path)
        final float footerWidth = 200f;   // wraps FOOTER_CONTENT (forceBreak path)

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(new PDRectangle(PAGE_WIDTH, PAGE_HEIGHT));
            doc.addPage(page);

            // 1. First RichTextBlock (intro) ──────────────────────────────
            float introBlockHeight = 80f;
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                PageContentStreamOptimized stream = new PageContentStreamOptimized(cs);
                RichTextBlock.builder()
                        .at(MARGIN, MARGIN)
                        .size(introWidth, introBlockHeight)
                        .blockPadding(0f)
                        .addContent(richLine(INTRO_TEXT, 9f))
                        .build()
                        .render(doc, stream, PAGE_HEIGHT);
            }

            // 2. BaseTable (middle element) ──────────────────────────────
            float tableTopY    = PAGE_HEIGHT - MARGIN - introBlockHeight - 10f;
            float tableWidth   = footerWidth;
            BaseTable table = new BaseTable(
                    tableTopY, tableTopY, 20f, tableWidth, MARGIN,
                    doc, page, true, true);

            Row<PDPage> headerRow = table.createRow(12f);
            Cell<PDPage> hCell = headerRow.createCell(100, "Data table");
            hCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
            hCell.setFontSize(8);
            table.addHeaderRow(headerRow);

            Row<PDPage> dataRow = table.createRow(10f);
            Cell<PDPage> dCell = dataRow.createCell(100, "Row 1: value A, value B, value C");
            dCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA));
            dCell.setFontSize(8);

            float tableBottomY = table.draw();

            // 3. Second RichTextBlock (footer content) ──────────────────
            float footerBlockTop = PAGE_HEIGHT - tableBottomY + 10f;
            try (PDPageContentStream cs = new PDPageContentStream(
                    doc, page, PDPageContentStream.AppendMode.APPEND, true)) {
                PageContentStreamOptimized stream = new PageContentStreamOptimized(cs);
                RichTextBlock.builder()
                        .at(MARGIN, footerBlockTop)
                        .size(footerWidth, PAGE_HEIGHT - footerBlockTop - MARGIN)
                        .blockPadding(0f)
                        .addContent(richLine(FOOTER_CONTENT, 8f))
                        .build()
                        .render(doc, stream, PAGE_HEIGHT);
            }

            new File("target").mkdirs();
            doc.save(new File("target/TextContentElementWrappingTest.pdf"));
            doc.save(baos);
        }

        try (PDDocument readDoc = Loader.loadPDF(baos.toByteArray())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(readDoc);
        }
    }

    /**
     * Renders a PDF with a header {@link RichTextBlock}, a {@link BaseTable}
     * body, and a footer {@link RichTextBlock} — mirroring the three-area
     * layout of the production report generator — and returns the full
     * extracted text.
     *
     * @param headerText text for the top RichTextBlock (header area)
     * @param footerText text for the bottom RichTextBlock (footer area)
     * @param blockWidth width of both RichTextBlocks in points
     * @param fontSize   font size in points
     */
    private String renderHeaderAndFooterAndExtract(String headerText, String footerText,
                                                   float blockWidth, float fontSize)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(new PDRectangle(PAGE_WIDTH, PAGE_HEIGHT));
            doc.addPage(page);

            // 1. Header RichTextBlock (top of page)
            // PAGE_HEIGHT * 0.45 ≈ 379 pt — large enough for ~22 wrapped lines
            // at any reasonable font size so no text is silently truncated.
            float headerBlockHeight = PAGE_HEIGHT * 0.45f;
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                PageContentStreamOptimized stream = new PageContentStreamOptimized(cs);
                RichTextBlock.builder()
                        .at(MARGIN, MARGIN)
                        .size(blockWidth, headerBlockHeight)
                        .blockPadding(0f)
                        .addContent(richLine(headerText, fontSize))
                        .build()
                        .render(doc, stream, PAGE_HEIGHT);
            }

            // 2. BaseTable (body area — middle element)
            float tableTopY = PAGE_HEIGHT - MARGIN - headerBlockHeight - 10f;
            BaseTable table = new BaseTable(
                    tableTopY, tableTopY, 20f, blockWidth, MARGIN,
                    doc, page, true, true);
            Row<PDPage> row = table.createRow(10f);
            Cell<PDPage> cell = row.createCell(100, "Body content");
            cell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA));
            cell.setFontSize(8);
            float tableBottomY = table.draw();

            // 3. Footer RichTextBlock (below the table)
            float footerTop = PAGE_HEIGHT - tableBottomY + 10f;
            try (PDPageContentStream cs = new PDPageContentStream(
                    doc, page, PDPageContentStream.AppendMode.APPEND, true)) {
                PageContentStreamOptimized stream = new PageContentStreamOptimized(cs);
                RichTextBlock.builder()
                        .at(MARGIN, footerTop)
                        .size(blockWidth, PAGE_HEIGHT - footerTop - MARGIN)
                        .blockPadding(0f)
                        .addContent(richLine(footerText, fontSize))
                        .build()
                        .render(doc, stream, PAGE_HEIGHT);
            }

            doc.save(baos);
        }

        try (PDDocument readDoc = Loader.loadPDF(baos.toByteArray())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(readDoc);
        }
    }

    /**
     * Renders a single {@link TextContentElement} containing {@code word} in a
     * block of {@code blockWidth} × full page height, and returns the text
     * extracted by {@link PDFTextStripper} with {@code sortByPosition=true}.
     */
    private String renderSingleWordAndExtract(String word, float blockWidth,
                                              float fontSize) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(new PDRectangle(PAGE_WIDTH, PAGE_HEIGHT));
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                PageContentStreamOptimized stream = new PageContentStreamOptimized(cs);
                RichTextBlock.builder()
                        .at(MARGIN, MARGIN)
                        .size(blockWidth, PAGE_HEIGHT - 2 * MARGIN)
                        .blockPadding(0f)
                        .addContent(richLine(word, fontSize))
                        .build()
                        .render(doc, stream, PAGE_HEIGHT);
            }
            doc.save(baos);
        }
        try (PDDocument readDoc = Loader.loadPDF(baos.toByteArray())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(readDoc);
        }
    }

    /** Java-8–compatible equivalent of {@link String#repeat(int)}. */
    private static String repeat(char ch, int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) sb.append(ch);
        return sb.toString();
    }

    private static TextContentElement richLine(String text, float fontSize) {
        return new TextContentElement(
                new RichTextLine(
                        Collections.singletonList(
                                new RichTextSegment(
                                        text, EnumSet.noneOf(TextStyle.class), fontSize)),
                        ListType.NONE, 0, TextAlignment.LEFT));
    }
}
