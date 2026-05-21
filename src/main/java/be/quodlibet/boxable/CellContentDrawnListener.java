package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

/**
 * Listener interface for cell content drawing events.
 * <p>
 * Implementations of this interface can be used to handle custom logic when
 * cell content has been drawn on a PDF page.
 * </p>
 *
 * @param <T> the type of PDPage
 */
public interface CellContentDrawnListener<T extends PDPage> {
    /**
     * Called when cell content has been drawn on the page.
     *
     * @param cell the {@link Cell} whose content was drawn
     * @param document the {@link PDDocument} containing the page
     * @param page the {@link PDPage} on which content was drawn
     * @param rectangle the {@link PDRectangle} bounds of the drawn content
     */
    void onContentDrawn(Cell<T> cell, PDDocument document, PDPage page, PDRectangle rectangle);
}
