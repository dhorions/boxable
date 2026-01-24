package be.quodlibet.boxable;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Assert;
import org.junit.Test;

import be.quodlibet.boxable.image.Image;

public class AbsoluteCellWidthTest {

    @Test
    public void testAbsoluteWidths() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        // Table width = 500
        float tableWidth = 500f;
        float margin = 10f;
        float yStart = 800f;

        BaseTable table = new BaseTable(yStart, yStart, 10f, tableWidth, margin, doc, page, true, true);

        Row<PDPage> row = table.createRow(20f);
        
        // Create a cell with 50% width (should be 250 calculated)
        // Existing method: defaults to isPercent=true (isCalculated=true)
        Cell<PDPage> percentCell = row.createCell(50, "50% Cell");
        Assert.assertEquals(250f, percentCell.getWidth(), 0.01f);

        // Create a cell with 100pt absolute width
        // New method: createCell(width, text, align, valign, isPercent)
        // Here we want isPercent = false
        // Note: For convenience we might want a simpler overload createCell(width, text, isPercent)
        Cell<PDPage> absCell = row.createCell(100f, "100pt Absolute", HorizontalAlignment.LEFT, VerticalAlignment.TOP, false);
        Assert.assertEquals(100f, absCell.getWidth(), 0.01f);
        
        // Verify another absolute cell
        Cell<PDPage> absCell2 = row.createCell(75f, "75pt Absolute", HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE, false);
        Assert.assertEquals(75f, absCell2.getWidth(), 0.01f);

        table.draw();
        
        File file = new File("target/AbsoluteCellWidthTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();
    }
    
    @Test
    public void testImageCellAbsoluteWidth() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float tableWidth = 500f;
        float yStart = 800f;
        float margin = 10f;

        BaseTable table = new BaseTable(yStart, yStart, 10f, tableWidth, margin, doc, page, true, true);
        
        // Load image
        BufferedImage bim = ImageIO.read(new File("src/test/resources/150dpi.png"));
        
        Row<PDPage> row = table.createRow(100f);
        
        // 1. Percentage Image Cell (20% of 500 = 100pt)
        // Note: Creating new Image object for each cell because ImageCell modifies the Image dimensions
        Image img1 = new Image(bim);
        ImageCell<PDPage> percentCell = row.createImageCell(20, img1);
        Assert.assertEquals(100f, percentCell.getWidth(), 0.01f);
        
        // 2. Absolute Image Cell (75pt)
        Image img2 = new Image(bim);
        ImageCell<PDPage> absCell = row.createImageCell(75f, img2, false);
        Assert.assertEquals(75f, absCell.getWidth(), 0.01f);
        
        table.draw();
        
        File file = new File("target/AbsoluteImageCellWidthTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();
    }

    @Test
    public void testTableCellAbsoluteWidth() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float tableWidth = 500f;
        float yStart = 800f;
        float margin = 10f;

        BaseTable table = new BaseTable(yStart, yStart, 10f, tableWidth, margin, doc, page, true, true);
        
        Row<PDPage> row = table.createRow(50f);
        
        // 1. Percentage Table Cell (30% of 500 = 150pt)
        String html1 = "<table><tr><td>Percent Table 30%</td></tr></table>";
        TableCell<PDPage> percentCell = row.createTableCell(30, html1, doc, page, yStart, 10, 10);
        Assert.assertEquals(150f, percentCell.getWidth(), 0.01f);
        
        // 2. Absolute Table Cell (25pt)
        String html2 = "<table><tr><td>Absolute Table 25pt</td></tr></table>";
        TableCell<PDPage> absCell = row.createTableCell(25f, html2, doc, page, yStart, 10, 10, false);
        Assert.assertEquals(25f, absCell.getWidth(), 0.01f);
        
        table.draw();
        
        File file = new File("target/AbsoluteTableCellWidthTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();
    }

    @Test
    public void testTableCellAbsoluteWidthStress() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float tableWidth = 500f;
        float yStart = 800f;
        float margin = 10f;

        BaseTable table = new BaseTable(yStart, yStart, 10f, tableWidth, margin, doc, page, true, true);
        
        // --- Row 1: Mixed large absolute and percent ---
        Row<PDPage> row1 = table.createRow(50f);
        
        // 30% of 500 = 150pt
        String html1 = "<table><tr><td>Row 1 - Cell 1 (30%)</td></tr></table>";
        row1.createTableCell(30, html1, doc, page, yStart, 5, 5);
        
        // 100pt absolute
        String html2 = "<table><tr><td>Row 1 - Cell 2 (100pt)</td></tr></table>";
        row1.createTableCell(100f, html2, doc, page, yStart, 5, 5, false);
        
        // Remainder percent (e.g. 50%)
        String html3 = "<table><tr><td>Row 1 - Cell 3 (50%)</td></tr></table>";
        row1.createTableCell(50, html3, doc, page, yStart, 5, 5);


        // --- Row 2: Small absolute widths (Stress test) ---
        Row<PDPage> row2 = table.createRow(50f);
        
        // 25pt absolute - Very small
        // Reduce padding for inner table cell using TableCell config to ensure it fits nicely
        String htmlSmall = "<table><tr><td style=\"font-size: 6px\">25pt</td></tr></table>";
        TableCell<PDPage> cell25 = row2.createTableCell(25f, htmlSmall, doc, page, yStart, 2, 2, false);
        cell25.setInnerTableCellPadding(1f, 1f, 1f, 1f); 
        
