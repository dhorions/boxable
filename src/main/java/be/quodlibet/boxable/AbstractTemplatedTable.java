package be.quodlibet.boxable;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;

import be.quodlibet.boxable.page.PageProvider;

/**
 * Abstract templated table implementation for PDF documents with custom page templates.
 * Extends the base Table class with support for custom page templates.
 *
 * @param <T> the page template type extending AbstractPageTemplate
 */
public abstract class AbstractTemplatedTable<T extends AbstractPageTemplate> extends Table<T> {

	@Deprecated
	public AbstractTemplatedTable(float yStart, float yStartNewPage, float bottomMargin, float width, float margin, PDDocument document, T currentPage, boolean drawLines, boolean drawContent) throws IOException {
		super(yStart, yStartNewPage, bottomMargin, width, margin, document, currentPage, drawLines, drawContent);
	}

	@Deprecated
	public AbstractTemplatedTable(float yStartNewPage, float bottomMargin, float width, float margin, PDDocument document, boolean drawLines, boolean drawContent) throws IOException {
		super(yStartNewPage, bottomMargin, width, margin, document, drawLines, drawContent);
        setYStart(getCurrentPage().yStart());
	}

    /**
     * Constructs a templated table with an existing page and specified position.
     *
     * @param yStart the initial Y coordinate for table content
     * @param yStartNewPage the Y coordinate to use for new pages
     * @param bottomMargin the bottom margin of the page in points
     * @param width the width of the table in points
     * @param margin the left and right margin of the table in points
     * @param document the PDF document
     * @param currentPage the current page template
     * @param drawLines whether to draw cell borders
     * @param drawContent whether to draw cell content
     * @param pageProvider the provider for creating new pages
     * @throws IOException if an error occurs during initialization
     */
    public AbstractTemplatedTable(float yStart, float yStartNewPage, float bottomMargin, float width, float margin, PDDocument document, T currentPage, boolean drawLines, boolean drawContent, PageProvider<T> pageProvider) throws IOException {
        super(yStart, yStartNewPage, 0, bottomMargin, width, margin, document, currentPage, drawLines, drawContent, pageProvider);
    }

    /**
     * Constructs a templated table without an existing page, using the page provider.
     *
     * @param yStartNewPage the Y coordinate to use for new pages
     * @param bottomMargin the bottom margin of the page in points
     * @param width the width of the table in points
     * @param margin the left and right margin of the table in points
     * @param document the PDF document
     * @param drawLines whether to draw cell borders
     * @param drawContent whether to draw cell content
     * @param pageProvider the provider for creating new pages
     * @throws IOException if an error occurs during initialization
     */
    public AbstractTemplatedTable(float yStartNewPage, float bottomMargin, float width, float margin, PDDocument document, boolean drawLines, boolean drawContent, PageProvider<T> pageProvider) throws IOException {
        super(yStartNewPage, 0, bottomMargin, width, margin, document, drawLines, drawContent, pageProvider);
        setYStart(getCurrentPage().yStart());
    }

}
