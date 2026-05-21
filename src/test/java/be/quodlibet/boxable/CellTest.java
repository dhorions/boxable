package be.quodlibet.boxable;

import be.quodlibet.boxable.line.LineStyle;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.Color;
import java.io.IOException;

import static org.junit.Assert.*;

public class CellTest {

    private static PDDocument document;
    private static PDPage page;
    private BaseTable table;
    private Row<PDPage> row;

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
        table = new BaseTable(700, 700, 50, 500, 50, document, page, true, true);
        row = table.createRow(20f);
    }

    // ── Text ─────────────────────────────────────────────────────────────────

    @Test
    public void nullTextBecomesEmptyString() {
        Cell<PDPage> cell = row.createCell(50, null);
        assertEquals("", cell.getText());
    }

    // ── Width ─────────────────────────────────────────────────────────────────

    @Test
    public void widthIsCalculatedAsPercentageOfTableWidth() {
        Cell<PDPage> cell = row.createCell(50, "Test");
        assertEquals(250f, cell.getWidth(), 0.001f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cellWidthExceedingRowWidthThrowsException() throws IOException {
        // 150% > 100% — must throw
        row.createCell(150, "Too wide");
    }

    // ── Font ─────────────────────────────────────────────────────────────────

    @Test
    public void headerCellUsesBoldFont() {
        Cell<PDPage> cell = row.createCell(50, "Test");
        PDType1Font bold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        cell.setFontBold(bold);

        cell.setHeaderCell(true);

        assertSame(bold, cell.getFont());
    }

    @Test
    public void paragraphIsInvalidatedOnFontChange() {
        Cell<PDPage> cell = row.createCell(50, "Test");
        Paragraph p1 = cell.getParagraph();

        cell.setFont(new PDType1Font(Standard14Fonts.FontName.COURIER));
        Paragraph p2 = cell.getParagraph();

        assertNotSame(p1, p2);
    }

    // ── copyCellStyle ─────────────────────────────────────────────────────────

    @Test
    public void copyCellStyle_copiesStyleFromSource() throws IOException {
        Row<PDPage> row2 = table.createRow(20f);
        Cell<PDPage> source = row2.createCell(25, "Source");
        source.setFillColor(Color.CYAN);
        source.setTextColor(Color.MAGENTA);
        source.setFontSize(14f);
        source.setAlign(HorizontalAlignment.CENTER);
        source.setValign(VerticalAlignment.BOTTOM);

        Cell<PDPage> target = row2.createCell(25, "Target");
        target.copyCellStyle(source);

        assertEquals(Color.CYAN,              target.getFillColor());
        assertEquals(Color.MAGENTA,           target.getTextColor());
        assertEquals(14f,                     target.getFontSize(), 0.001f);
        assertEquals(HorizontalAlignment.CENTER, target.getAlign());
        assertEquals(VerticalAlignment.BOTTOM,   target.getValign());
    }

    // ── Border ───────────────────────────────────────────────────────────────

    @Test
    public void setBorderStyle_setsAllFourBorders() {
        Cell<PDPage> cell = row.createCell(100, "Test");
        LineStyle border = new LineStyle(Color.RED, 2f);

        cell.setBorderStyle(border);

        assertSame(border, cell.getLeftBorder());
        assertSame(border, cell.getRightBorder());
        assertSame(border, cell.getTopBorder());
        assertSame(border, cell.getBottomBorder());
    }

    @Test
    public void setBorderStyleNull_removesAllBorders() {
        Cell<PDPage> cell = row.createCell(100, "Test");

        cell.setBorderStyle(null);

        assertNull(cell.getLeftBorder());
        assertNull(cell.getRightBorder());
        assertNull(cell.getTopBorder());
        assertNull(cell.getBottomBorder());
    }
}
