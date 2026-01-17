package be.quodlibet.boxable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Test;

import be.quodlibet.boxable.utils.FontUtils;

public class FontUtilsConcurrencyTest {

    @Test
    public void testConcurrency() throws InterruptedException, ExecutionException {
        // 5 Threads consistently triggers the concurrency issue in https://github.com/dhorions/boxable/issues/272 before fix
        int threads = 5;
        int iterations = 10; // Each thread repeats the work multiple times
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            tasks.add(() -> {
                for (int j = 0; j < iterations; j++) {
                    try {
                        PDDocument doc = new PDDocument();
                        PDPage page = new PDPage(PDRectangle.A4);
                        doc.addPage(page);
                        
                        // This call updates the static defaultFonts map
                        FontUtils.setSansFontsAsDefault(doc);
                        
                        // Capture the font object we just set
                        // In a correct single-context implementation, this should persist for this context
                        // But in the buggy implementation, it gets overwritten by other threads
                        Object fontBefore = FontUtils.getDefaultfonts().get("font");
                        
                        // Simulate some work or delay to allow interleaving
                        Thread.sleep(50);
    
                        Object fontAfter = FontUtils.getDefaultfonts().get("font");
                        
                        // If another thread ran setSansFontsAsDefault in the meantime, fontAfter will be different
                        // This strictly proves the race condition on the shared static resource
                        if (fontBefore != fontAfter) {
                             throw new RuntimeException("Race Condition Detected: FontUtils.defaultFonts was modified by another thread!");
                        }

                        BaseTable table = new BaseTable(100, 100, 10, 500, 10, doc, page, true, true);
                        Row<PDPage> row = table.createRow(10);
                        Cell<PDPage> cell = row.createCell(100, "Test Content " + Thread.currentThread().getId());
                        cell.setFontSize(12);
                        
                        table.draw();
                        
                        doc.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw e;
                    }
                }
                return null;
            });
        }

        List<Future<Void>> results = executor.invokeAll(tasks);
        
        for (Future<Void> result : results) {
            // This will throw ExecutionException if the task failed
            result.get(); 
        }
        
        executor.shutdown();
    }
}
