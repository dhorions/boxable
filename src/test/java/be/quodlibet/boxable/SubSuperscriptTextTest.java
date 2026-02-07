package be.quodlibet.boxable;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SubSuperscriptTextTest {

    @Test
    public void testSubSuperscriptTagsInCells() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float margin = 30f;
        float bottomMargin = 30f;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;

        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);

        Row<PDPage> titleRow = table.createRow(20f);
        Cell<PDPage> titleCell = titleRow.createCell(100, "Sub/Superscript Tags in Cells");
        titleCell.setFillColor(new Color(41, 128, 185));
        titleCell.setTextColor(Color.WHITE);

        Row<PDPage> descRow = table.createRow(15f);
        descRow.createCell(100,
                "Expected: Subscript appears smaller and below the baseline; superscript appears smaller and above the baseline.");

        Row<PDPage> headerRow = table.createRow(15f);
        Cell<PDPage> sampleHeader = headerRow.createCell(40, "Sample");
        Cell<PDPage> expectedHeader = headerRow.createCell(60, "Expected (visual)");
        sampleHeader.setFillColor(new Color(236, 240, 241));
        expectedHeader.setFillColor(new Color(236, 240, 241));

        Row<PDPage> row1 = table.createRow(15f);
        row1.createCell(40, "H<sub>2</sub>O");
        row1.createCell(60, "The '2' is smaller and sits below the baseline.");

        Row<PDPage> row2 = table.createRow(15f);
        row2.createCell(40, "x<sup>2</sup> + y<sup>2</sup> = z<sup>2</sup>");
        row2.createCell(60, "All exponents are smaller and above the baseline.");

        Row<PDPage> row3 = table.createRow(15f);
        row3.createCell(40, "CO<sub>2</sub> + 2H<sub>2</sub>O");
        row3.createCell(60, "Each subscript number is smaller and below the baseline.");

        Row<PDPage> row4 = table.createRow(15f);
        row4.createCell(40, "A<sup>n</sup><sub>i</sub>");
        row4.createCell(60, "Superscript sits above and subscript sits below the baseline.");

        Row<PDPage> row5 = table.createRow(15f);
        row5.createCell(40, "<u>xx<sup>bla</sub><b>bold</b></u> bla");
        row5.createCell(60, "Nested tags render with underline and bold while keeping the sub/superscript offset.");

        table.draw();

        File file = new File("target/SubSuperscriptTextTest.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();

        assertTrue("PDF file should exist", file.exists());
        assertTrue("PDF file should not be empty", file.length() > 0);
    }
}
