package be.quodlibet.boxable.page;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public interface PageProvider<T extends PDPage> {

	T createPage() throws IOException;

	T nextPage() throws IOException;

	T previousPage() throws IOException;

	PDDocument getDocument();
}
