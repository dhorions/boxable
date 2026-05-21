package be.quodlibet.boxable;

import org.junit.Test;

import static org.junit.Assert.*;

public class AlignmentTest {

    // ── HorizontalAlignment ──────────────────────────────────────────────────

    @Test
    public void horizontalAlignment_hasThreeValues() {
        assertEquals(3, HorizontalAlignment.values().length);
        assertNotNull(HorizontalAlignment.LEFT);
        assertNotNull(HorizontalAlignment.CENTER);
        assertNotNull(HorizontalAlignment.RIGHT);
    }

    @Test
    public void horizontalAlignment_get_caseInsensitive() {
        assertEquals(HorizontalAlignment.LEFT,   HorizontalAlignment.get("left"));
        assertEquals(HorizontalAlignment.LEFT,   HorizontalAlignment.get("LEFT"));
        assertEquals(HorizontalAlignment.LEFT,   HorizontalAlignment.get("Left"));
        assertEquals(HorizontalAlignment.CENTER, HorizontalAlignment.get("center"));
        assertEquals(HorizontalAlignment.CENTER, HorizontalAlignment.get("CENTER"));
        assertEquals(HorizontalAlignment.CENTER, HorizontalAlignment.get("Center"));
        assertEquals(HorizontalAlignment.RIGHT,  HorizontalAlignment.get("right"));
        assertEquals(HorizontalAlignment.RIGHT,  HorizontalAlignment.get("RIGHT"));
        assertEquals(HorizontalAlignment.RIGHT,  HorizontalAlignment.get("Right"));
    }

    @Test
    public void horizontalAlignment_get_trimsWhitespace() {
        assertEquals(HorizontalAlignment.LEFT,   HorizontalAlignment.get("  left  "));
        assertEquals(HorizontalAlignment.CENTER, HorizontalAlignment.get(" center "));
        assertEquals(HorizontalAlignment.RIGHT,  HorizontalAlignment.get("right "));
    }

    @Test
    public void horizontalAlignment_get_nullReturnsLeft() {
        assertEquals(HorizontalAlignment.LEFT, HorizontalAlignment.get(null));
    }

    @Test
    public void horizontalAlignment_get_invalidInputReturnsLeft() {
        assertEquals(HorizontalAlignment.LEFT, HorizontalAlignment.get("invalid"));
        assertEquals(HorizontalAlignment.LEFT, HorizontalAlignment.get(""));
        assertEquals(HorizontalAlignment.LEFT, HorizontalAlignment.get("middle"));
    }

    @Test
    public void horizontalAlignment_valueOf() {
        assertSame(HorizontalAlignment.LEFT,   HorizontalAlignment.valueOf("LEFT"));
        assertSame(HorizontalAlignment.CENTER, HorizontalAlignment.valueOf("CENTER"));
        assertSame(HorizontalAlignment.RIGHT,  HorizontalAlignment.valueOf("RIGHT"));
    }

    @Test
    public void horizontalAlignment_ordinalsAreCorrect() {
        assertEquals(0, HorizontalAlignment.LEFT.ordinal());
        assertEquals(1, HorizontalAlignment.CENTER.ordinal());
        assertEquals(2, HorizontalAlignment.RIGHT.ordinal());
    }

    @Test
    public void horizontalAlignment_namesAreCorrect() {
        assertEquals("LEFT",   HorizontalAlignment.LEFT.name());
        assertEquals("CENTER", HorizontalAlignment.CENTER.name());
        assertEquals("RIGHT",  HorizontalAlignment.RIGHT.name());
    }

    // ── VerticalAlignment ────────────────────────────────────────────────────

    @Test
    public void verticalAlignment_hasThreeValues() {
        assertEquals(3, VerticalAlignment.values().length);
        assertNotNull(VerticalAlignment.TOP);
        assertNotNull(VerticalAlignment.MIDDLE);
        assertNotNull(VerticalAlignment.BOTTOM);
    }

    @Test
    public void verticalAlignment_get_caseInsensitive() {
        assertEquals(VerticalAlignment.TOP,    VerticalAlignment.get("top"));
        assertEquals(VerticalAlignment.TOP,    VerticalAlignment.get("TOP"));
        assertEquals(VerticalAlignment.TOP,    VerticalAlignment.get("Top"));
        assertEquals(VerticalAlignment.MIDDLE, VerticalAlignment.get("middle"));
        assertEquals(VerticalAlignment.MIDDLE, VerticalAlignment.get("MIDDLE"));
        assertEquals(VerticalAlignment.MIDDLE, VerticalAlignment.get("Middle"));
        assertEquals(VerticalAlignment.BOTTOM, VerticalAlignment.get("bottom"));
        assertEquals(VerticalAlignment.BOTTOM, VerticalAlignment.get("BOTTOM"));
        assertEquals(VerticalAlignment.BOTTOM, VerticalAlignment.get("Bottom"));
    }

    @Test
    public void verticalAlignment_get_trimsWhitespace() {
        assertEquals(VerticalAlignment.TOP,    VerticalAlignment.get("  top  "));
        assertEquals(VerticalAlignment.MIDDLE, VerticalAlignment.get(" middle "));
        assertEquals(VerticalAlignment.BOTTOM, VerticalAlignment.get("bottom "));
    }

    @Test
    public void verticalAlignment_get_nullReturnsTop() {
        assertEquals(VerticalAlignment.TOP, VerticalAlignment.get(null));
    }

    @Test
    public void verticalAlignment_get_invalidInputReturnsTop() {
        assertEquals(VerticalAlignment.TOP, VerticalAlignment.get("invalid"));
        assertEquals(VerticalAlignment.TOP, VerticalAlignment.get(""));
        assertEquals(VerticalAlignment.TOP, VerticalAlignment.get("center"));
    }

    @Test
    public void verticalAlignment_valueOf() {
        assertSame(VerticalAlignment.TOP,    VerticalAlignment.valueOf("TOP"));
        assertSame(VerticalAlignment.MIDDLE, VerticalAlignment.valueOf("MIDDLE"));
        assertSame(VerticalAlignment.BOTTOM, VerticalAlignment.valueOf("BOTTOM"));
    }

    @Test
    public void verticalAlignment_ordinalsAreCorrect() {
        assertEquals(0, VerticalAlignment.TOP.ordinal());
        assertEquals(1, VerticalAlignment.MIDDLE.ordinal());
        assertEquals(2, VerticalAlignment.BOTTOM.ordinal());
    }

    @Test
    public void verticalAlignment_namesAreCorrect() {
        assertEquals("TOP",    VerticalAlignment.TOP.name());
        assertEquals("MIDDLE", VerticalAlignment.MIDDLE.name());
        assertEquals("BOTTOM", VerticalAlignment.BOTTOM.name());
    }

    // ── Combined ─────────────────────────────────────────────────────────────

    @Test
    public void allNineAlignmentCombinationsExist() {
        for (HorizontalAlignment h : HorizontalAlignment.values()) {
            for (VerticalAlignment v : VerticalAlignment.values()) {
                assertNotNull(h);
                assertNotNull(v);
            }
        }
        assertEquals(9, HorizontalAlignment.values().length * VerticalAlignment.values().length);
    }
}
