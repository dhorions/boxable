package be.quodlibet.boxable;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;

import be.quodlibet.boxable.page.PageProvider;

/**
 * Created by dgautier on 3/18/2015.
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

    public AbstractTemplatedTable(float yStart, float yStartNewPage, float bottomMargin, float width, float margin, PDDocument document, T currentPage, boolean drawLines, boolean drawContent, PageProvider<T> pageProvider) throws IOException {
        super(yStart, yStartNewPage, 0, bottomMargin, width, margin, document, currentPage, drawLines, drawContent, pageProvider);
    }

    public AbstractTemplatedTable(float yStartNewPage, float bottomMargin, float width, float margin, PDDocument document, boolean drawLines, boolean drawContent, PageProvider<T> pageProvider) throws IOException {
        super(yStartNewPage, 0, bottomMargin, width, margin, document, drawLines, drawContent, pageProvider);
        setYStart(getCurrentPage().yStart());
    }

}
