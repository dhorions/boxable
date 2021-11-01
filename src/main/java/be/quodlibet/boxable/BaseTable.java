package be.quodlibet.boxable;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import be.quodlibet.boxable.page.DefaultPageProvider;
import be.quodlibet.boxable.page.PageProvider;

/**
 * Created by dgautier on 3/18/2015.
 */
public class BaseTable extends Table<PDPage> {

    public BaseTable(float yStart, float yStartNewPage, float bottomMargin, float width, float margin, 
            PDDocument document, PDPage currentPage, boolean drawLines, boolean drawContent) throws IOException {
        this(yStart, yStartNewPage, 0, bottomMargin, width, margin, document, currentPage, drawLines, drawContent, newPageProvider(document, currentPage));
    }

    public BaseTable(float yStart, float yStartNewPage, float pageTopMargin, float bottomMargin, float width, float margin,
            PDDocument document, PDPage currentPage, boolean drawLines, boolean drawContent) throws IOException {
        this(yStart, yStartNewPage, pageTopMargin, bottomMargin, width, margin, document, currentPage, drawLines, drawContent, newPageProvider(document, currentPage));
    }

    public BaseTable(float yStart, float yStartNewPage, float pageTopMargin, float bottomMargin, float width, float margin,
            PDDocument document, PDPage currentPage, boolean drawLines, boolean drawContent, PageProvider<PDPage> pageProvider) throws IOException {
        super(yStart, yStartNewPage, pageTopMargin, bottomMargin, width, margin, document, currentPage, drawLines, drawContent, pageProvider);
    }

    protected static DefaultPageProvider newPageProvider(final PDDocument doc, final PDPage page) {
        return new DefaultPageProvider(doc, page.getMediaBox(), page.getRotation());
    }

    @Override
    protected void loadFonts() {
        // Do nothing as we don't have any fonts to load
    }

}
