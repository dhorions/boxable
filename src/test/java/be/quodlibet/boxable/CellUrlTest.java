package be.quodlibet.boxable;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CellUrlTest {

    @Test
    public void testCellUrlCreatesClickableLink() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float margin = 30f;
        float bottomMargin = 30f;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;

        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);

        Row<PDPage> titleRow = table.createRow(20f);
        Cell<PDPage> titleCell = titleRow.createCell(100, "Cell.setUrl() - Link Annotation");
        titleCell.setFillColor(new Color(41, 128, 185));
        titleCell.setTextColor(Color.WHITE);

        Row<PDPage> descRow = table.createRow(15f);
        descRow.createCell(100,
                "Expected: The 'Open Boxable' text is a clickable link in the PDF that opens https://github.com/dhorions/boxable.");

        Row<PDPage> linkRow = table.createRow(15f);
        Cell<PDPage> linkCell = linkRow.createCell(100, "Open Boxable");
        linkCell.setTextColor(new Color(41, 128, 185));
        linkCell.setUrl(newUrl("https://github.com/dhorions/boxable"));

        table.draw();

        File file = new File("target/CellUrlTest.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();

        assertTrue("PDF file should exist", file.exists());
        assertTrue("PDF file should not be empty", file.length() > 0);
    }

    private URL newUrl(String value) throws MalformedURLException {
        return new URL(value);
    }
}
