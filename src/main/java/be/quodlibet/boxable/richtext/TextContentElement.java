package be.quodlibet.boxable.richtext;

import be.quodlibet.boxable.richtext.alignment.AlignmentStrategy;
import be.quodlibet.boxable.richtext.alignment.AlignmentStrategyFactory;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * A {@link ContentElement} representing one or more logical lines of styled text.
 * <p>
 * Word-wrapping is performed lazily against the available width during
 * {@link #estimateHeight} / {@link #render}.
 * </p>
 * <p>
 * Alignment is delegated to an {@link AlignmentStrategy} (Strategy pattern).
 * </p>
 */
public final class TextContentElement implements ContentElement {

    /** Standard line-spacing multiplier (1.4 × font size). */
    static final float LINE_SPACING = 1.4f;

    /** Indentation applied to list item text (in points). */
    static final float LIST_INDENT = 15f;
    /** Horizontal offset for the bullet character from the indent start. */
    static final float BULLET_OFFSET = 5f;

    private final List<RichTextLine> lines;

    /**
     * Creates a text element from one or more pre-built lines.
     *
     * @param lines the rich text lines
     */
    public TextContentElement(List<RichTextLine> lines) {
        this.lines = lines != null
                ? Collections.unmodifiableList(new ArrayList<>(lines))
                : Collections.emptyList();
    }

    /** Convenience constructor for a single line. */
    public TextContentElement(RichTextLine line) {
        this(Collections.singletonList(line));
    }

    // -- ContentElement contract --------------------------------------------

    @Override
    public float estimateHeight(float availableWidth) throws IOException {
        float total = 0;
        for (RichTextLine line : lines) {
            total += estimateLineHeight(line, availableWidth);
        }
        return total;
    }

    @Override
    public void render(RenderContext ctx) throws IOException {
        for (RichTextLine line : lines) {
            if (ctx.isOverflow()) return;
            renderLine(ctx, line);
        }
    }

    // ── Internals ────────────────────────────────────────────────────────

    private float estimateLineHeight(RichTextLine line, float availableWidth) throws IOException {
        float fontSize = line.getMaxFontSize();
        float lineHeight = fontSize * LINE_SPACING;

        float contentWidth = availableWidth;
        if (line.getListType() == ListType.BULLETED || line.getListType() == ListType.NUMBERED) {
            contentWidth -= LIST_INDENT;
        }

        List<List<LineElement>> wrappedLines = wrapElements(line.getElements(), contentWidth);
        return lineHeight * wrappedLines.size();
    }

    private void renderLine(RenderContext ctx, RichTextLine line) throws IOException {
        float fontSize = line.getMaxFontSize();
        float lineHeight = fontSize * LINE_SPACING;
        float contentStartX = ctx.getLeft();
        float contentWidth = ctx.getInnerWidth();

        // Handle list indentation
        String prefix = null;
        PDFont prefixFont = FontResolver.resolve(EnumSet.noneOf(TextStyle.class));
        if (line.getListType() == ListType.BULLETED) {
            prefix = "• ";
            contentStartX += LIST_INDENT;
            contentWidth -= LIST_INDENT;
        } else if (line.getListType() == ListType.NUMBERED) {
            prefix = line.getListIndex() + ". ";
            contentStartX += LIST_INDENT;
            contentWidth -= LIST_INDENT;
        }

        // Word-wrap all elements (text segments + inline images), then render line by line
        List<List<LineElement>> wrappedVisualLines =
                wrapElements(line.getElements(), contentWidth);

        AlignmentStrategy strategy = AlignmentStrategyFactory.get(line.getAlignment());

        for (int i = 0; i < wrappedVisualLines.size(); i++) {
            if (!ctx.hasSpace(lineHeight)) {
                ctx.markOverflow();
                return;
            }
            ctx.advanceCursor(lineHeight);

            // Draw list prefix on the first visual line only
            if (i == 0 && prefix != null) {
                float prefixX = ctx.getLeft()
                        + (line.getListType() == ListType.BULLETED
                        ? BULLET_OFFSET : 0);
                ctx.getStream().beginText();
                ctx.getStream().setFont(prefixFont, fontSize);
                ctx.getStream().setNonStrokingColor(java.awt.Color.BLACK);
                ctx.getStream().newLineAt(prefixX, ctx.getCursorY());
                ctx.getStream().showText(prefix);
                ctx.getStream().endText();
            }

            // Render the visual line elements using the chosen alignment strategy
            List<LineElement> visualElements = wrappedVisualLines.get(i);

            // For JUSTIFY: don't justify the last visual line
            if (line.getAlignment() == TextAlignment.JUSTIFY
                    && i == wrappedVisualLines.size() - 1) {
                AlignmentStrategyFactory.get(TextAlignment.LEFT)
                        .renderLine(ctx, visualElements, ctx.getCursorY(),
                                contentStartX, contentWidth);
            } else {
                strategy.renderLine(ctx, visualElements, ctx.getCursorY(),
                        contentStartX, contentWidth);
            }
        }
    }

    /**
     * Wraps a list of elements into visual lines that fit within {@code maxWidth}.
     * Each visual line is itself a list of {@link LineElement}s.
     * <p>
     * Text segments are split at word boundaries when they exceed the available width.
     * Inline images are treated as atomic, unsplittable units — if an image does not
     * fit on the current line it is pushed to the next line.
     * </p>
     */
    private List<List<LineElement>> wrapElements(List<LineElement> elements,
                                                  float maxWidth) throws IOException {
        List<List<LineElement>> visualLines = new ArrayList<>();
        List<LineElement> currentVisualLine = new ArrayList<>();
        float currentLineWidth = 0;

        for (LineElement element : elements) {
            if (element instanceof InlineImageSegment) {
                // Inline images are atomic — cannot be split
                float imgWidth = element.getWidth();
                if (imgWidth <= maxWidth - currentLineWidth) {
                    // Fits on current line
                    currentVisualLine.add(element);
                    currentLineWidth += imgWidth;
                } else if (currentLineWidth > 0) {
                    // Doesn't fit — flush current line and place on new line
                    visualLines.add(currentVisualLine);
                    currentVisualLine = new ArrayList<>();
                    currentVisualLine.add(element);
                    currentLineWidth = imgWidth;
                } else {
                    // Fresh line but image is wider than maxWidth — force-place it
                    currentVisualLine.add(element);
                    visualLines.add(currentVisualLine);
                    currentVisualLine = new ArrayList<>();
                    currentLineWidth = 0;
                }
            } else if (element instanceof RichTextSegment) {
                RichTextSegment segment = (RichTextSegment) element;
                PDFont font = segment.resolveFont();
                float fontSize = segment.getFontSize();
                String remaining = segment.getText();

                while (!remaining.isEmpty()) {
                    // floor/ceil applied to width values to prevent
                    // floating-point rounding from placing text that is one sub-pixel
                    // over the limit.  Previously both values were raw floats, which
                    // caused border-line tokens to appear to "fit" on a line even
                    // when they were fractionally wider than availableWidth.
                    float availableWidth = (float) Math.floor(maxWidth - currentLineWidth);
                    float textWidth = (float) Math.ceil(WordWrapUtil.textWidth(remaining, font, fontSize));

                    if (textWidth <= availableWidth) {
                        // Entire remaining text fits on the current line
                        currentVisualLine.add(new RichTextSegment(
                                remaining, segment.getStyles(), fontSize, segment.getColor()));
                        currentLineWidth += textWidth;
                        remaining = "";
                    } else {
                        // Text does not fit — attempt to split at a word boundary
                        String[] split = splitAtWidth(remaining, font, fontSize, availableWidth);

                        if (!split[0].isEmpty()) {
                            currentVisualLine.add(new RichTextSegment(
                                    split[0], segment.getStyles(), fontSize, segment.getColor()));
                            visualLines.add(currentVisualLine);
                            currentVisualLine = new ArrayList<>();
                            currentLineWidth = 0;
                            remaining = split[1];
                        } else if (currentLineWidth > 0) {
                            visualLines.add(currentVisualLine);
                            currentVisualLine = new ArrayList<>();
                            currentLineWidth = 0;
                        } else {
                            String[] forced = forceBreak(remaining, font, fontSize, maxWidth);
                            currentVisualLine.add(new RichTextSegment(
                                    forced[0], segment.getStyles(), fontSize, segment.getColor()));
                            visualLines.add(currentVisualLine);
                            currentVisualLine = new ArrayList<>();
                            currentLineWidth = 0;
                            remaining = forced[1];
                        }
                    }
                }
            }
        }

        if (!currentVisualLine.isEmpty()) {
            visualLines.add(currentVisualLine);
        }
        if (visualLines.isEmpty()) {
            visualLines.add(Collections.emptyList());
        }
        return visualLines;
    }

    /**
     * Splits text at a word boundary so that the first part fits within {@code maxWidth}.
     * Returns an empty first element if not even the first word fits.
     *
     * @return {@code [fittingPart, remainder]}
     */
    private String[] splitAtWidth(String text, PDFont font, float fontSize,
                                  float maxWidth) throws IOException {
        // floor applied so we never place text that "barely fits"
        // due to floating-point precision in font metrics.  Previously the raw
        // maxWidth float was used directly, causing occasional over-placement.
        float effectiveMax = (float) Math.floor(maxWidth);

        // replaced text.split("(?<=\\s)") with an explicit
        // alternating-token scanner.  The old regex produced tokens that each
        // ended with a trailing whitespace character, which meant whitespace was
        // counted as part of the preceding word.  The new scanner produces
        // separate tokens for word-runs and whitespace-runs, so the width check
        // is done against the real printable characters only.
        List<String> tokens = new ArrayList<>();
        int i = 0;
        while (i < text.length()) {
            int start = i;
            if (Character.isWhitespace(text.charAt(i))) {
                while (i < text.length() && Character.isWhitespace(text.charAt(i))) i++;
            } else {
                while (i < text.length() && !Character.isWhitespace(text.charAt(i))) i++;
            }
            tokens.add(text.substring(start, i));
        }

        StringBuilder fitting = new StringBuilder();
        for (String token : tokens) {
            String candidate = fitting.toString() + token;
            float candidateWidth = (float) Math.ceil(WordWrapUtil.textWidth(candidate, font, fontSize));
            if (candidateWidth > effectiveMax) {
                if (fitting.length() > 0) {
                    // Previously this was:
                    //   return new String[]{fitting.toString().trim(), remainder.trim()};
                    // The double trim() stripped (a) trailing spaces from the fitting
                    // part (harmless) AND (b) the leading space from the remainder,
                    // so the first character of every continuation line was silently
                    // dropped — causing "Alpha, Beta" to become "Alpha,Beta" in the
                    // extracted PDF text.
                    // Now only trailing whitespace is removed from the fitting part
                    // (stripTrailing() — Java 8 compatible, see helper below),
                    // and the remainder is taken verbatim via substring so its leading
                    // spaces are fully preserved.
                    String fittingText = stripTrailing(fitting.toString());
                    String remainder = text.substring(fittingText.length());
                    return new String[]{fittingText, remainder};
                } else {
                    // Not even the first token fits — signal with empty first part
                    return new String[]{"", text};
                }
            }
            fitting.append(token);
        }
        // Everything fits
        return new String[]{text, ""};
    }

    /**
     * Force-breaks text character-by-character when a single word is wider than
     * the available width.  Guarantees at least one character is placed to
     * prevent infinite loops.
     *
     * @return {@code [fittingPart, remainder]}
     */
    private String[] forceBreak(String text, PDFont font, float fontSize,
                                float maxWidth) throws IOException {
        // floor/ceil applied for the same floating-point
        // precision reason as in splitAtWidth.
        float effectiveMax = (float) Math.floor(maxWidth);
        // loop bound changed from  i < text.length()
        // to  i <= text.length()
        // The old bound meant the last character of the string was never tested:
        // when exactly (n) characters fit and the (n+1)-th caused overflow, the
        // loop exited without detecting the overflow and fell through to the
        // "place it all" return — rendering all (n+1) characters on one line past
        // the right margin.  The fix lets the loop test the full-string case so
        // the overflow is caught and the string is properly split.
        for (int i = 1; i <= text.length(); i++) {
            String candidate = text.substring(0, i);
            float width = (float) Math.ceil(WordWrapUtil.textWidth(candidate, font, fontSize));
            if (width > effectiveMax && i > 1) {
                // Removed .trim() from the remainder.
                // Previously: text.substring(i - 1).trim() discarded the
                // character at position (i-1) when it was whitespace, effectively
                // orphaning boundary characters onto the next line.
                return new String[]{text.substring(0, i - 1), text.substring(i - 1)};
            }
        }
        // The entire text fits or is a single char — place it all
        return new String[]{text, ""};
    }

    /**
     * Removes trailing whitespace from {@code s}.
     *
     * <p>This is the Java-8–compatible equivalent of {@code String.stripTrailing()}
     * introduced in Java 11.  The logic mirrors the JDK 11 implementation in
     * {@code StringLatin1.stripTrailing}: scan from the right while
     * {@link Character#isWhitespace(char)} is true, then return the prefix up
     * to the last non-whitespace character.
     *
     * @param s the string to process; must not be {@code null}
     * @return {@code s} with all trailing whitespace removed, or an empty
     *         string if {@code s} consists entirely of whitespace
     */
    private static String stripTrailing(String s) {
        int right = s.length() - 1;
        while (right >= 0 && Character.isWhitespace(s.charAt(right))) {
            right--;
        }
        // right == s.length() - 1 means no trailing whitespace was found
        return right == s.length() - 1 ? s : s.substring(0, right + 1);
    }
}

