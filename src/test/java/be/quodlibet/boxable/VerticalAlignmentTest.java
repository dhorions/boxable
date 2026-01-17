package be.quodlibet.boxable;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Test;

public class VerticalAlignmentTest {

    @Test
    public void testVerticalAlignment() throws IOException {
        // Initialize Document
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float margin = 10;
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        boolean drawContent = true;
        float bottomMargin = 70;
        float yStart = yStartNewPage;

        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, drawContent);

        Row<PDPage> row = table.createRow(12f);
        Cell<PDPage> cell = row.createCell(22, "Nome do Paciente");
        cell.setValign(VerticalAlignment.BOTTOM);
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setFillColor(Color.LIGHT_GRAY);

        cell = row.createCell(22, "Nome da Mae");
        cell.setValign(VerticalAlignment.TOP);
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setFillColor(Color.LIGHT_GRAY);

        cell = row.createCell(8, "Nascimento");
        cell.setValign(VerticalAlignment.MIDDLE);
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setFillColor(Color.LIGHT_GRAY);

        table.draw();

        File file = new File("target/VerticalAlignmentTest.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();
    }
}
