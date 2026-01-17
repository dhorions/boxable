package be.quodlibet.boxable;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.VerticalAlignment;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Test;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

public class HeaderRowIssueTest {

    @Test
    public void testHeaderRowNotAdded() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float margin = 50;
        float startY = page.getMediaBox().getHeight() - margin;
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        boolean drawContent = true;

        BaseTable table = new BaseTable(startY, yStartNewPage, margin, tableWidth, margin, doc, page, true, drawContent);

        // Header Row
        Row<PDPage> headerRow = table.createRow(50);
        headerRow.setHeaderRow(true); 
        Cell<PDPage> cell = headerRow.createCell(100, "HEADER");
        cell.setFontSize(12);
        cell.setValign(VerticalAlignment.MIDDLE);
        table.addHeaderRow(headerRow); // User Code

        // Data Row
        Row<PDPage> interventionAreaRow = table.createRow(50);
        Cell<PDPage> interventionAreaCell = interventionAreaRow.createCell(100, "Block");
        interventionAreaCell.setFontSize(10);
        interventionAreaCell.setTextColor(Color.darkGray);
        interventionAreaCell.setValign(VerticalAlignment.MIDDLE);

        table.draw();

        File file = new File("target/HeaderRowIssueTest.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        doc.save(file);
        doc.close();
    }
}
