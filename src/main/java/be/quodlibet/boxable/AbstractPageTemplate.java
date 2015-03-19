package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dgautier on 3/18/2015.
 */
public abstract class AbstractPageTemplate extends PDPage {

    protected abstract PDDocument getDocument();

    protected abstract float yStart();

    protected void addPicture(PDJpeg ximage, float cursorX, float cursorY, int width, int height) throws IOException {

        PDPageContentStream contentStream = new PDPageContentStream(getDocument(), this, true, false);
        contentStream.drawXObject(ximage, cursorX, cursorY, width, height);
        contentStream.close();
    }

    protected PDJpeg loadPicture(String nameJPGFile) throws IOException {
        InputStream inputStream = loadStream(nameJPGFile);
        BufferedImage awtImage = ImageIO.read(inputStream);
        return new PDJpeg(getDocument(), awtImage, 1.0f);
    }

    private InputStream loadStream(String fileName) {
        return getClass().getClassLoader().getResourceAsStream(fileName);
    }



}
