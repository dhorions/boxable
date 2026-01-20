package be.quodlibet.boxable;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.Test;

public class InnerTableGreaterThanEntityTest
{

    @Test
    public void innerTableHtmlEntityForGreaterThanIsRenderedAsSymbol() throws IOException
    {
        // Initialize Document
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);

        float margin = 10;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = 20;

        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);

        Row<PDPage> row = table.createRow(10f);
        row.createTableCell(100f,
            "<table>"
                + "<tr><th>Character</th><th>Rendered text</th></tr>"
                + "<tr><td>Greater-than</td><td>Composers->Schubert->String</td></tr>"
                + "<tr><td>Smaller-than </td><td>Composers<-Schubert<-String</td></tr>"
                + "<tr><td>Question mark</td><td>Where? When? Why?</td></tr>"
                + "<tr><td>At sign </td><td>user@example.com</td></tr>"
                + "<tr><td>Hash </td><td>Issue#123</td></tr>"
                + "<tr><td>Backslash </td><td>\\</td></tr>"
                + "</table>",
            doc, page, yStart, bottomMargin, margin);

        table.draw();

        File file = new File("target/InnerTableGreaterThanEntityTest.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();
    }
}
