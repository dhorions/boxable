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
 * Tutorial 11: Fixed Height Rows
 * 
 * This tutorial demonstrates:
 * - Creating fixed-height rows
 * - Auto-fit text in fixed-height cells
 * - Flexible vs fixed height rows
 * - Font size adjustment for fixed heights
 */
public class Tutorial11_FixedHeightRows {

    @Test
    public void demonstrateFixedHeightRows() throws IOException {
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
        Cell<PDPage> titleCell = titleRow.createCell(100, "Tutorial 11: Fixed Height Rows");
        titleCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        titleCell.setFontSize(16f);
        titleCell.setFillColor(new Color(192, 57, 43));  // Dark red background
        titleCell.setTextColor(Color.WHITE);
        titleCell.setAlign(HorizontalAlignment.CENTER);

        // Description
        Row<PDPage> descRow = table.createRow(20f);
        Cell<PDPage> descCell = descRow.createCell(100, 
            "Learn how to create fixed-height rows where text automatically shrinks to fit.");
        descCell.setFillColor(new Color(236, 240, 241));
        descCell.setAlign(HorizontalAlignment.CENTER);

        // Section 1: Flexible vs Fixed Height
        addSectionHeader(table, "Flexible vs Fixed Height Comparison");
        
        Row<PDPage> compHeader = table.createRow(15f);
        compHeader.createCell(50, "Row Type");
        compHeader.createCell(50, "Behavior");
        styleHeaderRow(compHeader);
        
        Row<PDPage> flexDesc = table.createRow(15f);
        flexDesc.createCell(50, "Flexible Row");
        flexDesc.createCell(50, "Height expands to fit content");
        
        Row<PDPage> fixedDesc = table.createRow(15f);
        fixedDesc.createCell(50, "Fixed Height Row");
        fixedDesc.createCell(50, "Text shrinks to fit specified height");

        // Section 2: Flexible Row Example
        addSectionHeader(table, "Flexible Row (Default Behavior)");
        
        Row<PDPage> flexDesc2 = table.createRow(15f);
        flexDesc2.createCell(100, "This row will grow to accommodate the content:");
        
        // Flexible row - will expand
        Row<PDPage> flexibleRow = table.createRow(12f);
        Cell<PDPage> flexCell = flexibleRow.createCell(100, 
            "This is a flexible row with a lot of text content that would normally require " +
            "more space than the initial 12pt height. The row will automatically expand to " +
            "fit all of this text content while maintaining the specified font size.");
        flexCell.setFillColor(new Color(174, 214, 241));
        flexCell.setFontSize(12f);

        // Section 3: Fixed Height Row Example
        addSectionHeader(table, "Fixed Height Row (Text Shrinks to Fit)");
        
        Row<PDPage> fixedDesc2 = table.createRow(15f);
        fixedDesc2.createCell(100, "This row will NOT grow - text shrinks instead:");
        
        // Fixed height row - text will shrink
        Row<PDPage> fixedRow = table.createRow(12f);
        fixedRow.setFixedHeight(true);
        Cell<PDPage> fixedCell = fixedRow.createCell(100, 
            "This is a fixed-height row with the same amount of text content. " +
            "Instead of expanding, the text will be automatically shrunk to fit " +
            "within the specified 12pt height constraint. Notice the smaller font size.");
        fixedCell.setFillColor(new Color(255, 243, 224));
        fixedCell.setFontSize(12f);
        fixedCell.setTopPadding(1f);
        fixedCell.setBottomPadding(1f);

        // Section 4: Multiple Fixed Height Rows
        addSectionHeader(table, "Multiple Fixed Height Rows");
        
        Row<PDPage> multiDesc = table.createRow(15f);
        multiDesc.createCell(100, "Demonstrating various fixed heights:");
        
        Row<PDPage> multiHeader = table.createRow(15f);
        multiHeader.createCell(20, "Height");
        multiHeader.createCell(80, "Content");
        styleHeaderRow(multiHeader);
        
        // 10pt fixed height
        Row<PDPage> fixed10 = table.createRow(10f);
        fixed10.setFixedHeight(true);
        fixed10.createCell(20, "10pt");
        Cell<PDPage> content10 = fixed10.createCell(80, 
            "Fixed at 10pt - Text automatically adjusts to fit this small height.");
        content10.setFillColor(new Color(255, 235, 238));
        content10.setTopPadding(1f);
        content10.setBottomPadding(1f);
        
        // 15pt fixed height
        Row<PDPage> fixed15 = table.createRow(15f);
        fixed15.setFixedHeight(true);
        fixed15.createCell(20, "15pt");
        Cell<PDPage> content15 = fixed15.createCell(80, 
            "Fixed at 15pt - More space allows slightly larger text while still constraining the height.");
        content15.setFillColor(new Color(230, 245, 255));
        content15.setTopPadding(1f);
        content15.setBottomPadding(1f);
        
        // 20pt fixed height
        Row<PDPage> fixed20 = table.createRow(20f);
        fixed20.setFixedHeight(true);
        fixed20.createCell(20, "20pt");
        Cell<PDPage> content20 = fixed20.createCell(80, 
            "Fixed at 20pt - Even more space available, text can be larger but still constrained by the fixed height.");
        content20.setFillColor(new Color(255, 250, 240));
        content20.setTopPadding(1f);
        content20.setBottomPadding(1f);

        // Section 5: Fixed Height Headers
        addSectionHeader(table, "Fixed Height Header Rows");
        
        Row<PDPage> headerDesc = table.createRow(15f);
        headerDesc.createCell(100, "Headers can also use fixed heights for consistent appearance:");
        
        // Create a table with fixed-height header
        Row<PDPage> fixedHeader = table.createRow(12f);
        fixedHeader.setFixedHeight(true);
        Cell<PDPage> hCell1 = fixedHeader.createCell(33.33f, "Column A with Long Name");
        Cell<PDPage> hCell2 = fixedHeader.createCell(33.33f, "Column B Extended Title");
        Cell<PDPage> hCell3 = fixedHeader.createCell(33.34f, "Column C Description");
        hCell1.setFontSize(14f);
        hCell2.setFontSize(14f);
        hCell3.setFontSize(14f);
        hCell1.setTopPadding(1f);
        hCell2.setTopPadding(1f);
        hCell3.setTopPadding(1f);
        hCell1.setBottomPadding(1f);
        hCell2.setBottomPadding(1f);
        hCell3.setBottomPadding(1f);
        styleHeaderRow(fixedHeader);
        table.addHeaderRow(fixedHeader);
        
        // Add data rows
        for (int i = 1; i <= 3; i++) {
            Row<PDPage> dataRow = table.createRow(15f);
            dataRow.createCell(33.33f, "Value A" + i);
            dataRow.createCell(33.33f, "Value B" + i);
            dataRow.createCell(33.34f, "Value C" + i);
            
            if (i % 2 == 0) {
                for (Cell<PDPage> cell : dataRow.getCells()) {
                    cell.setFillColor(new Color(236, 240, 241));
                }
            }
        }

        // Section 6: Practical Use Case
        addSectionHeader(table, "Practical Use Case: Compact Data Table");
        
        Row<PDPage> practicalDesc = table.createRow(15f);
        practicalDesc.createCell(100, 
            "Fixed heights are useful for creating compact tables with consistent row heights:");
        
        // Compact table with fixed-height rows
        Row<PDPage> compactHeader = table.createRow(12f);
        compactHeader.setFixedHeight(true);
        compactHeader.createCell(10, "ID");
        compactHeader.createCell(30, "Name");
        compactHeader.createCell(30, "Email");
        compactHeader.createCell(30, "Department");
        styleHeaderRow(compactHeader);
        for (Cell<PDPage> cell : compactHeader.getCells()) {
            cell.setFontSize(10f);
            cell.setTopPadding(1f);
            cell.setBottomPadding(1f);
        }
        
        String[][] compactData = {
            {"1", "John Smith", "john@company.com", "Engineering"},
            {"2", "Jane Doe", "jane@company.com", "Marketing"},
            {"3", "Bob Johnson", "bob@company.com", "Sales"},
            {"4", "Alice Williams", "alice@company.com", "HR"},
            {"5", "Charlie Brown", "charlie@company.com", "Finance"}
        };
        
        for (int i = 0; i < compactData.length; i++) {
            Row<PDPage> compactRow = table.createRow(10f);
            compactRow.setFixedHeight(true);
            for (int j = 0; j < 4; j++) {
                Cell<PDPage> cell = compactRow.createCell(
                    (j == 0) ? 10 : 30, 
                    compactData[i][j]
                );
                cell.setFontSize(9f);
                cell.setTopPadding(1f);
                cell.setBottomPadding(1f);
                if (i % 2 == 0) {
                    cell.setFillColor(new Color(236, 240, 241));
                }
            }
        }

        // Draw the table
        table.draw();

        // Save the document
        File file = new File("target/tutorials/Tutorial11_FixedHeightRows.pdf");
        System.out.println("Tutorial 11 PDF saved at: " + file.getAbsolutePath());
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
