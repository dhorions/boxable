package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BookmarkDestinationTest {

    @Test
    public void testBookmarkDestinationAfterPageBreak() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(new PDRectangle(200, 200));
        doc.addPage(page);

        float margin = 10f;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = 20f;

        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);

        Row<PDPage> descriptionRow = table.createRow(24f);
        descriptionRow.setFixedHeight(true);
        Cell<PDPage> descriptionCell = descriptionRow.createCell(100,
            "Expected: clicking outline 'Target Row' jumps to page 2 at the row labeled 'BOOKMARK'.");
        descriptionCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA));
        descriptionCell.setFontSize(8);

        Row<PDPage> fillerRow = table.createRow(130f);
        fillerRow.setFixedHeight(true);
        Cell<PDPage> fillerCell = fillerRow.createCell(100, "Spacer row to force a page break before the bookmark row.");
        fillerCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA));
        fillerCell.setFontSize(8);

        Row<PDPage> targetRow = table.createRow(60f);
        targetRow.setFixedHeight(true);
        Cell<PDPage> targetCell = targetRow.createCell(100, "BOOKMARK TARGET");
        targetCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        targetCell.setFontSize(12);

        PDOutlineItem outlineItem = new PDOutlineItem();
        outlineItem.setTitle("Target Row");
        targetRow.setBookmark(outlineItem);

        table.draw();

        PDDocumentOutline outline = new PDDocumentOutline();
        outline.addLast(outlineItem);
        doc.getDocumentCatalog().setDocumentOutline(outline);

        File file = new File("target/BookmarkDestinationTest.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        doc.save(file);

        assertTrue("Expected at least 2 pages after page break", doc.getNumberOfPages() >= 2);
        PDPageXYZDestination destination = (PDPageXYZDestination) outlineItem.getDestination();
        assertNotNull("Bookmark destination should be set", destination);
        assertNotNull("Bookmark destination page should be set", destination.getPage());

        PDPage lastPage = table.getCurrentPage();
        PDPage destinationPage = destination.getPage();

        int destinationIndex = -1;
        int lastIndex = -1;
        int pageIndex = 0;
        for (PDPage candidate : doc.getPages()) {
            if (candidate.getCOSObject() == destinationPage.getCOSObject()) {
                destinationIndex = pageIndex;
            }
            if (candidate.getCOSObject() == lastPage.getCOSObject()) {
                lastIndex = pageIndex;
            }
            pageIndex++;
        }

        PDFTextStripper stripper = new PDFTextStripper();
        int targetIndex = -1;
        for (int i = 1; i <= doc.getNumberOfPages(); i++) {
            stripper.setStartPage(i);
            stripper.setEndPage(i);
            String pageText = stripper.getText(doc);
            String normalizedText = pageText == null ? "" : pageText.replaceAll("\\s+", " ").trim();
            if (normalizedText.contains("BOOKMARK TARGET")) {
                targetIndex = i - 1;
                break;
            }
        }

        assertTrue("Expected to find target row text in the PDF. targetIndex=" + targetIndex
            + ", pages=" + doc.getNumberOfPages(), targetIndex >= 0);
        assertTrue("Bookmark should point to the page containing the target row. destinationIndex="
            + destinationIndex + ", targetIndex=" + targetIndex + ", lastIndex=" + lastIndex
            + ", pages=" + doc.getNumberOfPages(),
            destinationIndex == targetIndex && destinationIndex == lastIndex && destinationIndex >= 1);

        doc.close();
    }
}
