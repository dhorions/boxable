package be.quodlibet.boxable.tutorial;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
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

/**
 * Tutorial 06: Borders and Styling
 * 
 * This tutorial demonstrates:
 * - Border styles (solid lines with different widths)
 * - Border colors
 * - Selective borders (top, bottom, left, right)
 * - No borders (removing borders)
 * - Outer border only
 * - Custom border configurations
 */
public class Tutorial06_BordersAndStyling {

    @Test
    public void demonstrateBordersAndStyling() throws IOException {
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
        Cell<PDPage> titleCell = titleRow.createCell(100, "Tutorial 06: Borders and Styling");
        titleCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        titleCell.setFontSize(16f);
        titleCell.setFillColor(new Color(192, 57, 43));  // Dark red background
        titleCell.setTextColor(Color.WHITE);
        titleCell.setAlign(HorizontalAlignment.CENTER);

        // Description
        Row<PDPage> descRow = table.createRow(20f);
        Cell<PDPage> descCell = descRow.createCell(100, 
            "Learn how to customize cell borders, including colors, widths, and selective borders.");
        descCell.setFillColor(new Color(236, 240, 241));
        descCell.setAlign(HorizontalAlignment.CENTER);

        // Section 1: Default Borders
        addSectionHeader(table, "Default Borders");
        
        Row<PDPage> defaultDesc = table.createRow(15f);
        defaultDesc.createCell(100, "By default, all cells have 1pt black borders:");
        
        Row<PDPage> defaultRow = table.createRow(20f);
        defaultRow.createCell(33.33f, "Cell 1");
        defaultRow.createCell(33.33f, "Cell 2");
        defaultRow.createCell(33.34f, "Cell 3");

        // Section 2: Border Width Variations
        addSectionHeader(table, "Border Width Variations");
        
        Row<PDPage> widthHeader = table.createRow(15f);
        widthHeader.createCell(33.33f, "Thin (0.5pt)");
        widthHeader.createCell(33.33f, "Normal (1pt)");
        widthHeader.createCell(33.34f, "Thick (3pt)");
        styleHeaderRow(widthHeader);
        
        Row<PDPage> widthRow = table.createRow(20f);
        
        // Thin border
        Cell<PDPage> thinCell = widthRow.createCell(33.33f, "Thin Border");
        thinCell.setBorderStyle(new LineStyle(Color.BLACK, 0.5f));
        thinCell.setAlign(HorizontalAlignment.CENTER);
        
        // Normal border
        Cell<PDPage> normalCell = widthRow.createCell(33.33f, "Normal Border");
        normalCell.setBorderStyle(new LineStyle(Color.BLACK, 1f));
        normalCell.setAlign(HorizontalAlignment.CENTER);
        
        // Thick border
        Cell<PDPage> thickCell = widthRow.createCell(33.34f, "Thick Border");
        thickCell.setBorderStyle(new LineStyle(Color.BLACK, 3f));
        thickCell.setAlign(HorizontalAlignment.CENTER);

        // Section 3: Border Colors
        addSectionHeader(table, "Border Colors");
        
        Row<PDPage> colorHeader = table.createRow(15f);
        colorHeader.createCell(25, "Red");
        colorHeader.createCell(25, "Blue");
        colorHeader.createCell(25, "Green");
        colorHeader.createCell(25, "Purple");
        styleHeaderRow(colorHeader);
        
        Row<PDPage> colorRow = table.createRow(20f);
        
        Cell<PDPage> redBorder = colorRow.createCell(25, "Red Border");
        redBorder.setBorderStyle(new LineStyle(Color.RED, 2f));
        redBorder.setAlign(HorizontalAlignment.CENTER);
        
        Cell<PDPage> blueBorder = colorRow.createCell(25, "Blue Border");
        blueBorder.setBorderStyle(new LineStyle(Color.BLUE, 2f));
        blueBorder.setAlign(HorizontalAlignment.CENTER);
        
        Cell<PDPage> greenBorder = colorRow.createCell(25, "Green Border");
        greenBorder.setBorderStyle(new LineStyle(new Color(46, 204, 113), 2f));
        greenBorder.setAlign(HorizontalAlignment.CENTER);
        
        Cell<PDPage> purpleBorder = colorRow.createCell(25, "Purple Border");
        purpleBorder.setBorderStyle(new LineStyle(new Color(155, 89, 182), 2f));
        purpleBorder.setAlign(HorizontalAlignment.CENTER);

        // Section 4: Selective Borders
        addSectionHeader(table, "Selective Borders");
        
        Row<PDPage> selectDesc = table.createRow(15f);
        selectDesc.createCell(100, "Control which sides have borders:");
        
        Row<PDPage> selectHeader = table.createRow(15f);
        selectHeader.createCell(25, "Top Only");
        selectHeader.createCell(25, "Bottom Only");
        selectHeader.createCell(25, "Left Only");
        selectHeader.createCell(25, "Right Only");
        styleHeaderRow(selectHeader);
        
        Row<PDPage> selectRow = table.createRow(30f);
        
        Cell<PDPage> topOnly = selectRow.createCell(25, "Top Border Only");
        topOnly.setTopBorderStyle(new LineStyle(Color.BLACK, 2f));
        topOnly.setBottomBorderStyle(null);
        topOnly.setLeftBorderStyle(null);
        topOnly.setRightBorderStyle(null);
        topOnly.setAlign(HorizontalAlignment.CENTER);
        
        Cell<PDPage> bottomOnly = selectRow.createCell(25, "Bottom Border Only");
        bottomOnly.setTopBorderStyle(null);
        bottomOnly.setBottomBorderStyle(new LineStyle(Color.BLACK, 2f));
        bottomOnly.setLeftBorderStyle(null);
        bottomOnly.setRightBorderStyle(null);
        bottomOnly.setAlign(HorizontalAlignment.CENTER);
        
        Cell<PDPage> leftOnly = selectRow.createCell(25, "Left Border Only");
        leftOnly.setTopBorderStyle(null);
        leftOnly.setBottomBorderStyle(null);
        leftOnly.setLeftBorderStyle(new LineStyle(Color.BLACK, 2f));
        leftOnly.setRightBorderStyle(null);
        leftOnly.setAlign(HorizontalAlignment.CENTER);
        
        Cell<PDPage> rightOnly = selectRow.createCell(25, "Right Border Only");
        rightOnly.setTopBorderStyle(null);
        rightOnly.setBottomBorderStyle(null);
        rightOnly.setLeftBorderStyle(null);
        rightOnly.setRightBorderStyle(new LineStyle(Color.BLACK, 2f));
        rightOnly.setAlign(HorizontalAlignment.CENTER);

        // Section 5: No Borders
        addSectionHeader(table, "No Borders");
        
        Row<PDPage> noBorderDesc = table.createRow(15f);
        noBorderDesc.createCell(100, "Cells can be created without any borders:");
        
        Row<PDPage> noBorderRow = table.createRow(20f);
        
        Cell<PDPage> noBorder1 = noBorderRow.createCell(33.33f, "No Border");
        noBorder1.setBorderStyle(null);
        noBorder1.setFillColor(new Color(174, 214, 241));
        noBorder1.setAlign(HorizontalAlignment.CENTER);
        
        Cell<PDPage> noBorder2 = noBorderRow.createCell(33.33f, "No Border");
        noBorder2.setBorderStyle(null);
        noBorder2.setFillColor(new Color(255, 243, 224));
        noBorder2.setAlign(HorizontalAlignment.CENTER);
        
        Cell<PDPage> noBorder3 = noBorderRow.createCell(33.34f, "No Border");
        noBorder3.setBorderStyle(null);
        noBorder3.setFillColor(new Color(255, 235, 238));
        noBorder3.setAlign(HorizontalAlignment.CENTER);

        // Section 6: Combined Styling
        addSectionHeader(table, "Combined Styling Example");
        
        Row<PDPage> comboDesc = table.createRow(15f);
        comboDesc.createCell(100, "Combining borders with colors for visual effects:");
        
        Row<PDPage> comboRow = table.createRow(25f);
        
        Cell<PDPage> combo1 = comboRow.createCell(50, "<b>Important:</b> Highlighted Cell");
        combo1.setFillColor(new Color(255, 243, 224));
        combo1.setBorderStyle(new LineStyle(new Color(243, 156, 18), 3f));
        
        Cell<PDPage> combo2 = comboRow.createCell(50, "<b>Warning:</b> Alert Cell");
        combo2.setFillColor(new Color(255, 235, 238));
        combo2.setBorderStyle(new LineStyle(new Color(231, 76, 60), 3f));

        // Draw the table
        table.draw();

        // Create a second table demonstrating outer border only
        if (table.getCurrentPage() != page) {
            page = table.getCurrentPage();
        }
        // Create new page for the second table
        page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        float newYStart = yStartNewPage;
        
        BaseTable table2 = new BaseTable(newYStart, yStartNewPage, bottomMargin, 
                                         tableWidth, margin, document, page, 
                                         false, true);  // drawLines = false
        table2.setOuterBorderStyle(new LineStyle(Color.BLACK, 2f));
        
        Row<PDPage> outerTitle = table2.createRow(20f);
        Cell<PDPage> outerTitleCell = outerTitle.createCell(100, "Outer Border Only Example");
        outerTitleCell.setFillColor(new Color(52, 73, 94));
        outerTitleCell.setTextColor(Color.WHITE);
        outerTitleCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        outerTitleCell.setAlign(HorizontalAlignment.CENTER);
        
        Row<PDPage> outerRow1 = table2.createRow(15f);
        outerRow1.createCell(33.33f, "Cell 1");
        outerRow1.createCell(33.33f, "Cell 2");
        outerRow1.createCell(33.34f, "Cell 3");
        
        Row<PDPage> outerRow2 = table2.createRow(15f);
        outerRow2.createCell(33.33f, "Cell 4");
        outerRow2.createCell(33.33f, "Cell 5");
        outerRow2.createCell(33.34f, "Cell 6");
        
        table2.draw();

        // Save the document
        File file = new File("target/tutorials/Tutorial06_BordersAndStyling.pdf");
        System.out.println("Tutorial 06 PDF saved at: " + file.getAbsolutePath());
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
