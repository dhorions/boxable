package be.quodlibet.boxable.tutorial;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.Test;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

/**
 * Tutorial 02: HTML Formatting
 * 
 * This tutorial demonstrates all supported HTML tags in Boxable:
 * - Text formatting: <b>, <i>, <u>, <s>
 * - Headings: <h1> through <h6>
 * - Superscript and subscript: <sup>, <sub>
 * - Line breaks: <br>
 * - Paragraphs: <p>
 * - Lists: <ul>, <ol>, <li>
 * - Nested tags
 */
public class Tutorial02_HtmlFormatting {

    @Test
    public void demonstrateHtmlFormatting() throws IOException {
        // Initialize PDF Document
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        // Define table dimensions
        float margin = 50;
        float yStart = page.getMediaBox().getHeight() - margin;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - margin;
        float bottomMargin = 70;

        // Create the table
        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, 
                                        tableWidth, margin, document, page, 
                                        true, true);

        // Title Row
        Row<PDPage> titleRow = table.createRow(30f);
        Cell<PDPage> titleCell = titleRow.createCell(100, "Tutorial 02: HTML Formatting");
        titleCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        titleCell.setFontSize(16f);
        titleCell.setFillColor(new Color(231, 76, 60));  // Red background
        titleCell.setTextColor(Color.WHITE);
        titleCell.setAlign(HorizontalAlignment.CENTER);

        // Description
        Row<PDPage> descRow = table.createRow(20f);
        Cell<PDPage> descCell = descRow.createCell(100, 
            "Boxable supports various HTML tags for rich text formatting.");
        descCell.setFillColor(new Color(236, 240, 241));
        descCell.setAlign(HorizontalAlignment.CENTER);

        // Header Row
        Row<PDPage> headerRow = table.createRow(15f);
        Cell<PDPage> htmlHeader = headerRow.createCell(40, "HTML Code");
        Cell<PDPage> descHeader = headerRow.createCell(60, "Description");
        htmlHeader.setFillColor(new Color(52, 73, 94));
        descHeader.setFillColor(new Color(52, 73, 94));
        htmlHeader.setTextColor(Color.WHITE);
        descHeader.setTextColor(Color.WHITE);

        // Text Formatting Examples
        addExampleRow(table, "<b>Bold text</b>", "Makes text bold");
        addExampleRow(table, "<i>Italic text</i>", "Makes text italic");
        addExampleRow(table, "<u>Underlined text</u>", "Underlines text");
        addExampleRow(table, "<s>Strikethrough text</s>", "Strikes through text");
        
        // Nested formatting
        addExampleRow(table, "<b><i>Bold and Italic</i></b>", "Combines multiple styles");
        addExampleRow(table, "<u><b>Bold Underlined</b></u>", "Underlined bold text");

        // Headings
        addSectionHeader(table, "Heading Tags");
        addExampleRow(table, "<h1>Heading 1</h1>", "Largest heading");
        addExampleRow(table, "<h2>Heading 2</h2>", "Second-level heading");
        addExampleRow(table, "<h3>Heading 3</h3>", "Third-level heading");
        addExampleRow(table, "<h6>Heading 6</h6>", "Smallest heading");

        // Superscript and Subscript (NEW FEATURES)
        addSectionHeader(table, "Superscript and Subscript (New!)");
        addExampleRow(table, "H<sub>2</sub>O", "Water formula with subscript");
        addExampleRow(table, "x<sup>2</sup> + y<sup>2</sup>", "Mathematical exponents");
        addExampleRow(table, "CO<sub>2</sub>", "Carbon dioxide formula");
        addExampleRow(table, "E=mc<sup>2</sup>", "Einstein's equation");
        addExampleRow(table, "A<sup>n</sup><sub>i</sub>", "Combined super and subscript");

        // Line Breaks and Paragraphs
        addSectionHeader(table, "Line Breaks and Paragraphs");
        addExampleRow(table, "First line<br/>Second line<br/>Third line", 
                     "Line breaks separate lines");
        addExampleRow(table, "<p>First paragraph</p><p>Second paragraph</p>", 
                     "Paragraphs create vertical spacing");

        // Lists
        addSectionHeader(table, "Lists");
        addExampleRow(table, "<ul><li>Item 1</li><li>Item 2</li><li>Item 3</li></ul>", 
                     "Unordered (bullet) list");
        addExampleRow(table, "<ol><li>First</li><li>Second</li><li>Third</li></ol>", 
                     "Ordered (numbered) list");

        // Complex Nested Example
        addSectionHeader(table, "Complex Nested Tags");
        addExampleRow(table, 
            "<b>Bold with <i>italic</i> and <u>underline</u></b>",
            "Multiple nested tags work together");
        addExampleRow(table,
            "<u>Underlined with <sup>superscript</sup> and <sub>subscript</sub></u>",
            "Combines underline with super/subscripts");

        // Draw the table
        table.draw();

        // Save the document
        File file = new File("target/tutorials/Tutorial02_HtmlFormatting.pdf");
        System.out.println("Tutorial 02 PDF saved at: " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        document.save(file);
        document.close();
    }

    /**
     * Helper method to add an example row with alternating colors
     */
    private void addExampleRow(BaseTable table, String htmlCode, String description) throws IOException {
        Row<PDPage> row = table.createRow(15f);
        Cell<PDPage> codeCell = row.createCell(40, htmlCode);
        Cell<PDPage> descCell = row.createCell(60, description);
        
        // Alternate colors for readability
        Color rowColor = (table.getRows().size() % 2 == 0) ? 
                         Color.WHITE : new Color(236, 240, 241);
        codeCell.setFillColor(rowColor);
        descCell.setFillColor(rowColor);
    }

    /**
     * Helper method to add a section header
     */
    private void addSectionHeader(BaseTable table, String title) throws IOException {
        Row<PDPage> sectionRow = table.createRow(15f);
        Cell<PDPage> sectionCell = sectionRow.createCell(100, title);
        sectionCell.setFillColor(new Color(149, 165, 166));
        sectionCell.setTextColor(Color.WHITE);
        sectionCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
    }
}
