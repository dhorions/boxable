package be.quodlibet.boxable;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.Test;

import be.quodlibet.boxable.line.LineStyle;

public class InnerTableStylingTest {

    @Test
    public void testInnerTableAttributes() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);

        float margin = 50f;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStart = page.getMediaBox().getUpperRightY() - margin;
        float bottomMargin = margin;

        BaseTable table = new BaseTable(yStart, yStart, bottomMargin, tableWidth, margin, doc, page, true, true);

        // Header
        Row<PDPage> header = table.createRow(20f);
        header.createCell(100, "Inner Table Styling Limitations Test").setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        table.addHeaderRow(header);

        // Description
        Row<PDPage> descRow = table.createRow(20f);
        descRow.createCell(100, "The second row below attempts to set Red Text Color and Courier Bold Font on the TableCell (inner table container). " +
                "Expected: Background is Gray, Borders are Blue. Text is RED and COURIER BOLD (propagated).");

        // Row 1: Normal Cell Reference
        Row<PDPage> row1 = table.createRow(20f);
        Cell<PDPage> cell1 = row1.createCell(100, "Reference: Normal Cell with RED text and Courier Bold");
        cell1.setTextColor(Color.RED);
        cell1.setFont(new PDType1Font(Standard14Fonts.FontName.COURIER_BOLD));

        // Row 2: Inner Table Cell
        Row<PDPage> row2 = table.createRow(50f);
        String html = "<table>" +
                      "<tr><td>Inner Cell 1</td><td>Inner Cell 2</td></tr>" +
                      "<tr><td>Inner Cell 3</td><td><b>Bold Tag Works</b></td></tr>" +
                      "</table>";
        
        TableCell<PDPage> tableCell = row2.createTableCell(100, html, doc, page, yStart, margin, bottomMargin);
        
        // Attempt to style the TableCell (Container)
        tableCell.setTextColor(Color.RED); // Expecting this NOT to apply to inner cells
        tableCell.setFont(new PDType1Font(Standard14Fonts.FontName.COURIER_BOLD)); // Expecting this NOT to apply
        tableCell.setFillColor(Color.LIGHT_GRAY); // Expecting this TO apply to background
        
        // Inner Table specific styling
        tableCell.setInnerTableDrawLines(true);
        tableCell.setInnerTableBorderStyle(new LineStyle(Color.BLUE, 1)); // Blue borders for inner table

        table.draw();

        File file = new File("target/InnerTableStylingTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
    }
}
