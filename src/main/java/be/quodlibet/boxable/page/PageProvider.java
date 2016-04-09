package be.quodlibet.boxable.page;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public interface PageProvider<T extends PDPage> {

	T createPage();

	T nextPage();

    T previousPage();

    T getCurrentPage();

    void setSize(PDRectangle size);
    PDRectangle getSize();
    PDDocument getDocument();

}
