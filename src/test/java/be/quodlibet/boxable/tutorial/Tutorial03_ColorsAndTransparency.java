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
 * Tutorial 03: Colors and Transparency
 * 
 * This tutorial demonstrates:
 * - Setting cell fill colors (background)
 * - Setting text colors
 * - Using the alpha channel for transparency
 * - Creating visual effects with color combinations
 * - RGB and RGBA color specifications
 */
public class Tutorial03_ColorsAndTransparency {

    @Test
    public void demonstrateColorsAndTransparency() throws IOException {
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
        Cell<PDPage> titleCell = titleRow.createCell(100, "Tutorial 03: Colors and Transparency");
        titleCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        titleCell.setFontSize(16f);
        titleCell.setFillColor(new Color(155, 89, 182));  // Purple background
        titleCell.setTextColor(Color.WHITE);
        titleCell.setAlign(HorizontalAlignment.CENTER);

        // Description
        Row<PDPage> descRow = table.createRow(20f);
        Cell<PDPage> descCell = descRow.createCell(100, 
            "Explore RGB colors, text colors, and alpha channel transparency effects.");
        descCell.setFillColor(new Color(236, 240, 241));
        descCell.setAlign(HorizontalAlignment.CENTER);

        // Section 1: Basic RGB Colors
        addSectionHeader(table, "Basic RGB Colors");
        
        Row<PDPage> rgbHeader = table.createRow(15f);
        rgbHeader.createCell(25, "Color Name");
        rgbHeader.createCell(25, "RGB Values");
        rgbHeader.createCell(25, "Fill Color");
        rgbHeader.createCell(25, "Text Color");
        styleHeaderRow(rgbHeader);

        // Red
        addColorRow(table, "Red", "255, 0, 0", 
                   new Color(255, 0, 0), new Color(255, 0, 0));
        
        // Green
        addColorRow(table, "Green", "0, 255, 0", 
                   new Color(0, 255, 0), new Color(0, 128, 0));
        
        // Blue
        addColorRow(table, "Blue", "0, 0, 255", 
                   new Color(0, 0, 255), new Color(0, 0, 255));
        
        // Yellow
        addColorRow(table, "Yellow", "255, 255, 0", 
                   new Color(255, 255, 0), new Color(184, 134, 11));
        
        // Cyan
        addColorRow(table, "Cyan", "0, 255, 255", 
                   new Color(0, 255, 255), new Color(0, 139, 139));
        
        // Magenta
        addColorRow(table, "Magenta", "255, 0, 255", 
                   new Color(255, 0, 255), new Color(139, 0, 139));

        // Section 2: Transparency (Alpha Channel)
        addSectionHeader(table, "Transparency with Alpha Channel");
        
        Row<PDPage> alphaHeader = table.createRow(15f);
        alphaHeader.createCell(25, "Description");
        alphaHeader.createCell(25, "Alpha Value");
        alphaHeader.createCell(50, "Visual Effect");
        styleHeaderRow(alphaHeader);

        // Demonstrate different alpha values
        addAlphaRow(table, "Opaque Red", "255 (100%)", 
                   new Color(255, 0, 0, 255));
        
        addAlphaRow(table, "Semi-Transparent Red", "180 (70%)", 
                   new Color(255, 0, 0, 180));
        
        addAlphaRow(table, "Half-Transparent Red", "128 (50%)", 
                   new Color(255, 0, 0, 128));
        
        addAlphaRow(table, "Very Transparent Red", "50 (20%)", 
                   new Color(255, 0, 0, 50));

        // Section 3: Color Combinations
        addSectionHeader(table, "Text and Background Color Combinations");
        
        Row<PDPage> comboHeader = table.createRow(15f);
        comboHeader.createCell(50, "Background Color");
        comboHeader.createCell(50, "Text Color Example");
        styleHeaderRow(comboHeader);

        addCombinationRow(table, new Color(41, 128, 185), Color.WHITE, "White on Blue");
        addCombinationRow(table, new Color(46, 204, 113), Color.BLACK, "Black on Green");
        addCombinationRow(table, new Color(241, 196, 15), Color.BLACK, "Black on Yellow");
        addCombinationRow(table, new Color(52, 73, 94), Color.WHITE, "White on Dark Gray");
        addCombinationRow(table, new Color(236, 240, 241), Color.BLACK, "Black on Light Gray");
        addCombinationRow(table, new Color(231, 76, 60), Color.WHITE, "White on Red");

        // Section 4: Gradient-like effects
        addSectionHeader(table, "Simulated Gradient Effect (Using Alpha)");
        
        Row<PDPage> gradientDesc = table.createRow(15f);
        Cell<PDPage> gradCell = gradientDesc.createCell(100, 
            "By varying alpha values, we can create gradient-like visual effects:");
        gradCell.setFillColor(Color.WHITE);

        // Create gradient effect with different alpha values
        for (int i = 0; i <= 5; i++) {
            int alpha = 255 - (i * 40);  // Decreasing opacity
            Row<PDPage> gradRow = table.createRow(12f);
            Cell<PDPage> cell = gradRow.createCell(100, 
                String.format("Level %d - Alpha: %d", i + 1, alpha));
            cell.setFillColor(new Color(52, 152, 219, alpha));
            if (alpha < 150) {
                cell.setTextColor(Color.BLACK);
            } else {
                cell.setTextColor(Color.WHITE);
            }
        }

        // Draw the table
        table.draw();

        // Save the document
        File file = new File("target/tutorials/Tutorial03_ColorsAndTransparency.pdf");
        System.out.println("Tutorial 03 PDF saved at: " + file.getAbsolutePath());
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

    /**
     * Helper method to add a color demonstration row
     */
    private void addColorRow(BaseTable table, String name, String rgb, 
                            Color fillColor, Color textColor) throws IOException {
        Row<PDPage> row = table.createRow(15f);
        row.createCell(25, name);
        row.createCell(25, rgb);
        
        Cell<PDPage> fillCell = row.createCell(25, "Sample");
        fillCell.setFillColor(fillColor);
        fillCell.setTextColor(Color.WHITE);
        fillCell.setAlign(HorizontalAlignment.CENTER);
        
        Cell<PDPage> textCell = row.createCell(25, "Sample Text");
        textCell.setTextColor(textColor);
        textCell.setAlign(HorizontalAlignment.CENTER);
    }

    /**
     * Helper method to add an alpha/transparency demonstration row
     */
    private void addAlphaRow(BaseTable table, String description, String alphaValue, 
                            Color color) throws IOException {
        Row<PDPage> row = table.createRow(15f);
        row.createCell(25, description);
        row.createCell(25, alphaValue);
        
        Cell<PDPage> visualCell = row.createCell(50, "This cell shows the transparency effect");
        visualCell.setFillColor(color);
        visualCell.setAlign(HorizontalAlignment.CENTER);
    }

    /**
     * Helper method to add a color combination row
     */
    private void addCombinationRow(BaseTable table, Color bgColor, Color textColor, 
                                  String text) throws IOException {
        Row<PDPage> row = table.createRow(15f);
        
        Cell<PDPage> bgCell = row.createCell(50, "Background Sample");
        bgCell.setFillColor(bgColor);
        bgCell.setTextColor(Color.WHITE);
        bgCell.setAlign(HorizontalAlignment.CENTER);
        
        Cell<PDPage> textCell = row.createCell(50, text);
        textCell.setFillColor(bgColor);
        textCell.setTextColor(textColor);
        textCell.setAlign(HorizontalAlignment.CENTER);
    }
}
