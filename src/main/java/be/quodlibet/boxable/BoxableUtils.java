package be.quodlibet.boxable;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

/**
 * Created by dgautier on 3/19/2015.
 */
public class BoxableUtils {

    public static final PDType0Font loadFont(PDDocument document,String fontPath) throws IOException {
        return PDType0Font.load(document, BoxableUtils.class.getClassLoader().getResourceAsStream(fontPath));
    }
}
