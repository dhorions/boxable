package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class RowTest {

    private static PDDocument document;
    private static PDPage page;
    private BaseTable table;

    @BeforeClass
    public static void setUpClass() throws IOException {
        document = new PDDocument();
        page = new PDPage(PDRectangle.A4);
        document.addPage(page);
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        document.close();
    }

    @Before
    public void setUp() throws IOException {
        // width=500, margin=50
        table = new BaseTable(700, 700, 50, 500, 50, document, page, true, true);
    }

    // ── Border auto-removal ───────────────────────────────────────────────────

    @Test
    public void firstCellHasLeftBorder() {
        Row<PDPage> row = table.createRow(20f);
        Cell<PDPage> first = row.createCell(50, "First");
        assertNotNull(first.getLeftBorder());
    }

    @Test
    public void secondCellHasNoLeftBorderToAvoidDoubling() {
        Row<PDPage> row = table.createRow(20f);
        row.createCell(50, "First");
        Cell<PDPage> second = row.createCell(50, "Second");
        assertNull(second.getLeftBorder());
    }

    // ── Header row ────────────────────────────────────────────────────────────

    @Test
    public void headerRowCellsAreMarkedAsHeaderCells() {
        Row<PDPage> row = table.createRow(20f);
        row.setHeaderRow(true);

        Cell<PDPage> cell = row.createCell(100, "Header");

        assertTrue(cell.isHeaderCell());
    }

    // ── Line spacing propagation ──────────────────────────────────────────────

    @Test
    public void lineSpacingPropagatestoNewCells() {
        Row<PDPage> row = table.createRow(20f);
        row.setLineSpacing(2.0f);

        Cell<PDPage> cell = row.createCell(100, "Test");

        assertEquals(2.0f, cell.getLineSpacing(), 0.001f);
    }

    // ── Height ───────────────────────────────────────────────────────────────

    @Test
    public void rowHeightIsAtLeastZero() {
        Row<PDPage> row = table.createRow(30f);
        assertTrue(row.getHeight() >= 0);
    }

    @Test
    public void rowHeightIsMaxOfCellHeights() {
        Row<PDPage> row = table.createRow(10f);
        row.createCell(50, "Short");
        row.createCell(50,
            "This is a much longer text that should wrap to multiple lines " +
            "and create a taller cell height within the same row.");

        float rowHeight = row.getHeight();
        for (Cell<PDPage> c : row.getCells()) {
            assertTrue(rowHeight >= c.getCellHeight());
        }
    }

    // ── xEnd ──────────────────────────────────────────────────────────────────

    @Test
    public void xEndReturnsMarginPlusWidth() {
        Row<PDPage> row = table.createRow(20f);
        // margin=50 + width=500
        assertEquals(550f, row.xEnd(), 0.001f);
    }

    // ── lastCellExtraWidth ────────────────────────────────────────────────────

    @Test
    public void lastCellExtraWidthReturnsRemainingWidth() {
        Row<PDPage> row = table.createRow(20f);
        row.createCell(30, "A");   // 30% of 500 = 150
        row.createCell(30, "B");   // 30% of 500 = 150; used = 300

        assertEquals(200f, row.getLastCellExtraWidth(), 0.001f);
    }
}
