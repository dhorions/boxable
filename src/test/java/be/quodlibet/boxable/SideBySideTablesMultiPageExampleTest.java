package be.quodlibet.boxable;

import be.quodlibet.boxable.page.PageProvider;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class SideBySideTablesMultiPageExampleTest {

    // Minimal helper to record pages created by the left table.
    private static class RecordingPageProvider implements PageProvider<PDPage> {
        private final PDDocument document;
        private final PDRectangle mediaBox;
        private final List<PDPage> pages = new ArrayList<>();
        private int index = 0;

        RecordingPageProvider(PDDocument document, PDRectangle mediaBox, PDPage firstPage) {
            this.document = document;
            this.mediaBox = mediaBox;
            this.pages.add(firstPage);
        }

        @Override
        public PDPage createPage() {
            PDPage page = new PDPage(mediaBox);
            document.addPage(page);
            pages.add(page);
            index = pages.size() - 1;
            return page;
        }

        @Override
        public PDPage nextPage() {
            return createPage();
        }

        @Override
        public PDPage previousPage() {
            if (pages.isEmpty()) {
                return createPage();
            }
            if (index > 0) {
                index -= 1;
            }
            return pages.get(index);
        }

        @Override
        public PDDocument getDocument() {
            return document;
        }

        List<PDPage> getPages() {
            return pages;
        }
    }

    @Test
    public void sideBySideTablesAcrossPagesExample() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage firstPage = new PDPage(PDRectangle.A4);
        doc.addPage(firstPage);

        float margin = 40f;
        float gap = 20f;
        float leftWidth = 220f;
        float rightWidth = 220f;
        float yStartNewPage = firstPage.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = 50f;

        RecordingPageProvider pageProvider = new RecordingPageProvider(doc, firstPage.getMediaBox(), firstPage);

        BaseTable leftTable = new BaseTable(
            yStart,
            yStartNewPage,
            0,
            bottomMargin,
            leftWidth,
            margin,
            doc,
            firstPage,
            true,
            true,
            pageProvider
        );

        Row<PDPage> leftHeader = leftTable.createRow(14f);
        leftHeader.createCell(100, "Left table header");
        leftTable.addHeaderRow(leftHeader);

        for (int i = 1; i <= 40; i++) {
            Row<PDPage> row = leftTable.createRow(12f);
            row.createCell(100, "LEFT ROW " + i);
        }

        // Draw the left table first, then draw the right table on each page the left table used.
        leftTable.draw();

        float rightMargin = margin + leftWidth + gap;
        int pageIndex = 1;
        for (PDPage page : pageProvider.getPages()) {
            BaseTable rightTable = new BaseTable(
                yStart,
                yStartNewPage,
                bottomMargin,
                rightWidth,
                rightMargin,
                doc,
                page,
                true,
                true
            );

            Row<PDPage> rightHeader = rightTable.createRow(14f);
            rightHeader.createCell(100, "Right table page " + pageIndex);
            rightTable.addHeaderRow(rightHeader);

            Row<PDPage> rightRow = rightTable.createRow(12f);
            rightRow.createCell(100, "RIGHT CONTENT P" + pageIndex);

            if (pageIndex == 1) {
                Row<PDPage> expectedRow = rightTable.createRow(22f);
                expectedRow.createCell(100,
                    "Expected behavior: left table spans pages; right table is drawn per page at a fixed X offset.");
            }

            rightTable.draw();
            pageIndex++;
        }

        String text = new PDFTextStripper().getText(doc);

        File file = new File("target/SideBySideTablesMultiPageExampleTest.pdf");
        file.getParentFile().mkdirs();
        doc.save(file);
        doc.close();

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        assertTrue(text.contains("Left table header"));
        assertTrue(text.contains("LEFT ROW 1"));
        assertTrue(text.contains("Right table page 1"));
        assertTrue(text.contains("RIGHT CONTENT P1"));
    }
}
