package be.quodlibet.boxable.page;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class DefaultPageProvider implements PageProvider<PDPage> {

	private final PDDocument document;

	private  PDRectangle size;

	private int currentPageIndex = -1;

	public DefaultPageProvider(final PDDocument document, final PDRectangle size) {
            this.document = document;
            this.size = size;
            this.currentPageIndex = document.getNumberOfPages() - 1;

	}

	@Override
	public PDDocument getDocument() {
		return document;
	}

	@Override
	public PDPage createPage() {
		currentPageIndex = document.getNumberOfPages();
		return getCurrentPage();
	}

	@Override
	public PDPage nextPage() {
            if (currentPageIndex == -1) {
                //currentPageIndex = document.getNumberOfPages();
                    currentPageIndex = document.getNumberOfPages() - 1;
                //unless the document has no pages yet
                if (document.getNumberOfPages() == 0) {
                    currentPageIndex = 0;
                }
		} else {
			currentPageIndex++;
		}

		return getCurrentPage();
	}

	@Override
	public PDPage previousPage() {
		currentPageIndex--;
		if (currentPageIndex < 0) {
			currentPageIndex = 0;
		}

		return getCurrentPage();
	}

    public PDPage getCurrentPage()
    {
        //If there are no pages, create a first page
        if (document.getNumberOfPages() == 0) {
            currentPageIndex = 0;
        }

        if (currentPageIndex >= document.getNumberOfPages()) {
            final PDPage newPage = new PDPage(size);
            document.addPage(newPage);
            return newPage;
        }
	return document.getPage(currentPageIndex);
	}

    @Override
    public void setSize(PDRectangle size)
    {
        this.size = size;

    }

    @Override
    public PDRectangle getSize()
    {
        return size;
    }

}
