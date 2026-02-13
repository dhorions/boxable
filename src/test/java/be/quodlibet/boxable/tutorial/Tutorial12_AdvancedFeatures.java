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
 * Tutorial 12: Advanced Features
 * 
 * This tutorial demonstrates:
 * - Rotated text (90 degrees)
 * - Line spacing adjustments
 * - Colspan cells (HTML colspan attribute)
 * - Mixed advanced features
 * - Creative table layouts
 */
public class Tutorial12_AdvancedFeatures {

    @Test
    public void demonstrateAdvancedFeatures() throws IOException {
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
        Cell<PDPage> titleCell = titleRow.createCell(100, "Tutorial 12: Advanced Features");
        titleCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        titleCell.setFontSize(16f);
        titleCell.setFillColor(new Color(136, 78, 160));  // Purple background
        titleCell.setTextColor(Color.WHITE);
        titleCell.setAlign(HorizontalAlignment.CENTER);

        // Description
        Row<PDPage> descRow = table.createRow(20f);
        Cell<PDPage> descCell = descRow.createCell(100, 
            "Explore advanced Boxable features including rotated text, line spacing, and colspan.");
        descCell.setFillColor(new Color(236, 240, 241));
        descCell.setAlign(HorizontalAlignment.CENTER);

        // Section 1: Rotated Text
        addSectionHeader(table, "Rotated Text (90 Degrees)");
        
        Row<PDPage> rotDesc = table.createRow(15f);
        rotDesc.createCell(100, "Text can be rotated 90 degrees for vertical labels:");
        
        Row<PDPage> rotRow = table.createRow(80f);
        
        Cell<PDPage> rotCell1 = rotRow.createCell(10, "VERTICAL");
        rotCell1.setTextRotated(true);
        rotCell1.setAlign(HorizontalAlignment.CENTER);
        rotCell1.setFillColor(new Color(174, 214, 241));
        
        Cell<PDPage> rotCell2 = rotRow.createCell(10, "ROTATED");
        rotCell2.setTextRotated(true);
        rotCell2.setAlign(HorizontalAlignment.CENTER);
        rotCell2.setFillColor(new Color(255, 243, 224));
        
        Cell<PDPage> normalCell = rotRow.createCell(80, 
            "<b>Regular horizontal text</b><br/><br/>" +
            "The cells on the left demonstrate rotated text, which is useful for " +
            "column headers in tables where space is limited. This feature allows " +
            "you to create compact table designs.");
        normalCell.setFillColor(Color.WHITE);

        // Section 2: Colspan
        addSectionHeader(table, "Colspan - Spanning Multiple Columns");
        
        Row<PDPage> colspanDesc = table.createRow(15f);
        colspanDesc.createCell(100, "Cells can span multiple columns using HTML colspan attribute:");
        
        // Row with colspan
        Row<PDPage> colspanRow1 = table.createRow(20f);
        Cell<PDPage> spanCell = colspanRow1.createTableCell(100, 
            "<table>" +
            "<tr><td colspan='3'><b>This cell spans 3 columns</b></td></tr>" +
            "<tr><td>Column 1</td><td>Column 2</td><td>Column 3</td></tr>" +
            "<tr><td colspan='2'>Spans 2 columns</td><td>Column 3</td></tr>" +
            "</table>");
        spanCell.setFillColor(new Color(245, 245, 245));

        // Section 3: Line Spacing
        addSectionHeader(table, "Line Spacing");
        
        Row<PDPage> lineDesc = table.createRow(15f);
        lineDesc.createCell(100, "Comparing different line spacing:");
        
        Row<PDPage> lineHeader = table.createRow(15f);
        lineHeader.createCell(50, "Normal Line Spacing");
        lineHeader.createCell(50, "With Line Breaks");
        styleHeaderRow(lineHeader);
        
        Row<PDPage> lineRow = table.createRow(60f);
        
        Cell<PDPage> normalSpacing = lineRow.createCell(50, 
            "This is text with normal line spacing. " +
            "Multiple sentences are displayed with standard spacing between them. " +
            "This is the default behavior.");
        normalSpacing.setFillColor(new Color(230, 245, 255));
        
        Cell<PDPage> customSpacing = lineRow.createCell(50, 
            "First line<br/>" +
            "Second line<br/>" +
            "Third line<br/>" +
            "Fourth line<br/>" +
            "Fifth line");
        customSpacing.setFillColor(new Color(255, 250, 240));

        // Section 4: Complex Table Layout
        addSectionHeader(table, "Complex Layout Example");
        
        Row<PDPage> complexDesc = table.createRow(15f);
        complexDesc.createCell(100, "Combining multiple advanced features:");
        
        // Complex header with rotated text
        Row<PDPage> complexHeader = table.createRow(60f);
        
        Cell<PDPage> rotHeader1 = complexHeader.createCell(10, "Q1");
        rotHeader1.setTextRotated(true);
        rotHeader1.setAlign(HorizontalAlignment.CENTER);
        rotHeader1.setFillColor(new Color(52, 152, 219));
        rotHeader1.setTextColor(Color.WHITE);
        
        Cell<PDPage> rotHeader2 = complexHeader.createCell(10, "Q2");
        rotHeader2.setTextRotated(true);
        rotHeader2.setAlign(HorizontalAlignment.CENTER);
        rotHeader2.setFillColor(new Color(52, 152, 219));
        rotHeader2.setTextColor(Color.WHITE);
        
        Cell<PDPage> rotHeader3 = complexHeader.createCell(10, "Q3");
        rotHeader3.setTextRotated(true);
        rotHeader3.setAlign(HorizontalAlignment.CENTER);
        rotHeader3.setFillColor(new Color(52, 152, 219));
        rotHeader3.setTextColor(Color.WHITE);
        
        Cell<PDPage> rotHeader4 = complexHeader.createCell(10, "Q4");
        rotHeader4.setTextRotated(true);
        rotHeader4.setAlign(HorizontalAlignment.CENTER);
        rotHeader4.setFillColor(new Color(52, 152, 219));
        rotHeader4.setTextColor(Color.WHITE);
        
        Cell<PDPage> summaryHeader = complexHeader.createCell(60, 
            "<b>2024 Quarterly Sales Summary</b><br/><br/>" +
            "This table demonstrates:<br/>" +
            "• Rotated text for quarter labels<br/>" +
            "• Nested tables for data<br/>" +
            "• HTML formatting");
        summaryHeader.setFillColor(new Color(174, 214, 241));
        
        // Data row with nested table
        Row<PDPage> complexData = table.createRow(80f);
        
        Cell<PDPage> q1Data = complexData.createTableCell(10, 
            "<table>" +
            "<tr><td>Sales:</td></tr>" +
            "<tr><td><b>$100K</b></td></tr>" +
            "</table>");
        q1Data.setAlign(HorizontalAlignment.CENTER);
        q1Data.setFillColor(new Color(230, 245, 255));
        
        Cell<PDPage> q2Data = complexData.createTableCell(10, 
            "<table>" +
            "<tr><td>Sales:</td></tr>" +
            "<tr><td><b>$120K</b></td></tr>" +
            "</table>");
        q2Data.setAlign(HorizontalAlignment.CENTER);
        q2Data.setFillColor(new Color(230, 245, 255));
        
        Cell<PDPage> q3Data = complexData.createTableCell(10, 
            "<table>" +
            "<tr><td>Sales:</td></tr>" +
            "<tr><td><b>$135K</b></td></tr>" +
            "</table>");
        q3Data.setAlign(HorizontalAlignment.CENTER);
        q3Data.setFillColor(new Color(230, 245, 255));
        
        Cell<PDPage> q4Data = complexData.createTableCell(10, 
            "<table>" +
            "<tr><td>Sales:</td></tr>" +
            "<tr><td><b>$150K</b></td></tr>" +
            "</table>");
        q4Data.setAlign(HorizontalAlignment.CENTER);
        q4Data.setFillColor(new Color(230, 245, 255));
        
        Cell<PDPage> summaryData = complexData.createTableCell(60, 
            "<b>Annual Summary:</b><br/><br/>" +
            "<table>" +
            "<tr><td>Total Sales:</td><td><b>$505,000</b></td></tr>" +
            "<tr><td>Growth Rate:</td><td>+15%</td></tr>" +
            "<tr><td>Best Quarter:</td><td>Q4</td></tr>" +
            "<tr><td>Target Achievement:</td><td>102%</td></tr>" +
            "</table>");
        summaryData.setFillColor(Color.WHITE);

        // Section 5: Feature Combination Matrix
        addSectionHeader(table, "Feature Combination Matrix");
        
        Row<PDPage> matrixDesc = table.createRow(15f);
        matrixDesc.createCell(100, "A practical example combining various features:");
        
        Row<PDPage> matrixHeader = table.createRow(40f);
        matrixHeader.createCell(20, "");
        
        Cell<PDPage> mh1 = matrixHeader.createCell(20, "Feature A");
        mh1.setTextRotated(true);
        mh1.setAlign(HorizontalAlignment.CENTER);
        mh1.setFillColor(new Color(52, 73, 94));
        mh1.setTextColor(Color.WHITE);
        
        Cell<PDPage> mh2 = matrixHeader.createCell(20, "Feature B");
        mh2.setTextRotated(true);
        mh2.setAlign(HorizontalAlignment.CENTER);
        mh2.setFillColor(new Color(52, 73, 94));
        mh2.setTextColor(Color.WHITE);
        
        Cell<PDPage> mh3 = matrixHeader.createCell(20, "Feature C");
        mh3.setTextRotated(true);
        mh3.setAlign(HorizontalAlignment.CENTER);
        mh3.setFillColor(new Color(52, 73, 94));
        mh3.setTextColor(Color.WHITE);
        
        Cell<PDPage> mh4 = matrixHeader.createCell(20, "Notes");
        mh4.setAlign(HorizontalAlignment.CENTER);
        mh4.setFillColor(new Color(52, 73, 94));
        mh4.setTextColor(Color.WHITE);
        
        // Matrix data rows
        String[][] matrixData = {
            {"Product X", "Yes", "Yes", "No", "High priority"},
            {"Product Y", "Yes", "No", "Yes", "Medium priority"},
            {"Product Z", "No", "Yes", "Yes", "Low priority"}
        };
        
        for (int i = 0; i < matrixData.length; i++) {
            Row<PDPage> mRow = table.createRow(20f);
            for (int j = 0; j < 5; j++) {
                Cell<PDPage> mCell = mRow.createCell(20, matrixData[i][j]);
                if (j > 0 && j < 4) {
                    mCell.setAlign(HorizontalAlignment.CENTER);
                }
                if (i % 2 == 0) {
                    mCell.setFillColor(new Color(236, 240, 241));
                }
            }
        }

        // Draw the table
        table.draw();

        // Save the document
        File file = new File("target/tutorials/Tutorial12_AdvancedFeatures.pdf");
        System.out.println("Tutorial 12 PDF saved at: " + file.getAbsolutePath());
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
