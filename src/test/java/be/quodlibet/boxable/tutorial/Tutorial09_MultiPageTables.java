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
 * Tutorial 09: Multi-Page Tables
 * 
 * This tutorial demonstrates:
 * - Automatic page breaks
 * - Large content handling across multiple pages
 * - Header repetition on new pages
 * - Managing page transitions
 */
public class Tutorial09_MultiPageTables {

    @Test
    public void demonstrateMultiPageTables() throws IOException {
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
        Cell<PDPage> titleCell = titleRow.createCell(100, "Tutorial 09: Multi-Page Tables");
        titleCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        titleCell.setFontSize(16f);
        titleCell.setFillColor(new Color(22, 160, 133));  // Turquoise background
        titleCell.setTextColor(Color.WHITE);
        titleCell.setAlign(HorizontalAlignment.CENTER);

        // Description
        Row<PDPage> descRow = table.createRow(20f);
        Cell<PDPage> descCell = descRow.createCell(100, 
            "Demonstrate how tables automatically span multiple pages with repeating headers.");
        descCell.setFillColor(new Color(236, 240, 241));
        descCell.setAlign(HorizontalAlignment.CENTER);

        // Create repeating header
        Row<PDPage> headerRow = table.createRow(20f);
        headerRow.createCell(10, "#");
        headerRow.createCell(25, "Product Name");
        headerRow.createCell(25, "Description");
        headerRow.createCell(15, "Price");
        headerRow.createCell(10, "Qty");
        headerRow.createCell(15, "Total");
        styleHeaderRow(headerRow);
        table.addHeaderRow(headerRow);

        // Generate 100 rows to force multiple page breaks
        Color lightBlue = new Color(236, 240, 241);
        Color white = Color.WHITE;
        
        for (int i = 1; i <= 100; i++) {
            Row<PDPage> dataRow = table.createRow(15f);
            
            // Row color alternation
            Color rowColor = (i % 2 == 0) ? white : lightBlue;
            
            Cell<PDPage> numCell = dataRow.createCell(10, String.valueOf(i));
            dataRow.createCell(25, "Product " + i);
            dataRow.createCell(25, 
                "Description for product number " + i);
            Cell<PDPage> priceCell = dataRow.createCell(15, 
                String.format("$%.2f", i * 9.99));
            Cell<PDPage> qtyCell = dataRow.createCell(10, String.valueOf(i % 50 + 1));
            Cell<PDPage> totalCell = dataRow.createCell(15, 
                String.format("$%.2f", (i * 9.99) * (i % 50 + 1)));
            
            // Apply styling
            for (Cell<PDPage> cell : dataRow.getCells()) {
                cell.setFillColor(rowColor);
            }
            
            // Right-align numeric columns
            numCell.setAlign(HorizontalAlignment.CENTER);
            priceCell.setAlign(HorizontalAlignment.RIGHT);
            qtyCell.setAlign(HorizontalAlignment.CENTER);
            totalCell.setAlign(HorizontalAlignment.RIGHT);
            
            // Add section markers at page transitions
            if (i % 25 == 0) {
                Row<PDPage> sectionRow = table.createRow(20f);
                Cell<PDPage> sectionCell = sectionRow.createCell(100, 
                    String.format("=== Section %d (Items %d-%d) ===", 
                                  (i/25), (i-24), i));
                sectionCell.setFillColor(new Color(52, 152, 219));
                sectionCell.setTextColor(Color.WHITE);
                sectionCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
                sectionCell.setAlign(HorizontalAlignment.CENTER);
            }
        }

        // Summary row
        Row<PDPage> summaryRow = table.createRow(20f);
        Cell<PDPage> summaryLabel = summaryRow.createCell(85, "TOTAL ITEMS:");
        summaryLabel.setAlign(HorizontalAlignment.RIGHT);
        summaryLabel.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        summaryLabel.setFillColor(new Color(46, 204, 113));
        summaryLabel.setTextColor(Color.WHITE);
        
        Cell<PDPage> summaryValue = summaryRow.createCell(15, "100");
        summaryValue.setAlign(HorizontalAlignment.CENTER);
        summaryValue.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        summaryValue.setFillColor(new Color(46, 204, 113));
        summaryValue.setTextColor(Color.WHITE);

        // Draw the table
        table.draw();

        // Get statistics about the generated PDF
        int pageCount = document.getNumberOfPages();
        System.out.println("Multi-page table generated " + pageCount + " pages");

        // Save the document
        File file = new File("target/tutorials/Tutorial09_MultiPageTables.pdf");
        System.out.println("Tutorial 09 PDF saved at: " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        document.save(file);
        document.close();
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
