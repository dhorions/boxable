package be.quodlibet.boxable;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.Test;

public class VariableColumnWidthTest
{

    @Test
    public void headerAndDataRowsCanUseDifferentColumnWidths() throws IOException
    {
        //Initialize Document
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);

        float margin = 10;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = 20;

        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);

        Row<PDPage> header = table.createRow(10f);
        header.createCell(60f, "Header A");
        header.createCell(40f, "Header B");
        table.addHeaderRow(header);

        Row<PDPage> data = table.createRow(10f);
        data.createCell(30f, "Data A");
        data.createCell(70f, "Data B");

        assertEquals(tableWidth * 0.6f, header.getCells().get(0).getWidth(), 0.001f);
        assertEquals(tableWidth * 0.4f, header.getCells().get(1).getWidth(), 0.001f);
        assertEquals(tableWidth * 0.3f, data.getCells().get(0).getWidth(), 0.001f);
        assertEquals(tableWidth * 0.7f, data.getCells().get(1).getWidth(), 0.001f);

        table.draw();

        File file = new File("target/VariableColumnWidthTest.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();
    }
}
