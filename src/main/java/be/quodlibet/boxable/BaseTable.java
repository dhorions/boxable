package be.quodlibet.boxable;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import be.quodlibet.boxable.page.DefaultPageProvider;
import be.quodlibet.boxable.page.PageProvider;

/**
 * Base implementation of a table for PDF documents using standard PDPage.
 * Provides a straightforward table implementation without custom page templates.
 */
public class BaseTable extends Table<PDPage> {

    /**
     * Constructs a basic table with standard PDPage and no top margin.
     *
     * @param yStart the initial Y coordinate for table content
     * @param yStartNewPage the Y coordinate to use for new pages
     * @param bottomMargin the bottom margin of the page in points
     * @param width the width of the table in points
     * @param margin the left and right margin of the table in points
     * @param document the PDF document
     * @param currentPage the current page
     * @param drawLines whether to draw cell borders
     * @param drawContent whether to draw cell content
     * @throws IOException if an error occurs during initialization
     */
    public BaseTable(float yStart, float yStartNewPage, float bottomMargin, float width, float margin, PDDocument document, PDPage currentPage, boolean drawLines, boolean drawContent) throws IOException {
        super(yStart, yStartNewPage, 0, bottomMargin, width, margin, document, currentPage, drawLines, drawContent, new DefaultPageProvider(document, currentPage.getMediaBox()));
    }

    /**
     * Constructs a table with standard PDPage with specified top margin.
     *
     * @param yStart the initial Y coordinate for table content
     * @param yStartNewPage the Y coordinate to use for new pages
     * @param pageTopMargin the top margin of the page in points
     * @param bottomMargin the bottom margin of the page in points
     * @param width the width of the table in points
     * @param margin the left and right margin of the table in points
     * @param document the PDF document
     * @param currentPage the current page
     * @param drawLines whether to draw cell borders
     * @param drawContent whether to draw cell content
     * @throws IOException if an error occurs during initialization
     */
    public BaseTable(float yStart, float yStartNewPage, float pageTopMargin, float bottomMargin, float width, float margin, PDDocument document, PDPage currentPage, boolean drawLines, boolean drawContent) throws IOException {
        super(yStart, yStartNewPage, pageTopMargin, bottomMargin, width, margin, document, currentPage, drawLines, drawContent, new DefaultPageProvider(document, currentPage.getMediaBox()));
    }

    /**
     * Constructs a table with custom page provider and specified margins.
     *
     * @param yStart the initial Y coordinate for table content
     * @param yStartNewPage the Y coordinate to use for new pages
     * @param pageTopMargin the top margin of the page in points
     * @param bottomMargin the bottom margin of the page in points
     * @param width the width of the table in points
     * @param margin the left and right margin of the table in points
     * @param document the PDF document
     * @param currentPage the current page
     * @param drawLines whether to draw cell borders
     * @param drawContent whether to draw cell content
     * @param pageProvider the provider for creating new pages
     * @throws IOException if an error occurs during initialization
     */
    public BaseTable(float yStart, float yStartNewPage, float pageTopMargin, float bottomMargin, float width, float margin, PDDocument document, PDPage currentPage, boolean drawLines, boolean drawContent, final PageProvider<PDPage> pageProvider) throws IOException {
        super(yStart, yStartNewPage, pageTopMargin, bottomMargin, width, margin, document, currentPage, drawLines, drawContent, pageProvider);
    }

    @Override
    protected void loadFonts() {
        // Do nothing as we don't have any fonts to load
    }

}
