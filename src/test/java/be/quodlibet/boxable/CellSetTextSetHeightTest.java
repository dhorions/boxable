package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;

public class CellSetTextSetHeightTest {

    @Test
    public void setTextAndSetHeightShouldRenderText() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float margin = 50f;
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = 70f;
        boolean drawContent = true;

        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true,
                drawContent);

        Row<PDPage> row = table.createRow(20f);
        Cell<PDPage> cell = row.createCell(100, "Text");
        cell.setText("If the test is successfull, you should see this text here");
        cell.setHeight(10f);

        Row<PDPage> expectedRow = table.createRow(22f);
        expectedRow.createCell(100,
                "Expected behavior: the cell that uses setText() and setHeight() still renders its text and does not appear empty.");

        table.draw();

        String text = new PDFTextStripper().getText(doc);

        File file = new File("target/CellSetTextSetHeightTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();

        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        assertTrue(text.contains("If the test is successfull, you should see this text here"));
    }

    @Test
    public void setTextAndSetHeightWithFixedRowShouldRenderText() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        float margin = 50f;
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = 70f;
        boolean drawContent = true;

        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true,
                drawContent);

        Row<PDPage> row = table.createRow(10f);
        row.setFixedHeight(true);
        Cell<PDPage> cell = row.createCell(100, "Text");
        cell.setFontSize(12f);
        cell.setTopPadding(1f);
        cell.setBottomPadding(1f);
        cell.setText("If the test is successfull, you should see this text here");
        cell.setHeight(10f);

        Row<PDPage> expectedRow = table.createRow(22f);
        expectedRow.createCell(100,
                "Expected behavior: the fixed-height row shrinks text and still renders it when using setText() and setHeight().");

        table.draw();

        String text = new PDFTextStripper().getText(doc);

        File file = new File("target/CellSetTextSetHeightFixedRowTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();

        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        assertTrue(text.contains("If the test is successfull, you should see this text here"));
    }

        @Test
        public void negativeInnerHeightLogsWarning() {
                System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");

                PrintStream originalErr = System.err;
                ByteArrayOutputStream errStream = new ByteArrayOutputStream();
                System.setErr(new PrintStream(errStream));

                try {
                        PDDocument doc = new PDDocument();
                        PDPage page = new PDPage(PDRectangle.A4);
                        doc.addPage(page);

                        float margin = 50f;
                        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
                        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
                        float yStart = yStartNewPage;
                        float bottomMargin = 70f;

                        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true,
                                        true);

                        Row<PDPage> row = table.createRow(10f);
                        row.setFixedHeight(true);
                        Cell<PDPage> cell = row.createCell(100, "Text");

                        float innerHeight = cell.getInnerHeight();
                        assertTrue(innerHeight < 0);

                        doc.close();
                } catch (IOException e) {
                        throw new RuntimeException(e);
                } finally {
                        System.setErr(originalErr);
                }

                String logs = errStream.toString();
                assertTrue(logs.contains("Cell inner height is negative"));
        }
}
