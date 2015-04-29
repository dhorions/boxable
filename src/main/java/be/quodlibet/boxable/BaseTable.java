package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.IOException;

/**
 * Created by dgautier on 3/18/2015.
 */
public class BaseTable extends Table<PDPage> {

    public BaseTable(float yStart, float yStartNewPage, float bottomMargin, float width, float margin, PDDocument document, PDPage currentPage, boolean drawLines, boolean drawContent) throws IOException {
        super(yStart, yStartNewPage, bottomMargin, width, margin, document, currentPage, drawLines, drawContent);
    }

    @Override
    protected void loadFonts() {
        // Do nothing as we don't have any fonts to load
    }

    @Override
    protected PDPage createPage() {
        PDPage nPage = new PDPage();
	document.addPage(nPage);
        return nPage;
    }
}
