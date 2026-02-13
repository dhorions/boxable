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
 * Tutorial 01: Basic Table Creation
 * 
 * This tutorial demonstrates:
 * - Creating a simple PDF table
 * - Adding header rows with styling
 * - Creating data rows
 * - Basic cell styling (colors, fonts, alignment)
 * - Saving the PDF document
 */
public class Tutorial01_BasicTable {

    @Test
    public void createBasicTable() throws IOException {
        // Step 1: Initialize PDF Document and Page
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        // Step 2: Define table dimensions and margins
        float margin = 50;
        float yStart = page.getMediaBox().getHeight() - margin;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - margin;
        float bottomMargin = 70;

        // Step 3: Create the table
        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, 
                                        tableWidth, margin, document, page, 
                                        true, true);

        // Step 4: Create a title row
        Row<PDPage> titleRow = table.createRow(30f);
        Cell<PDPage> titleCell = titleRow.createCell(100, "Tutorial 01: Basic Table Creation");
        titleCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        titleCell.setFontSize(16f);
        titleCell.setFillColor(new Color(41, 128, 185));  // Blue background
        titleCell.setTextColor(Color.WHITE);
        titleCell.setAlign(HorizontalAlignment.CENTER);

        // Step 5: Create a description row
        Row<PDPage> descRow = table.createRow(20f);
        Cell<PDPage> descCell = descRow.createCell(100, 
            "This table demonstrates basic table creation, headers, and cell styling.");
        descCell.setFillColor(new Color(236, 240, 241));  // Light gray background
        descCell.setAlign(HorizontalAlignment.CENTER);

        // Step 6: Create header row
        Row<PDPage> headerRow = table.createRow(20f);
        headerRow.createCell(25, "Product");
        headerRow.createCell(25, "Category");
        headerRow.createCell(25, "Price");
        headerRow.createCell(25, "Stock");

        // Style header cells
        Color headerColor = new Color(52, 152, 219);  // Blue
        Color headerTextColor = Color.WHITE;
        for (Cell<PDPage> cell : headerRow.getCells()) {
            cell.setFillColor(headerColor);
            cell.setTextColor(headerTextColor);
            cell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
            cell.setAlign(HorizontalAlignment.CENTER);
        }

        // Step 7: Add data rows with alternating colors
        String[][] data = {
            {"Laptop", "Electronics", "$999.99", "15"},
            {"Mouse", "Accessories", "$19.99", "120"},
            {"Keyboard", "Accessories", "$49.99", "85"},
            {"Monitor", "Electronics", "$299.99", "42"},
            {"USB Cable", "Accessories", "$9.99", "200"},
            {"Headphones", "Audio", "$79.99", "67"},
            {"Webcam", "Electronics", "$59.99", "33"},
            {"Desk Lamp", "Furniture", "$34.99", "58"}
        };

        Color lightGray = new Color(236, 240, 241);
        Color white = Color.WHITE;

        for (int i = 0; i < data.length; i++) {
            Row<PDPage> dataRow = table.createRow(15f);
            
            // Alternate row colors for better readability
            Color rowColor = (i % 2 == 0) ? white : lightGray;
            
            dataRow.createCell(25, data[i][0]);
            dataRow.createCell(25, data[i][1]);
            Cell<PDPage> cell3 = dataRow.createCell(25, data[i][2]);
            Cell<PDPage> cell4 = dataRow.createCell(25, data[i][3]);

            // Apply styling
            for (Cell<PDPage> cell : dataRow.getCells()) {
                cell.setFillColor(rowColor);
            }
            
            // Right-align numeric columns
            cell3.setAlign(HorizontalAlignment.RIGHT);
            cell4.setAlign(HorizontalAlignment.RIGHT);
        }

        // Step 8: Add a summary row
        Row<PDPage> summaryRow = table.createRow(20f);
        Cell<PDPage> summaryCell = summaryRow.createCell(100, 
            "Total Items: " + data.length);
        summaryCell.setFillColor(new Color(46, 204, 113));  // Green background
        summaryCell.setTextColor(Color.WHITE);
        summaryCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        summaryCell.setAlign(HorizontalAlignment.CENTER);

        // Step 9: Draw the table
        table.draw();

        // Step 10: Save the document
        File file = new File("target/tutorials/Tutorial01_BasicTable.pdf");
        System.out.println("Tutorial 01 PDF saved at: " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        document.save(file);
        document.close();
    }
}
