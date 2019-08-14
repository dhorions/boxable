package be.quodlibet.boxable.page;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

public class DefaultPageProvider implements PageProvider<PDPage> {

	public static final int ANG_PORTRAIT  =  0;
	public static final int ANG_LANDSCAPE = 90;

	private final PDDocument document;

	private final PDRectangle size;
	private final int rotation;

	private int currentPageIndex = -1;

	public DefaultPageProvider(final PDDocument document, final PDRectangle size) {
		this(document, size, 0);
	}

	public DefaultPageProvider(final PDDocument document, final PDRectangle size, final int rotation) {
		this.document = document;
		this.size = size;
		this.rotation = rotation;
	}

	@Override
	public PDDocument getDocument() {
		return document;
	}

	@Override
	public PDPage createPage() throws IOException {
		currentPageIndex = document.getNumberOfPages();
		return getCurrentPage();
	}

	@Override
	public PDPage nextPage() throws IOException {
		if (currentPageIndex == -1) {
			currentPageIndex = document.getNumberOfPages();
		} else {
			currentPageIndex++;
		}

		return getCurrentPage();
	}

	@Override
	public PDPage previousPage() throws IOException {
		currentPageIndex--;
		if (currentPageIndex < 0) {
			currentPageIndex = 0;
		}

		return getCurrentPage();
	}

	private PDPage getCurrentPage() throws IOException {
		if (currentPageIndex >= document.getNumberOfPages()) {
			final PDPage newPage = new PDPage(size);
			newPage.setRotation(rotation);
			if (rotation == ANG_LANDSCAPE) { // => change ref. for drawing
				addContStrmRot(newPage);
			}
			document.addPage(newPage);
			return newPage;
		}

		return document.getPage(currentPageIndex);
	}

	protected void addContStrmRot(final PDPage pg) throws IOException {
		final PDPageContentStream cont = new PDPageContentStream(getDocument(), pg, AppendMode.APPEND, true);
		cont.transform(newTransfMtxOrientL(pg));
		cont.close();
	}

	protected Matrix newTransfMtxOrientL(final PDPage pg) {
		return newTransfMtxOrientL(pg.getMediaBox().getWidth());
	}

	protected Matrix newTransfMtxOrientL(final float w) {
		return new Matrix(0, 1, -1, 0, w, 0);
	}
}
