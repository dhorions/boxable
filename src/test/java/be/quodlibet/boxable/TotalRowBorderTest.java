package be.quodlibet.boxable;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Test;

import be.quodlibet.boxable.line.LineStyle;

public class TotalRowBorderTest {

    @Test
    public void testTotalRowBorder() throws IOException {
        // Initialize Document
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        // margins
        float margin = 10;
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        boolean drawContent = true;
        float yStart = yStartNewPage;
        float bottomMargin = 70;

        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true,
                drawContent);
         LineStyle normalBorder = new LineStyle(Color.BLACK, 1.0f);    
        // 1. Header Row (All borders)
        Row<PDPage> headerRow = table.createRow(15f);
        Cell<PDPage> cell = headerRow.createCell(33, "Header 1");
        cell.setBorderStyle(normalBorder);
        cell = headerRow.createCell(33, "Header 2");
        cell.setBorderStyle(normalBorder);
        cell = headerRow.createCell(34, "Header 3");
        cell.setBorderStyle(normalBorder);
        table.addHeaderRow(headerRow);

        // 2. Data Rows (Left/Right borders only)
        for (int i = 0; i < 3; i++) {
            Row<PDPage> row = table.createRow(15f);
            
            cell = row.createCell(33, "Data " + i + "-1");
            configureDataCell(cell);
            
            cell = row.createCell(33, "Data " + i + "-2");
            configureDataCell(cell);
            
            cell = row.createCell(34, "Data " + i + "-3");
            configureDataCell(cell);
        }

        // 3. Total Row (All borders, thick top border)
        Row<PDPage> totalRow = table.createRow(20f);
        
        
       
        
        cell = totalRow.createCell(33, "Total 1");
        cell.setBorderStyle(normalBorder);
        
        cell = totalRow.createCell(33, "Total 2");
        cell.setBorderStyle(normalBorder);
        
        cell = totalRow.createCell(34, "Total 3");
        cell.setBorderStyle(normalBorder);

        table.draw();

        // Save the file
        File file = new File("target/TotalRowBorderTest.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();
    }

    private void configureDataCell(Cell<PDPage> cell) {
        // "Data rows - multiple rows where borders are set only on the left and right sides."
        cell.setTopBorderStyle(null);
        cell.setBottomBorderStyle(null);
        cell.setLeftBorderStyle(new LineStyle(Color.BLACK, 1));
        cell.setRightBorderStyle(new LineStyle(Color.BLACK, 1));
    }
}
