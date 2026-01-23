package be.quodlibet.boxable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.Test;

import be.quodlibet.boxable.datatable.DataTable;

public class WideTableRepeatedColumnsTest {

    @Test
    public void testWideTableWithRepeatedColumns() throws IOException {
        // 1. Setup Data
        // We simulate a class list with:
        // - 3 Fixed columns (StudentId, Lastname, Firstname)
        // - 27 Dynamic columns (Days 1-27) for signatures
        // Total rows: 50 + Header
        List<List<String>> data = createDummyData(50, 27);
        List<String> headers = data.get(0);

        // 2. Setup Document
        PDDocument doc = new PDDocument();
        // Create initial landscape page
        PDPage page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth())); 
        doc.addPage(page);
        
        float margin = 20;
        float yStart = page.getMediaBox().getHeight() - margin;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float bottomMargin = 20;

        // 3. Define Column Splitting Logic
        // We will split the "dynamic" columns (index 3 to 29) into chunks that fit on a single landscape page.
        // Configuration:
        int fixedColCount = 3;         // First 3 columns are repeated on every page
        int dynamicColsPerPage = 7;    // Number of date columns to show per page
        int totalDynamicCols = headers.size() - fixedColCount;

        // 4. Iterate through chunks of dynamic columns and draw tables
        for (int i = 0; i < totalDynamicCols; i += dynamicColsPerPage) {
            
            // A. Prepare Data for this Chunk
            // Calculate the range of dynamic columns to include in this pass
            int end = Math.min(i + dynamicColsPerPage, totalDynamicCols);
            
            List<List> subTableData = new ArrayList<>();
            for (List<String> originalRow : data) {
                List<String> newRow = new ArrayList<>();
                
                // Add the Fixed Repeating Columns (Student Info)
                for (int k = 0; k < fixedColCount; k++) {
                    newRow.add(originalRow.get(k));
                }
                
                // Add the Dynamic Columns for this specific chunk (Dates)
                for (int k = i; k < end; k++) {
                    // Indices in original data are offset by fixedColCount
                    newRow.add(originalRow.get(fixedColCount + k));
                }
                subTableData.add(newRow);
            }

            // B. Prepare Page
            // If this is not the first chunk, we need a fresh page to start this horizontal section
            if (i > 0) {
                 page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
                 doc.addPage(page);
                 yStart = page.getMediaBox().getHeight() - margin;
            } else {
                 // First chunk uses the page we created at setup
            }

            // C. Draw Table
            BaseTable table = new BaseTable(yStart, yStart, bottomMargin, tableWidth, margin, doc, page, true, true);
            
            // Only draw the main title on the very first page of the set
            if (i == 0) {
                table.drawTitle("Class Attendance List - Wide Table Splitting with Repeated Columns", 
                                new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12, tableWidth, 20, "left", 0, true);
            }
            
            DataTable t = new DataTable(table, page);
            t.addListToTable(subTableData, DataTable.HASHEADER);
            table.draw();
        }

        // 5. Save Result
        File file = new File("target/ListExampleLandscape_Fixed.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();
    }

    private List<List<String>> createDummyData(int rows, int days) {
        List<List<String>> data = new ArrayList<>();
        
        // Header
        List<String> headers = new ArrayList<>();
        headers.add("StudentId");
        headers.add("Lastname");
        headers.add("Firstname");
        for (int i = 1; i <= days; i++) {
            headers.add("Day " + i);
        }
        data.add(headers);

        // Rows
        for (int i = 1; i <= rows; i++) {
            List<String> row = new ArrayList<>();
            row.add("ID-" + i);
            row.add("Lastname" + i);
            row.add("Firstname" + i);
            for (int j = 1; j <= days; j++) {
                row.add(""); // Placeholder for signature
            }
            data.add(row);
        }
        return data;
    }
}
