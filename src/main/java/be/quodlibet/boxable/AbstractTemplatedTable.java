package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;

/**
 * Created by dgautier on 3/18/2015.
 */
public abstract class AbstractTemplatedTable<T extends AbstractPageTemplate> extends Table<T> {

    public AbstractTemplatedTable(float yStart, float yStartNewPage, float bottomMargin, float width, float margin, PDDocument document, T currentPage, boolean drawLines, boolean drawContent) throws IOException {
        super(yStart, yStartNewPage, bottomMargin, width, margin, document, currentPage, drawLines, drawContent);
    }

    public AbstractTemplatedTable(float yStartNewPage, float bottomMargin, float width, float margin, PDDocument document, boolean drawLines, boolean drawContent) throws IOException {
        super(yStartNewPage, bottomMargin, width, margin, document, drawLines, drawContent);
        setYStart(getCurrentPage().yStart());
    }

}
