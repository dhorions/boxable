package be.quodlibet.boxable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Assert;
import org.junit.Test;

import be.quodlibet.boxable.image.Image;

public class ImagePaddingTest {

	@Test
	public void testImagePadding() throws IOException {
		// Initialize Document
		PDDocument doc = new PDDocument();
		PDPage page = new PDPage(PDRectangle.A4);
		doc.addPage(page);

		float margin = 50;
		float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
		float yStart = 800;
		float bottomMargin = 70;

		BaseTable table = new BaseTable(yStart, yStart, bottomMargin, tableWidth, margin, doc, page, true, true);
		
		
		// Create a dummy image
		int imageWidth = 200;
		int imageHeight = 100;
		BufferedImage bim = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bim.createGraphics();
		g.setColor(Color.RED);
		// Fill it red so we can see it
		g.fillRect(0, 0, imageWidth, imageHeight);
		// Draw green circle
		g.setColor(Color.GREEN);
		g.fillOval(0, 0, imageWidth, imageHeight);
		g.dispose();
		
		Image image = new Image(bim);
		
		// We want the cell to be 100 points wide.
		float targetCellWidthPoints = 100;
		float cellWidthPercent = (targetCellWidthPoints / tableWidth) * 100;
		float remainingWidthPercent = 100 - cellWidthPercent;

        // === Cell 1: Default Padding ===
        // Must use a separate Image object because ImageCell modifies the Image object's dimensions
        Row<PDPage> row = table.createRow(20);
        row.createCell(100, "Test 1: Default Padding (5px each side). Expected Image Width ~88.");
        row = table.createRow(100);
        
        Image image1 = new Image(bim);
		ImageCell<PDPage> cellWithPadding = row.createImageCell(cellWidthPercent, image1);
		cellWithPadding.setFillColor(Color.LIGHT_GRAY);
		// Filler cell to prevent background expansion on the last cell
		row.createCell(remainingWidthPercent, "");
        // Default padding (5+5=10), so expected width is 90. Minus borders (1+1) = 88.
		
        // === Cell 2: No Padding ===
        row = table.createRow(20);
        row.createCell(100, "Test 2: No Padding. Expected Image Width 100.");
        row = table.createRow(100);
        
        Image image2 = new Image(bim);
		ImageCell<PDPage> cellNoPadding = row.createImageCell(cellWidthPercent, image2);
		
		cellNoPadding.setTopPadding(0);
		cellNoPadding.setBottomPadding(0);
		cellNoPadding.setLeftPadding(0);
		cellNoPadding.setRightPadding(0);
		cellNoPadding.setBorderStyle(null);
		cellNoPadding.setFillColor(Color.CYAN);
		// Filler cell
		row.createCell(remainingWidthPercent, "");
		
        // === Cell 3: Heavy Horizontal Padding (Resizing) ===
        row = table.createRow(20);
        row.createCell(100, "Test 3: Large Horizontal Padding (25px each). Expected Image Width 50.");
        row = table.createRow(100);
        
        Image image3 = new Image(bim);
        ImageCell<PDPage> cellHeavyPadding = row.createImageCell(cellWidthPercent, image3);
        
        // 100 width. 25 left + 25 right = 50 padding.
        // Borders default 1+1 = 2.
        // Available width = 100 - 50 - 2 = 48.
        cellHeavyPadding.setLeftPadding(25);
        cellHeavyPadding.setRightPadding(25);
        // Remove vertical padding to be clean, though it doesn't affect width constraint
		cellHeavyPadding.setTopPadding(0);
		cellHeavyPadding.setBottomPadding(0);
        // Remove borders to makes math easier? No, let's keep borders to test real scenario. 
        // 1 px border is default.
        // actually let's remove borders for clean 50 width check
        cellHeavyPadding.setBorderStyle(null);
		cellHeavyPadding.setFillColor(Color.YELLOW);
		// Filler cell
		row.createCell(remainingWidthPercent, "");
		
        // Assertions for Tests 1-3
        
        // 1. Cell with padding should preserve default padding (image scaled to ~88)
        Assert.assertEquals("Image with default padding should be smaller", 88f, cellWithPadding.getImage().getWidth(), 1.0f);

        // 2. Cell without padding should be full width (100)
        Assert.assertEquals("Image without padding should be full width", 100f, cellNoPadding.getImage().getWidth(), 0.5f);
        
        // 3. Heavy padding
        // Available: 100 - 25 - 25 = 50.
        Assert.assertEquals("Image with heavy padding should be scaled to fit available width", 50f, cellHeavyPadding.getImage().getWidth(), 0.5f);
        // Original 200x100 (2:1 ratio). New width 50. Height should be 25.
        Assert.assertEquals("Image height should scale proportionally", 25f, cellHeavyPadding.getImage().getHeight(), 0.5f);
        
        // 3. Comparison
	    Assert.assertTrue("No-padding image should be wider than padding image", cellNoPadding.getImage().getWidth() > cellWithPadding.getImage().getWidth());
        
        // === Cell 4: Vertical Constraints ===
        // Row height 100. Image 200x100.
        // Horizontal padding 0.
        // Vertical padding big (e.g. 40 top + 40 bottom = 80). Available height = 20.
        row = table.createRow(20);
        row.createCell(100, "Test 4: Vertical Padding Constraints. Available Height 20. Expected Image Height 20.");
        row = table.createRow(100);
        
        Image image4 = new Image(bim);
        ImageCell<PDPage> cellVertical = row.createImageCell(cellWidthPercent, image4);
        
        cellVertical.setLeftPadding(0);
        cellVertical.setRightPadding(0);
        cellVertical.setTopPadding(40);
        cellVertical.setBottomPadding(40);
        cellVertical.setBorderStyle(null);
        cellVertical.setFillColor(Color.MAGENTA);
        // Filler cell
		row.createCell(remainingWidthPercent, "");
		
        // === Cell 5: Square Image Full Fit ===
        BufferedImage bimSquare = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gSq = bimSquare.createGraphics();
        gSq.setColor(Color.RED);
        gSq.fillRect(0, 0, 200, 200);
        gSq.setColor(Color.GREEN);
        gSq.fillOval(0, 0, 200, 200);
        gSq.dispose();

        row = table.createRow(20);
        row.createCell(100, "Test 5: Square Image (200x200) in ~100pt Cell. Should fill vertically.");
        row = table.createRow(100);
        
        Image imageSquare = new Image(bimSquare);
        ImageCell<PDPage> cellSquare = row.createImageCell(cellWidthPercent, imageSquare);
        
        cellSquare.setTopPadding(0);
        cellSquare.setBottomPadding(0);
        cellSquare.setLeftPadding(0);
        cellSquare.setRightPadding(0);
        cellSquare.setBorderStyle(null);
        cellSquare.setFillColor(Color.ORANGE);
        row.createCell(remainingWidthPercent, "");
        
        // === Cell 6: Portrait Image Vertical Constraint ===
        BufferedImage bimPortrait = new BufferedImage(100, 300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gP = bimPortrait.createGraphics();
        gP.setColor(Color.RED);
        gP.fillRect(0, 0, 100, 300);
        gP.setColor(Color.GREEN);
        gP.fillOval(0, 0, 100, 300);
        gP.dispose();
        
        row = table.createRow(20);
        row.createCell(100, "Test 6: Portrait Image (100x300) in 100pt height Row. Should scale to 100 height.");
        row = table.createRow(100);
        
        Image imagePortrait = new Image(bimPortrait);
        ImageCell<PDPage> cellPortrait = row.createImageCell(cellWidthPercent, imagePortrait);
        cellPortrait.setTopPadding(0);
        cellPortrait.setBottomPadding(0);
        cellPortrait.setLeftPadding(0);
        cellPortrait.setRightPadding(0);
        cellPortrait.setBorderStyle(null);
        cellPortrait.setFillColor(Color.PINK);
        row.createCell(remainingWidthPercent, "");
        
        table.draw();
        
        File file = new File("target/ImagePaddingTest.pdf");
        System.out.println("Saving test PDF to " + file.getAbsolutePath());
        doc.save(file);
        doc.close();
        
        System.out.println("Cell 4 (Vertical) Image Width: " + cellVertical.getImage().getWidth());
        System.out.println("Cell 4 (Vertical) Image Height: " + cellVertical.getImage().getHeight());
        
        Assert.assertEquals("Image should scale vertically to fit", 20f, cellVertical.getImage().getHeight(), 0.5f);
        Assert.assertEquals("Width should scale proportionally", 40f, cellVertical.getImage().getWidth(), 0.5f);
        
        // Test 5 Assertions
        Assert.assertEquals("Square image should match cell width", 100f, cellSquare.getImage().getWidth(), 1.0f);
        Assert.assertEquals("Square image should match cell height", 100f, cellSquare.getImage().getHeight(), 1.0f);
        
        // Test 6 Assertions
        Assert.assertEquals("Portrait image should be constrained by height", 100f, cellPortrait.getImage().getHeight(), 1.0f);
        Assert.assertEquals("Portrait image width should scale proportionally (100/3 = 33.33)", 33.33f, cellPortrait.getImage().getWidth(), 1.0f);
	}
}
