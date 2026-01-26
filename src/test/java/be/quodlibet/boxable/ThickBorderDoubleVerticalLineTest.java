package be.quodlibet.boxable;

import be.quodlibet.boxable.line.LineStyle;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.Test;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class ThickBorderDoubleVerticalLineTest {

    @Test
    public void verticalLinesShouldNotDoubleWhenBorderWidthIsThick() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        BaseTable table = new BaseTable(
            page.getMediaBox().getHeight() - 60,
            page.getMediaBox().getHeight() - 60,
            0,
            200,
            350,
            doc,
            page,
            true,
            true);

        LineStyle thickBorder = new LineStyle(Color.BLACK, 2f);

        Row<PDPage> headerRow = table.createRow(22);
        Cell<PDPage> cell1 = headerRow.createCell(50, "Testing 1");
        cell1.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA));
        cell1.setFontSize(9);
        cell1.setBorderStyle(thickBorder);
        cell1.setFillColor(Color.GRAY);
        cell1.setValign(VerticalAlignment.BOTTOM);

        Cell<PDPage> cell2 = headerRow.createCell(50, "Testing 2");
        cell2.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA));
        cell2.setFontSize(9);
        cell2.setBorderStyle(thickBorder);
        cell2.setFillColor(Color.GRAY);
        cell2.setValign(VerticalAlignment.BOTTOM);

        table.addHeaderRow(headerRow);

        Row<PDPage> row = table.createRow(22);
        Cell<PDPage> cell3 = row.createCell(50, "Testing 3");
        cell3.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA));
        cell3.setFontSize(9);
        cell3.setBorderStyle(thickBorder);
        cell3.setValign(VerticalAlignment.BOTTOM);

        Cell<PDPage> cell4 = row.createCell(50, "Testing 4");
        cell4.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA));
        cell4.setFontSize(9);
        cell4.setBorderStyle(thickBorder);
        cell4.setValign(VerticalAlignment.BOTTOM);

        Row<PDPage> expectedRow = table.createRow(22);
        expectedRow.createCell(100,
            "Expected behavior: the vertical line between cells is the same thickness as the outer border (no doubled line)."
        );

        table.draw();

        File file = new File("target/ThickBorderDoubleVerticalLineTest.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }
}
