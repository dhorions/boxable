package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class CustomGridLayoutTest {

    @Test
    public void customGridLayoutExample() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float margin = 40f;
        float gap = 20f;
        float pageWidth = page.getMediaBox().getWidth();
        float tableWidth = (pageWidth - (2 * margin) - gap) / 2f;
        float yStart = page.getMediaBox().getHeight() - margin;
        float yStartNewPage = yStart;
        float bottomMargin = margin;

        BaseTable descriptionTable = new BaseTable(
            yStart,
            yStartNewPage,
            bottomMargin,
            pageWidth - (2 * margin),
            margin,
            doc,
            page,
            true,
            true
        );

        Row<PDPage> descriptionRow = descriptionTable.createRow(26f);
        descriptionRow.setFixedHeight(true);
        descriptionRow.createCell(100,
            "Expected behavior: a 2x12 grid layout drawn as two side-by-side tables with matching row heights."
            + " The left column shows odd labels (T1, T3, ...), and the right column shows even labels (T2, T4, ...)."
        );

        Table.DrawResult<PDPage> descriptionResult = descriptionTable.drawAndGetPosition();
        float gridYStart = descriptionResult.getYStart() - 10f;

        BaseTable leftTable = new BaseTable(
            gridYStart,
            yStartNewPage,
            bottomMargin,
            tableWidth,
            margin,
            doc,
            page,
            true,
            true
        );

        BaseTable rightTable = new BaseTable(
            gridYStart,
            yStartNewPage,
            bottomMargin,
            tableWidth,
            margin + tableWidth + gap,
            doc,
            page,
            true,
            true
        );

        float rowHeight = 14f;
        for (int i = 1; i <= 12; i++) {
            Row<PDPage> leftRow = leftTable.createRow(rowHeight);
            leftRow.setFixedHeight(true);
            leftRow.createCell(100, "T" + (2 * i - 1));

            Row<PDPage> rightRow = rightTable.createRow(rowHeight);
            rightRow.setFixedHeight(true);
            rightRow.createCell(100, "T" + (2 * i));
        }

        leftTable.draw();
        rightTable.draw();

        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(doc);

        File file = new File("target/CustomGridLayoutTest.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        assertTrue(text.contains("Expected behavior"));
        assertTrue(text.contains("T1"));
        assertTrue(text.contains("T2"));
        assertTrue(text.contains("T23"));
        assertTrue(text.contains("T24"));
    }
}
