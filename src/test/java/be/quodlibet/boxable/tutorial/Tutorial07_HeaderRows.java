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
 * Tutorial 07: Header Rows
 * 
 * This tutorial demonstrates:
 * - Creating single header rows
 * - Creating multiple header rows
 * - Header styling
 * - Headers spanning multiple pages
 * - Header row repetition on new pages
 */
public class Tutorial07_HeaderRows {

    @Test
    public void demonstrateHeaderRows() throws IOException {
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
        Cell<PDPage> titleCell = titleRow.createCell(100, "Tutorial 07: Header Rows");
        titleCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        titleCell.setFontSize(16f);
        titleCell.setFillColor(new Color(142, 68, 173));  // Purple background
        titleCell.setTextColor(Color.WHITE);
        titleCell.setAlign(HorizontalAlignment.CENTER);

        // Description
        Row<PDPage> descRow = table.createRow(20f);
        Cell<PDPage> descCell = descRow.createCell(100, 
            "Learn how to create header rows that repeat on each page of multi-page tables.");
        descCell.setFillColor(new Color(236, 240, 241));
        descCell.setAlign(HorizontalAlignment.CENTER);

        // Section 1: Single Header Row
        addSectionHeader(table, "Single Header Row");
        
        Row<PDPage> singleHeader = table.createRow(20f);
        Cell<PDPage> col1 = singleHeader.createCell(25, "Name");
        Cell<PDPage> col2 = singleHeader.createCell(25, "Age");
        Cell<PDPage> col3 = singleHeader.createCell(25, "City");
        Cell<PDPage> col4 = singleHeader.createCell(25, "Country");
        styleHeaderRow(singleHeader);
        table.addHeaderRow(singleHeader);
        
        // Add some data rows
        for (int i = 1; i <= 5; i++) {
            Row<PDPage> dataRow = table.createRow(15f);
            dataRow.createCell(25, "Person " + i);
            dataRow.createCell(25, String.valueOf(20 + i));
            dataRow.createCell(25, "City " + i);
            dataRow.createCell(25, "Country " + i);
            
            if (i % 2 == 0) {
                for (Cell<PDPage> cell : dataRow.getCells()) {
                    cell.setFillColor(new Color(236, 240, 241));
                }
            }
        }

        // Section 2: Multiple Header Rows
        addSectionHeader(table, "Multiple Header Rows");
        
        // Create a new table to demonstrate multiple headers
        float newYStart = table.draw() - 30;
        if (table.getCurrentPage() != page) {
            page = table.getCurrentPage();
        }
        
        BaseTable table2 = new BaseTable(newYStart, yStartNewPage, bottomMargin, 
                                         tableWidth, margin, document, page, 
                                         true, true);
        
        // First header row - Main title
        Row<PDPage> mainHeader = table2.createRow(20f);
        Cell<PDPage> mainHeaderCell = mainHeader.createCell(100, "2024 Sales Report");
        mainHeaderCell.setFillColor(new Color(41, 128, 185));
        mainHeaderCell.setTextColor(Color.WHITE);
        mainHeaderCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        mainHeaderCell.setAlign(HorizontalAlignment.CENTER);
        table2.addHeaderRow(mainHeader);
        
        // Second header row - Subtitle
        Row<PDPage> subHeader = table2.createRow(15f);
        Cell<PDPage> subHeaderCell = subHeader.createCell(100, "Quarterly Performance Analysis");
        subHeaderCell.setFillColor(new Color(52, 152, 219));
        subHeaderCell.setTextColor(Color.WHITE);
        subHeaderCell.setAlign(HorizontalAlignment.CENTER);
        table2.addHeaderRow(subHeader);
        
        // Third header row - Column headers
        Row<PDPage> colHeader = table2.createRow(15f);
        colHeader.createCell(40, "Product");
        colHeader.createCell(20, "Q1");
        colHeader.createCell(20, "Q2");
        colHeader.createCell(20, "Q3");
        styleHeaderRow(colHeader);
        table2.addHeaderRow(colHeader);
        
        // Data rows
        String[][] salesData = {
            {"Widget A", "$10,000", "$12,000", "$11,500"},
            {"Widget B", "$8,500", "$9,200", "$9,800"},
            {"Widget C", "$15,000", "$16,500", "$17,200"},
            {"Widget D", "$7,200", "$7,800", "$8,100"},
            {"Widget E", "$11,000", "$10,500", "$11,800"}
        };
        
        for (int i = 0; i < salesData.length; i++) {
            Row<PDPage> dataRow = table2.createRow(15f);
            dataRow.createCell(40, salesData[i][0]);
            dataRow.createCell(20, salesData[i][1]);
            dataRow.createCell(20, salesData[i][2]);
            dataRow.createCell(20, salesData[i][3]);
            
            // Right-align numeric columns
            for (int j = 1; j < 4; j++) {
                dataRow.getCells().get(j).setAlign(HorizontalAlignment.RIGHT);
            }
            
            if (i % 2 == 0) {
                for (Cell<PDPage> cell : dataRow.getCells()) {
                    cell.setFillColor(new Color(236, 240, 241));
                }
            }
        }
        
        // Draw the second table
        newYStart = table2.draw() - 30;
        if (table2.getCurrentPage() != page) {
            page = table2.getCurrentPage();
        }

        // Section 3: Multi-Page Table with Header Repetition
        BaseTable table3 = new BaseTable(newYStart, yStartNewPage, bottomMargin, 
                                         tableWidth, margin, document, page, 
                                         true, true);
        
        Row<PDPage> multiTitle = table3.createRow(20f);
        Cell<PDPage> multiTitleCell = multiTitle.createCell(100, "Multi-Page Table with Repeating Headers");
        multiTitleCell.setFillColor(new Color(44, 62, 80));
        multiTitleCell.setTextColor(Color.WHITE);
        multiTitleCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        multiTitleCell.setAlign(HorizontalAlignment.CENTER);
        
        Row<PDPage> multiDesc = table3.createRow(15f);
        Cell<PDPage> multiDescCell = multiDesc.createCell(100, 
            "This table has many rows. The header will repeat on the next page.");
        multiDescCell.setFillColor(new Color(236, 240, 241));
        
        Row<PDPage> multiHeader = table3.createRow(15f);
        multiHeader.createCell(10, "#");
        multiHeader.createCell(30, "Item Name");
        multiHeader.createCell(30, "Description");
        multiHeader.createCell(15, "Quantity");
        multiHeader.createCell(15, "Price");
        styleHeaderRow(multiHeader);
        table3.addHeaderRow(multiHeader);
        
        // Generate many rows to force page break
        for (int i = 1; i <= 60; i++) {
            Row<PDPage> dataRow = table3.createRow(15f);
            dataRow.createCell(10, String.valueOf(i));
            dataRow.createCell(30, "Item " + i);
            dataRow.createCell(30, "Description for item " + i);
            dataRow.createCell(15, String.valueOf(i * 10));
            dataRow.createCell(15, "$" + String.format("%.2f", i * 9.99));
            
            // Right-align numeric columns
            dataRow.getCells().get(3).setAlign(HorizontalAlignment.RIGHT);
            dataRow.getCells().get(4).setAlign(HorizontalAlignment.RIGHT);
            
            if (i % 2 == 0) {
                for (Cell<PDPage> cell : dataRow.getCells()) {
                    cell.setFillColor(new Color(236, 240, 241));
                }
            }
        }
        
        table3.draw();

        // Save the document
        File file = new File("target/tutorials/Tutorial07_HeaderRows.pdf");
        System.out.println("Tutorial 07 PDF saved at: " + file.getAbsolutePath());
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
