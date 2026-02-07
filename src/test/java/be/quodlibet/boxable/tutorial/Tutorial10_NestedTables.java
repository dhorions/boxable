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
 * Tutorial 10: Nested Tables
 * 
 * This tutorial demonstrates:
 * - Creating tables within cells using <table> HTML tag
 * - Complex layouts with nested structures
 * - Practical examples of nested tables
 * - Formatting nested table content
 */
public class Tutorial10_NestedTables {

    @Test
    public void demonstrateNestedTables() throws IOException {
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
        Cell<PDPage> titleCell = titleRow.createCell(100, "Tutorial 10: Nested Tables");
        titleCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        titleCell.setFontSize(16f);
        titleCell.setFillColor(new Color(211, 84, 0));  // Orange background
        titleCell.setTextColor(Color.WHITE);
        titleCell.setAlign(HorizontalAlignment.CENTER);

        // Description
        Row<PDPage> descRow = table.createRow(20f);
        Cell<PDPage> descCell = descRow.createCell(100, 
            "Learn how to create tables within cells using HTML <table> tags for complex layouts.");
        descCell.setFillColor(new Color(236, 240, 241));
        descCell.setAlign(HorizontalAlignment.CENTER);

        // Section 1: Simple Nested Table
        addSectionHeader(table, "Simple Nested Table");
        
        Row<PDPage> simpleDesc = table.createRow(15f);
        simpleDesc.createCell(100, "A basic example of a table within a cell:");
        
        Row<PDPage> simpleRow = table.createRow(80f);
        simpleRow.createCell(40, "<b>Product Details</b>");
        
        // Create a cell with nested table using HTML
        String nestedTableHtml = "<table>" +
            "<tr><td><b>Item:</b></td><td>Widget A</td></tr>" +
            "<tr><td><b>Price:</b></td><td>$99.99</td></tr>" +
            "<tr><td><b>Stock:</b></td><td>Available</td></tr>" +
            "</table>";
        Cell<PDPage> nestedCell = simpleRow.createCell(60, nestedTableHtml);
        nestedCell.setFillColor(new Color(245, 245, 245));

        // Section 2: Multiple Nested Tables
        addSectionHeader(table, "Multiple Nested Tables");
        
        Row<PDPage> multiDesc = table.createRow(15f);
        multiDesc.createCell(100, "Multiple cells with nested tables:");
        
        Row<PDPage> headerRow = table.createRow(15f);
        headerRow.createCell(33.33f, "Product A");
        headerRow.createCell(33.33f, "Product B");
        headerRow.createCell(33.34f, "Product C");
        styleHeaderRow(headerRow);
        
        Row<PDPage> multiRow = table.createRow(70f);
        
        // First nested table
        String table1 = "<table>" +
            "<tr><td><b>Price:</b></td><td>$50</td></tr>" +
            "<tr><td><b>Qty:</b></td><td>10</td></tr>" +
            "<tr><td><b>Total:</b></td><td>$500</td></tr>" +
            "</table>";
        Cell<PDPage> cell1 = multiRow.createCell(33.33f, table1);
        cell1.setFillColor(new Color(255, 243, 224));
        
        // Second nested table
        String table2 = "<table>" +
            "<tr><td><b>Price:</b></td><td>$75</td></tr>" +
            "<tr><td><b>Qty:</b></td><td>8</td></tr>" +
            "<tr><td><b>Total:</b></td><td>$600</td></tr>" +
            "</table>";
        Cell<PDPage> cell2 = multiRow.createCell(33.33f, table2);
        cell2.setFillColor(new Color(230, 245, 255));
        
        // Third nested table
        String table3 = "<table>" +
            "<tr><td><b>Price:</b></td><td>$100</td></tr>" +
            "<tr><td><b>Qty:</b></td><td>5</td></tr>" +
            "<tr><td><b>Total:</b></td><td>$500</td></tr>" +
            "</table>";
        Cell<PDPage> cell3 = multiRow.createCell(33.34f, table3);
        cell3.setFillColor(new Color(255, 235, 238));

        // Section 3: Complex Nested Structure
        addSectionHeader(table, "Complex Nested Structure");
        
        Row<PDPage> complexDesc = table.createRow(15f);
        complexDesc.createCell(100, "A more complex example with formatting:");
        
        Row<PDPage> complexRow = table.createRow(100f);
        
        // Left cell: Regular content
        Cell<PDPage> leftCell = complexRow.createCell(40, 
            "<b>Order #12345</b><br/><br/>" +
            "Customer: John Doe<br/>" +
            "Date: 2024-02-07<br/>" +
            "Status: <b>Shipped</b>");
        leftCell.setFillColor(new Color(236, 240, 241));
        
        // Right cell: Nested table with order details
        String orderTable = "<table>" +
            "<tr><td colspan='3'><b>Order Items</b></td></tr>" +
            "<tr><td><b>Item</b></td><td><b>Qty</b></td><td><b>Price</b></td></tr>" +
            "<tr><td>Widget A</td><td>2</td><td>$99.98</td></tr>" +
            "<tr><td>Widget B</td><td>1</td><td>$49.99</td></tr>" +
            "<tr><td>Widget C</td><td>3</td><td>$149.97</td></tr>" +
            "<tr><td colspan='2'><b>Total:</b></td><td><b>$299.94</b></td></tr>" +
            "</table>";
        Cell<PDPage> rightCell = complexRow.createCell(60, orderTable);
        rightCell.setFillColor(Color.WHITE);

        // Section 4: Nested Tables with Lists
        addSectionHeader(table, "Nested Tables Combined with Lists");
        
        Row<PDPage> listDesc = table.createRow(15f);
        listDesc.createCell(100, "Combining nested tables with HTML lists:");
        
        Row<PDPage> listRow = table.createRow(90f);
        
        Cell<PDPage> featuresCell = listRow.createCell(50, 
            "<b>Product Features:</b><br/>" +
            "<ul>" +
            "<li>High quality</li>" +
            "<li>Durable construction</li>" +
            "<li>Easy to use</li>" +
            "<li>Great value</li>" +
            "</ul>");
        featuresCell.setFillColor(new Color(255, 243, 224));
        
        String specsTable = "<b>Technical Specifications:</b><br/>" +
            "<table>" +
            "<tr><td><b>Dimension:</b></td><td>10x8x2 cm</td></tr>" +
            "<tr><td><b>Weight:</b></td><td>500g</td></tr>" +
            "<tr><td><b>Material:</b></td><td>Plastic</td></tr>" +
            "<tr><td><b>Color:</b></td><td>Blue</td></tr>" +
            "<tr><td><b>Warranty:</b></td><td>2 years</td></tr>" +
            "</table>";
        Cell<PDPage> specsCell = listRow.createCell(50, specsTable);
        specsCell.setFillColor(new Color(230, 245, 255));

        // Section 5: Practical Invoice Example
        addSectionHeader(table, "Practical Example: Invoice");
        
        Row<PDPage> invoiceHeader = table.createRow(60f);
        
        // Company info on left
        String companyInfo = "<b><h2>ACME Corp</h2></b>" +
            "123 Business St.<br/>" +
            "New York, NY 10001<br/>" +
            "Tel: (555) 123-4567<br/>" +
            "Email: info@acme.com";
        Cell<PDPage> companyCell = invoiceHeader.createCell(50, companyInfo);
        companyCell.setFillColor(new Color(245, 245, 245));
        
        // Invoice details on right
        String invoiceDetails = "<table>" +
            "<tr><td><b>Invoice #:</b></td><td>INV-2024-001</td></tr>" +
            "<tr><td><b>Date:</b></td><td>February 7, 2024</td></tr>" +
            "<tr><td><b>Due Date:</b></td><td>March 7, 2024</td></tr>" +
            "<tr><td><b>Terms:</b></td><td>Net 30</td></tr>" +
            "</table>";
        Cell<PDPage> invoiceCell = invoiceHeader.createCell(50, invoiceDetails);
        invoiceCell.setFillColor(Color.WHITE);
        
        // Bill to section
        Row<PDPage> billToRow = table.createRow(50f);
        String billToInfo = "<b>Bill To:</b><br/>" +
            "Jane Smith<br/>" +
            "456 Customer Ave.<br/>" +
            "Boston, MA 02101";
        billToRow.createCell(100, billToInfo);

        // Draw the table
        table.draw();

        // Save the document
        File file = new File("target/tutorials/Tutorial10_NestedTables.pdf");
        System.out.println("Tutorial 10 PDF saved at: " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        document.save(file);
        document.close();
    }

    /**
     * Helper method to add a section header
     */
    private void addSectionHeader(BaseTable table, String title) throws IOException {
        Row<PDPage> sectionRow = table.createRow(18f);
        Cell<PDPage> sectionCell = sectionRow.createCell(100, title);
        sectionCell.setFillColor(new Color(44, 62, 80));
        sectionCell.setTextColor(Color.WHITE);
        sectionCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        sectionCell.setAlign(HorizontalAlignment.CENTER);
    }

    /**
     * Helper method to style header rows
     */
    private void styleHeaderRow(Row<PDPage> row) {
        Color headerColor = new Color(52, 73, 94);
        for (Cell<PDPage> cell : row.getCells()) {
            cell.setFillColor(headerColor);
            cell.setTextColor(Color.WHITE);
            cell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        }
    }
}
