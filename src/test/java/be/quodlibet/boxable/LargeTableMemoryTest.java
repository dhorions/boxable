package be.quodlibet.boxable;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.Assert;
import org.junit.Test;

public class LargeTableMemoryTest {

    @Test
    public void testMemoryConsumptionWithManyRows() throws IOException {
        //this tests memory use, so garbage collect before starting
        System.gc();
        try { Thread.sleep(500); } catch (Exception e) {}
        System.gc();
        // 5000 rows test to ensure no OOM during drawing
        int rowCount = 5000; 
        int colCount = 10;
        
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);

        float margin = 10;
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float bottomMargin = 20;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        
        BaseTable table = new BaseTable(yStartNewPage, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);
        
        System.out.println("Starting generation of " + rowCount + " rows with " + colCount + " columns each.");
        long startTime = System.currentTimeMillis();
        long maxMemory = 0;
        for (int i = 0; i < rowCount; i++) {
            Row<PDPage> row = table.createRow(10f);
            for (int j = 0; j < colCount; j++) {
                row.createCell(100f / colCount, "Row " + i + " Col " + j);
            }
            if (i > 0 && i % 1000 == 0) {
                 long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                 if(usedMemory > maxMemory) {
                     maxMemory = usedMemory;
                 }
                 System.out.println("Created " + i + " rows. Used Memory: " + (usedMemory / 1024 / 1024) + " MB");
            }
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("Finished creating rows in " + (endTime - startTime) + "ms");
        
        Assert.assertTrue("Memory usage during creation (" + (maxMemory / 1024 / 1024) + " MB) exceeded limit of 50 MB", (maxMemory / 1024 / 1024) <= 50);

        // Return to baseline
        System.gc();
        try { Thread.sleep(500); } catch (Exception e) {}
        System.gc();

        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Final Used Memory: " + (usedMemory / 1024 / 1024) + " MB");
        
        System.out.println("Starting table draw...");
        long startDraw = System.currentTimeMillis();
        long memoryBeforeDraw = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        table.draw();

        long endDraw = System.currentTimeMillis();
        System.out.println("Finished drawing table in " + (endDraw - startDraw) + "ms");
        long memoryAfterDraw = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Memory used after draw before GC: " + (memoryAfterDraw / 1024 / 1024) + " MB");
        Assert.assertTrue("Memory usage after draw (" + (memoryAfterDraw / 1024 / 1024) + " MB) exceeded limit of 500 MB", (memoryAfterDraw / 1024 / 1024) <= 500);
        System.out.println("Memory increase during draw before GC: " + ((memoryAfterDraw - memoryBeforeDraw) / 1024 / 1024) + " MB");

        System.gc();
        try { Thread.sleep(500); } catch (Exception e) {}
        System.gc();

        memoryAfterDraw = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Memory used after draw: " + (memoryAfterDraw / 1024 / 1024) + " MB");
        System.out.println("Memory increase during draw: " + ((memoryAfterDraw - memoryBeforeDraw) / 1024 / 1024) + " MB");
        
        doc.save("target/LargeTableMemoryTest.pdf");
        doc.close();
    }
}