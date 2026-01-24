package be.quodlibet.boxable;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.Test;

public class InnerTableHTMLStylingTest {

    @Test
    public void testHTMLAttributesStyling() throws IOException {
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
        header.createCell(100, "HTML Styling Attributes Test");
        table.addHeaderRow(header);

        // Description
        Row<PDPage> descRow = table.createRow(40f);
        descRow.createCell(100, "Testing HTML attributes for styling inner table cells. " +
                "Supported: color, background-color (style & bgcolor), font-family.");

        // Row 1: HTML Styling
        Row<PDPage> row1 = table.createRow(100f);
        
        // HTML content with various styles
        String html = "<table>" +
                "<tr>" +
                // Text Color
                "<td style=\"color: #FF0000;\">Red Text (style='color: #FF0000')</td>" +
                 // Background Color (style)
                "<td style=\"background-color: #FFFF00;\">Yellow Background (style)</td>" +
                "</tr>" +
                "<tr>" +
                // Background Color (attribute)
                "<td bgcolor=\"#00FFFF\">Cyan Background (bgcolor='#00FFFF')</td>" +
                // Font Family
                "<td style=\"font-family: Courier-Bold;\">Courier Bold (font-family)</td>" +
                "</tr>" +
                "<tr>" + 
                // Mixed: Font and Color
                "<td style=\"font-family: Times-Italic; color: #0000FF;\">Times Italic Blue</td>" +
                // Default
                "<td>Normal Text</td>" +
                "</tr>" +
                "</table>";

        row1.createTableCell(100, html, doc, page, yStart, margin, bottomMargin);

        table.draw();

        File file = new File("target/InnerTableHTMLStylingTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
    }

    @Test
    public void testAdvancedHTMLColors() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);

        float margin = 50f;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStart = page.getMediaBox().getUpperRightY() - margin;
        float bottomMargin = margin;

        BaseTable table = new BaseTable(yStart, yStart, bottomMargin, tableWidth, margin, doc, page, true, true);

        Row<PDPage> header = table.createRow(20f);
        header.createCell(100, "Advanced HTML Color Parsing");
        table.addHeaderRow(header);

        Row<PDPage> row1 = table.createRow(150f);
        
        String html = "<table>" +
                "<tr>" +
                "<td style=\"background-color: cornflowerblue; color: white;\">CornflowerBlue (Named)</td>" +
                "<td style=\"background-color: #F00; color: #0F0;\">Red Bg, Green Text (#RGB)</td>" +
                "</tr>" +
                "<tr>" +
                "<td style=\"background-color: rgb(0, 128, 0); color: rgb(255, 255, 255);\">Dark Green (rgb)</td>" +
                // Note: PDFBox might support alpha if setFillColor handles it, which Boxable usually does
                "<td style=\"background-color: rgba(255, 0, 0, 0.5);\">Semi-transparent Red (rgba)</td>" +
                "</tr>" +
                 "<tr>" +
                "<td style=\"border-color: deepskyblue; border: 2px solid;\">Border Color (DeepSkyBlue)</td>" +
                "<td style=\"color: crimson; font-family: Helvetica-BoldOblique;\">Crimson Bold Oblique</td>" +
                "</tr>" +
                "</table>";

        row1.createTableCell(100, html, doc, page, yStart, margin, bottomMargin);

        table.draw();

        File file = new File("target/InnerTableAdvancedColorsTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
    }
}
