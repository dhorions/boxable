package be.quodlibet.boxable.line;

import org.junit.Test;

import java.awt.Color;

import static org.junit.Assert.*;

public class LineStyleTest {

    // ── Constructor ──────────────────────────────────────────────────────────

    @Test
    public void constructor_setsColorAndWidth() {
        LineStyle line = new LineStyle(Color.BLACK, 1f);
        assertEquals(Color.BLACK, line.getColor());
        assertEquals(1f, line.getWidth(), 0.001f);
    }

    @Test
    public void constructor_zeroWidthAllowed() {
        LineStyle line = new LineStyle(Color.BLACK, 0f);
        assertEquals(0f, line.getWidth(), 0.001f);
    }

    @Test
    public void constructor_largeWidthAllowed() {
        LineStyle line = new LineStyle(Color.BLACK, 100f);
        assertEquals(100f, line.getWidth(), 0.001f);
    }

    @Test
    public void solidLine_hasNoDashArray() {
        LineStyle line = new LineStyle(Color.BLACK, 1f);
        assertNull(line.getDashArray());
        assertEquals(0f, line.getDashPhase(), 0.001f);
    }

    // ── produceDotted ────────────────────────────────────────────────────────

    @Test
    public void produceDotted_createsDottedStyle() {
        LineStyle dotted = LineStyle.produceDotted(Color.BLUE, 2);
        assertEquals(Color.BLUE, dotted.getColor());
        assertEquals(2f, dotted.getWidth(), 0.001f);
        assertNotNull(dotted.getDashArray());
        assertEquals(1, dotted.getDashArray().length);
        assertEquals(1.0f, dotted.getDashArray()[0], 0.001f);
        assertEquals(0.0f, dotted.getDashPhase(), 0.001f);
    }

    @Test
    public void produceDotted_withDifferentColors() {
        LineStyle red   = LineStyle.produceDotted(Color.RED, 1);
        LineStyle green = LineStyle.produceDotted(Color.GREEN, 3);
        assertEquals(Color.RED,   red.getColor());
        assertEquals(Color.GREEN, green.getColor());
    }

    // ── produceDashed ────────────────────────────────────────────────────────

    @Test
    public void produceDashed_defaultPattern() {
        LineStyle dashed = LineStyle.produceDashed(Color.GREEN, 3);
        assertEquals(Color.GREEN, dashed.getColor());
        assertEquals(3f, dashed.getWidth(), 0.001f);
        assertNotNull(dashed.getDashArray());
        assertEquals(1, dashed.getDashArray().length);
        assertEquals(5.0f, dashed.getDashArray()[0], 0.001f);
        assertEquals(0.0f, dashed.getDashPhase(), 0.001f);
    }

    @Test
    public void produceDashed_customPattern() {
        float[] pattern = {10.0f, 5.0f};
        LineStyle dashed = LineStyle.produceDashed(Color.ORANGE, 2, pattern, 2.5f);
        assertEquals(Color.ORANGE, dashed.getColor());
        assertEquals(2f, dashed.getWidth(), 0.001f);
        assertEquals(2, dashed.getDashArray().length);
        assertEquals(10.0f, dashed.getDashArray()[0], 0.001f);
        assertEquals(5.0f,  dashed.getDashArray()[1], 0.001f);
        assertEquals(2.5f,  dashed.getDashPhase(), 0.001f);
    }

    @Test
    public void produceDashed_longPattern() {
        float[] pattern = {15.0f, 5.0f, 5.0f, 5.0f};
        LineStyle dashed = LineStyle.produceDashed(Color.CYAN, 1, pattern, 0.0f);
        assertEquals(4, dashed.getDashArray().length);
    }

    // ── equals ───────────────────────────────────────────────────────────────

    @Test
    public void equals_sameValues_returnsTrue() {
        LineStyle a = new LineStyle(Color.BLACK, 1f);
        LineStyle b = new LineStyle(Color.BLACK, 1f);
        assertEquals(a, b);
        assertEquals(b, a);
    }

    @Test
    public void equals_differentColor_returnsFalse() {
        assertNotEquals(new LineStyle(Color.BLACK, 1f), new LineStyle(Color.RED, 1f));
    }

    @Test
    public void equals_differentWidth_returnsFalse() {
        assertNotEquals(new LineStyle(Color.BLACK, 1f), new LineStyle(Color.BLACK, 2f));
    }

    @Test
    public void equals_null_returnsFalse() {
        assertNotEquals(new LineStyle(Color.BLACK, 1f), null);
    }

    @Test
    public void equals_differentClass_returnsFalse() {
        assertNotEquals(new LineStyle(Color.BLACK, 1f), "not a line style");
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        LineStyle line = new LineStyle(Color.BLACK, 1f);
        assertEquals(line, line);
    }

    // ── hashCode ─────────────────────────────────────────────────────────────

    @Test
    public void hashCode_isConsistent() {
        LineStyle line = new LineStyle(Color.BLACK, 1f);
        assertEquals(line.hashCode(), line.hashCode());
    }

    @Test
    public void hashCode_equalObjects_sameHash() {
        LineStyle a = new LineStyle(Color.BLACK, 1f);
        LineStyle b = new LineStyle(Color.BLACK, 1f);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void hashCode_differentObjects_differentHash() {
        LineStyle a = new LineStyle(Color.BLACK, 1f);
        LineStyle b = new LineStyle(Color.RED, 2f);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    @Test
    public void getColor_returnsCorrectColor() {
        for (Color c : new Color[]{Color.BLACK, Color.WHITE, Color.RED, Color.GREEN, Color.BLUE}) {
            assertEquals(c, new LineStyle(c, 1f).getColor());
        }
    }

    @Test
    public void getColor_customColor() {
        Color custom = new Color(128, 64, 32);
        assertEquals(custom, new LineStyle(custom, 1f).getColor());
    }

    @Test
    public void getWidth_variousWidths() {
        for (float w : new float[]{0.5f, 1.0f, 2.5f, 10.0f}) {
            assertEquals(w, new LineStyle(Color.BLACK, w).getWidth(), 0.001f);
        }
    }

    @Test
    public void typicalBorderStyles() {
        assertEquals(0.5f, new LineStyle(Color.BLACK, 0.5f).getWidth(), 0.001f);
        assertEquals(1f,   new LineStyle(Color.BLACK, 1f).getWidth(),   0.001f);
        assertEquals(3f,   new LineStyle(Color.BLACK, 3f).getWidth(),   0.001f);
    }
}
