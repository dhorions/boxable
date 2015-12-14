package be.quodlibet.boxable.utils;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * <p>
 * Utility methods for {@link PDPageContentStream}
 * </p>
 * 
 * @author hstimac
 * @author mkuehne
 *
 */
public final class PDStreamUtils {

	private PDStreamUtils() {
	}

	/**
	 * <p>
	 * Provides ability to write on a {@link PDPageContentStream}. The text will be written above Y coordinate.
	 * </p>
	 * 
	 * @param stream
	 *            The {@link PDPageContentStream} where writing will be applied.
	 * @param text
	 *            The text which will be displayed.
	 * @param font The font of the text
	 * @param fontSize The font size of the text
	 * @param x
	 *            Start X coordinate for text.
	 * @param y
	 *            Start Y coordinate for text.
	 * @param color
	 *            Color of the text
	 */
	public static void write(final PDPageContentStream stream, final String text, final PDFont font,
			final float fontSize, final float x, final float y, final Color color) {
		try {
			stream.beginText();
			stream.setFont(font, fontSize);
			// we want to position our text on his baseline
			stream.newLineAtOffset(x, y - FontUtils.getDescent(font, fontSize) - FontUtils.getHeight(font, fontSize));
			stream.setNonStrokingColor(color);
			stream.showText(text);
			stream.endText();
		} catch (final IOException e) {
			throw new IllegalStateException("Unable to write text", e);
		}
	}

	/**
	 * <p>
	 * Provides ability to draw rectangle for debugging purposes.
	 * </p>
	 * 
	 * @param stream
	 *            The {@link PDPageContentStream} where drawing will be applied.
	 * @param x
	 *            Start X coordinate for rectangle.
	 * @param y
	 *            Start Y coordinate for rectangle.
	 * @param width
	 *            Width of rectangle
	 * @param height
	 *            Height of rectangle
	 * @param color
	 *            Color of the text
	 */
	public static void rect(final PDPageContentStream stream, final float x, final float y, final float width,
			final float height, final Color color) {
		try {
			stream.setNonStrokingColor(color);
			// negative height because we want to draw down (not up!)
			stream.addRect(x, y, width, -height);
			stream.fill();
			stream.closePath();

			// Reset NonStroking Color to default value
			stream.setNonStrokingColor(Color.BLACK);
		} catch (final IOException e) {
			throw new IllegalStateException("Unable to draw rectangle", e);
		}
	}
	
	public static void rectFontMetrics(final PDPageContentStream stream, final float x, final float y, final PDFont font, final float fontSize) {
		// height
		PDStreamUtils.rect(stream, x, y, 3, FontUtils.getHeight(font, fontSize), Color.BLUE);
		// ascent
		PDStreamUtils.rect(stream, x+3, y, 3, FontUtils.getAscent(font, fontSize), Color.CYAN);
		// descent
		PDStreamUtils.rect(stream, x+3, y - FontUtils.getHeight(font, fontSize), 3, FontUtils.getDescent(font, 14), Color.GREEN);
	}
}
