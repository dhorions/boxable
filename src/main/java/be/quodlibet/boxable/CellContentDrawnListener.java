package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public interface CellContentDrawnListener<T extends PDPage> {
    void onContentDrawn(Cell<T> cell, PDDocument document, PDPage page, PDRectangle rectangle);
}
