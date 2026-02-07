package be.quodlibet.boxable.tutorial;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.ImageCell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.image.Image;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.Test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Tutorial 05: Images in Cells
 * 
 * This tutorial demonstrates:
 * - Adding images to table cells
 * - Image scaling to fit cells
 * - Image padding control
 * - Multiple images in a table
 * - Different image sizes and aspect ratios
 */
public class Tutorial05_Images {

    @Test
    public void demonstrateImages() throws IOException {
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
        Cell<PDPage> titleCell = titleRow.createCell(100, "Tutorial 05: Images in Cells");
        titleCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        titleCell.setFontSize(16f);
        titleCell.setFillColor(new Color(26, 188, 156));  // Teal background
        titleCell.setTextColor(Color.WHITE);
        titleCell.setAlign(HorizontalAlignment.CENTER);

        // Description
        Row<PDPage> descRow = table.createRow(20f);
        Cell<PDPage> descCell = descRow.createCell(100, 
            "Learn how to add images to cells, control scaling, and adjust padding.");
        descCell.setFillColor(new Color(236, 240, 241));
        descCell.setAlign(HorizontalAlignment.CENTER);

        // Section 1: Basic Image in Cell
        addSectionHeader(table, "Basic Image in Cell");
        
        Row<PDPage> basicDesc = table.createRow(15f);
        basicDesc.createCell(100, "A simple image in a cell with default settings:");
        
        // Create a sample image (red background with green circle)
        BufferedImage redGreenImage = createSampleImage(200, 100, Color.RED, Color.GREEN);
        Image image1 = new Image(redGreenImage);
        
        Row<PDPage> imageRow1 = table.createRow(100f);
        ImageCell<PDPage> imageCell1 = imageRow1.createImageCell(100, image1);
        imageCell1.setAlign(HorizontalAlignment.CENTER);

        // Section 2: Image Scaling
        addSectionHeader(table, "Image Scaling");
        
        Row<PDPage> scalingHeader = table.createRow(15f);
        scalingHeader.createCell(33.33f, "Small Cell");
        scalingHeader.createCell(33.33f, "Medium Cell");
        scalingHeader.createCell(33.34f, "Large Cell");
        styleHeaderRow(scalingHeader);
        
        Row<PDPage> scalingRow = table.createRow(80f);
        
        // Small cell with image
        BufferedImage blueImage = createSampleImage(200, 100, Color.BLUE, Color.YELLOW);
        Image image2 = new Image(blueImage);
        ImageCell<PDPage> smallCell = scalingRow.createImageCell(33.33f, image2);
        smallCell.setAlign(HorizontalAlignment.CENTER);
        
        // Medium cell with image
        BufferedImage orangeImage = createSampleImage(200, 100, Color.ORANGE, Color.CYAN);
        Image image3 = new Image(orangeImage);
        ImageCell<PDPage> mediumCell = scalingRow.createImageCell(33.33f, image3);
        mediumCell.setAlign(HorizontalAlignment.CENTER);
        
        // Large cell with image
        BufferedImage purpleImage = createSampleImage(200, 100, new Color(155, 89, 182), Color.WHITE);
        Image image4 = new Image(purpleImage);
        ImageCell<PDPage> largeCell = scalingRow.createImageCell(33.34f, image4);
        largeCell.setAlign(HorizontalAlignment.CENTER);

        // Section 3: Image Padding Control
        addSectionHeader(table, "Image Padding Control");
        
        Row<PDPage> paddingDesc = table.createRow(15f);
        paddingDesc.createCell(100, "Control the padding around images:");
        
        Row<PDPage> paddingHeader = table.createRow(15f);
        paddingHeader.createCell(33.33f, "Default Padding");
        paddingHeader.createCell(33.33f, "No Padding");
        paddingHeader.createCell(33.34f, "Custom Padding");
        styleHeaderRow(paddingHeader);
        
        Row<PDPage> paddingRow = table.createRow(80f);
        
        // Default padding
        BufferedImage greenImage = createSampleImage(200, 100, new Color(46, 204, 113), Color.BLACK);
        Image image5 = new Image(greenImage);
        ImageCell<PDPage> defaultPadCell = paddingRow.createImageCell(33.33f, image5);
        defaultPadCell.setFillColor(new Color(236, 240, 241));
        
        // No padding
        BufferedImage yellowImage = createSampleImage(200, 100, new Color(241, 196, 15), Color.BLACK);
        Image image6 = new Image(yellowImage);
        ImageCell<PDPage> noPadCell = paddingRow.createImageCell(33.33f, image6);
        noPadCell.setTopPadding(0);
        noPadCell.setBottomPadding(0);
        noPadCell.setLeftPadding(0);
        noPadCell.setRightPadding(0);
        noPadCell.setFillColor(new Color(236, 240, 241));
        
        // Custom padding (20pt)
        BufferedImage cyanImage = createSampleImage(200, 100, new Color(52, 152, 219), Color.WHITE);
        Image image7 = new Image(cyanImage);
        ImageCell<PDPage> customPadCell = paddingRow.createImageCell(33.34f, image7);
        customPadCell.setTopPadding(20);
        customPadCell.setBottomPadding(20);
        customPadCell.setLeftPadding(20);
        customPadCell.setRightPadding(20);
        customPadCell.setFillColor(new Color(236, 240, 241));

        // Section 4: Different Image Aspect Ratios
        addSectionHeader(table, "Different Image Aspect Ratios");
        
        Row<PDPage> aspectHeader = table.createRow(15f);
        aspectHeader.createCell(33.33f, "Landscape (2:1)");
        aspectHeader.createCell(33.33f, "Square (1:1)");
        aspectHeader.createCell(33.34f, "Portrait (1:2)");
        styleHeaderRow(aspectHeader);
        
        Row<PDPage> aspectRow = table.createRow(100f);
        
        // Landscape image
        BufferedImage landscapeImage = createSampleImage(200, 100, new Color(231, 76, 60), Color.WHITE);
        Image image8 = new Image(landscapeImage);
        ImageCell<PDPage> landscapeCell = aspectRow.createImageCell(33.33f, image8);
        landscapeCell.setAlign(HorizontalAlignment.CENTER);
        
        // Square image
        BufferedImage squareImage = createSampleImage(150, 150, new Color(52, 73, 94), Color.WHITE);
        Image image9 = new Image(squareImage);
        ImageCell<PDPage> squareCell = aspectRow.createImageCell(33.33f, image9);
        squareCell.setAlign(HorizontalAlignment.CENTER);
        
        // Portrait image
        BufferedImage portraitImage = createSampleImage(100, 200, new Color(155, 89, 182), Color.WHITE);
        Image image10 = new Image(portraitImage);
        ImageCell<PDPage> portraitCell = aspectRow.createImageCell(33.34f, image10);
        portraitCell.setAlign(HorizontalAlignment.CENTER);

        // Section 5: Images with Text
        addSectionHeader(table, "Images Combined with Text");
        
        Row<PDPage> combinedRow = table.createRow(80f);
        
        // Image cell
        BufferedImage finalImage = createSampleImage(200, 100, new Color(41, 128, 185), Color.WHITE);
        Image image11 = new Image(finalImage);
        ImageCell<PDPage> imgCell = combinedRow.createImageCell(40f, image11);
        imgCell.setAlign(HorizontalAlignment.CENTER);
        
        // Text description cell
        Cell<PDPage> textCell = combinedRow.createCell(60f, 
            "<b>Product Name:</b> Sample Widget<br/>" +
            "<b>Description:</b> This is a high-quality product with excellent features.<br/>" +
            "<b>Price:</b> $99.99<br/>" +
            "<b>Stock:</b> In Stock");
        textCell.setAlign(HorizontalAlignment.LEFT);

        // Draw the table
        table.draw();

        // Save the document
        File file = new File("target/tutorials/Tutorial05_Images.pdf");
        System.out.println("Tutorial 05 PDF saved at: " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        document.save(file);
        document.close();
    }

    /**
     * Helper method to create a sample image with colored background and shape
     */
    private BufferedImage createSampleImage(int width, int height, Color bgColor, Color shapeColor) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        
        // Fill background
        g.setColor(bgColor);
        g.fillRect(0, 0, width, height);
        
        // Draw a circle or oval
        g.setColor(shapeColor);
        int size = Math.min(width, height);
        int x = (width - size) / 2;
        int y = (height - size) / 2;
        g.fillOval(x, y, size, size);
        
        g.dispose();
        return image;
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
