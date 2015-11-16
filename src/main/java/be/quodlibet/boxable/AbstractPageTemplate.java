package be.quodlibet.boxable;

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 * Created by dgautier on 3/18/2015.
 */
public abstract class AbstractPageTemplate extends PDPage {

    protected abstract PDDocument getDocument();

    protected abstract float yStart();

    protected void addPicture(PDImageXObject ximage, float cursorX, float cursorY, int width, int height) throws IOException {

        PDPageContentStream contentStream = new PDPageContentStream(getDocument(), this, true, false);
        contentStream.drawXObject(ximage, cursorX, cursorY, width, height);
        contentStream.close();
    }

    protected PDImage loadPicture(String nameJPGFile) throws IOException {
        return PDImageXObject.createFromFile(nameJPGFile, getDocument());
    }

    private InputStream loadStream(String fileName) {
        return getClass().getClassLoader().getResourceAsStream(fileName);
    }



}
