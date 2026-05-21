package be.quodlibet.boxable;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 * Abstract base class for page templates in PDF documents.
 * Provides methods for managing PDF page content including images and document references.
 */
public abstract class AbstractPageTemplate extends PDPage {

    /**
     * Creates a new AbstractPageTemplate.
     * Subclasses should provide their own constructor implementation.
     */
    protected AbstractPageTemplate() {
        super();
    }

    /**
     * Gets the PDF document associated with this page template.
     *
     * @return the PDDocument instance
     */
    protected abstract PDDocument getDocument();

    /**
     * Gets the starting Y coordinate for content on this page.
     *
     * @return the Y coordinate value
     */
    protected abstract float yStart();

    /**
     * Adds an image to the page at the specified position and dimensions.
     *
     * @param ximage the image object to add
     * @param cursorX the X coordinate where the image should be placed
     * @param cursorY the Y coordinate where the image should be placed
     * @param width the width of the image in points
     * @param height the height of the image in points
     * @throws IOException if an error occurs while writing to the PDF stream
     */
    protected void addPicture(PDImageXObject ximage, float cursorX, float cursorY, int width, int height) throws IOException {

        PDPageContentStream contentStream = new PDPageContentStream(getDocument(), this,
                PDPageContentStream.AppendMode.APPEND, false);
        contentStream.drawImage(ximage, cursorX, cursorY, width, height);
        contentStream.close();
    }

    /**
     * Loads an image from a file.
     *
     * @param nameJPGFile the file path to the image file
     * @return the loaded PDImage object
     * @throws IOException if the file cannot be read or is not a valid image
     */
    protected PDImage loadPicture(String nameJPGFile) throws IOException {
        return PDImageXObject.createFromFile(nameJPGFile, getDocument());
    }

}
