package be.quodlibet.boxable;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;

/**
 * Created by dgautier on 3/19/2015.
 */
public class BoxableUtils {

    public static final PDTrueTypeFont loadFont(PDDocument document,String fontPath) throws IOException {
        return PDTrueTypeFont.loadTTF(document, BoxableUtils.class.getClassLoader().getResourceAsStream(fontPath));
    }
}
