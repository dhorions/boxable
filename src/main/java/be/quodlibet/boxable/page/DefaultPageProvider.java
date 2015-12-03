package be.quodlibet.boxable.page;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class DefaultPageProvider implements PageProvider<PDPage> {
	
	private final PDDocument document;
	private final PDRectangle size;

	public DefaultPageProvider(final PDDocument document, final PDRectangle size) {
		this.document = document;
		this.size = size;
	}

	@Override
	public PDPage createPage() {
		final PDPage newPage = new PDPage(size);
        document.addPage(newPage);
        return newPage;
	}

	@Override
	public PDDocument getDocument() {
		return document;
	}

}