        // 30pt absolute
        String htmlSmall2 = "<table><tr><td style=\"font-size: 6px\">30pt</td></tr></table>";
        row2.createTableCell(30f, htmlSmall2, doc, page, yStart, 2, 2, false);
        
        // Fill rest with percent (500 - 25 - 30 = 445. 445/500 = 89%)
        String htmlRest = "<table><tr><td>Rest of Row 2 (89% fill)</td></tr></table>";
        row2.createTableCell(89, htmlRest, doc, page, yStart, 5, 5);


        // --- Row 3: All Absolute ---
        Row<PDPage> row3 = table.createRow(50f);
        row3.createTableCell(100f, "<table><tr><td>100pt</td></tr></table>", doc, page, yStart, 5, 5, false);
        row3.createTableCell(100f, "<table><tr><td>100pt</td></tr></table>", doc, page, yStart, 5, 5, false);
        row3.createTableCell(100f, "<table><tr><td>100pt</td></tr></table>", doc, page, yStart, 5, 5, false);
        row3.createTableCell(200f, "<table><tr><td>200pt (Total 500)</td></tr></table>", doc, page, yStart, 5, 5, false);

        
        table.draw();
        
        File file = new File("target/AbsoluteTableCellStressTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();
    }

    @Test
    public void testCellAbsoluteWidthStress() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float tableWidth = 500f;
        float yStart = 800f;
        float margin = 10f;

        BaseTable table = new BaseTable(yStart, yStart, 10f, tableWidth, margin, doc, page, true, true);
        
        // --- Row 1: Mixed large absolute and percent ---
        Row<PDPage> row1 = table.createRow(20f);
        
        // 30% of 500 = 150pt
        row1.createCell(30, "Row 1 - Cell 1 (30%)");
        
        // 100pt absolute
        row1.createCell(100f, "Row 1 - Cell 2 (100pt)", HorizontalAlignment.LEFT, VerticalAlignment.TOP, false);
        
        // Remainder percent (50%)
        row1.createCell(50, "Row 1 - Cell 3 (50%)");


        // --- Row 2: Small absolute widths (Stress test) ---
        Row<PDPage> row2 = table.createRow(20f);
        
        // 25pt absolute - Very small
        Cell<PDPage> cell25 = row2.createCell(25f, "25pt", HorizontalAlignment.LEFT, VerticalAlignment.TOP, false);
        cell25.setFontSize(6);

        // 30pt absolute
        Cell<PDPage> cell30 = row2.createCell(30f, "30pt", HorizontalAlignment.LEFT, VerticalAlignment.TOP, false);
        cell30.setFontSize(6);

        // Fill rest with percent (500 - 25 - 30 = 445. 445/500 = 89%)
        row2.createCell(89, "Rest of Row 2 (89% fill)");


        // --- Row 3: All Absolute ---
        Row<PDPage> row3 = table.createRow(20f);
        row3.createCell(100f, "100pt", HorizontalAlignment.LEFT, VerticalAlignment.TOP, false);
        row3.createCell(100f, "100pt", HorizontalAlignment.LEFT, VerticalAlignment.TOP, false);
        row3.createCell(100f, "100pt", HorizontalAlignment.LEFT, VerticalAlignment.TOP, false);
        row3.createCell(200f, "200pt (Total 500)", HorizontalAlignment.LEFT, VerticalAlignment.TOP, false);

        
        table.draw();
        
        File file = new File("target/AbsoluteCellStressTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();
    }

    @Test
    public void testImageCellAbsoluteWidthStress() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float tableWidth = 500f;
        float yStart = 800f;
        float margin = 10f;

        BaseTable table = new BaseTable(yStart, yStart, 10f, tableWidth, margin, doc, page, true, true);
        
        // Load image
        BufferedImage bim = ImageIO.read(new File("src/test/resources/300dpi.png"));

        // --- Row 1: Mixed large absolute and percent ---
        Row<PDPage> row1 = table.createRow(50f);
        
        // 30% of 500 = 150pt
        row1.createImageCell(30, new Image(bim));
        
        // 100pt absolute
        row1.createImageCell(100f, new Image(bim), false);
        
        // Remainder percent (50%)
        row1.createImageCell(50, new Image(bim));


        // --- Row 2: Small absolute widths (Stress test) ---
        Row<PDPage> row2 = table.createRow(50f);
        
        // 25pt absolute - Very small
        row2.createImageCell(25f, new Image(bim), false);

        // 30pt absolute
        row2.createImageCell(30f, new Image(bim), false);

        // Fill rest with percent (500 - 25 - 30 = 445. 445/500 = 89%)
        row2.createImageCell(89, new Image(bim));


        // --- Row 3: All Absolute ---
        Row<PDPage> row3 = table.createRow(50f);
        row3.createImageCell(100f, new Image(bim), false);
        row3.createImageCell(100f, new Image(bim), false);
        row3.createImageCell(100f, new Image(bim), false);
        row3.createImageCell(200f, new Image(bim), false);

        
        table.draw();
        
        File file = new File("target/AbsoluteImageCellStressTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();
    }
}
