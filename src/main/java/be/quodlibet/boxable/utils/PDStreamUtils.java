package be.quodlibet.boxable.utils;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import be.quodlibet.boxable.line.LineStyle;

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
	 * Provides ability to write on a {@link PDPageContentStream}. The text will
	 * be written above Y coordinate.
	 * </p>
	 * 
	 * @param stream
	 *            The {@link PDPageContentStream} where writing will be applied.
	 * @param text
	 *            The text which will be displayed.
	 * @param font
	 *            The font of the text
	 * @param fontSize
	 *            The font size of the text
	 * @param x
	 *            Start X coordinate for text.
	 * @param y
	 *            Start Y coordinate for text.
	 * @param color
	 *            Color of the text
	 */
	public static void write(final PageContentStreamOptimized stream, final String text, final PDFont font,
			final float fontSize, final float x, final float y, final Color color) {
		try {
			stream.setFont(font, fontSize);
			// we want to position our text on his baseline
			stream.newLineAt(x, y - FontUtils.getDescent(font, fontSize) - FontUtils.getHeight(font, fontSize));
			stream.setNonStrokingColor(color);
			stream.showText(text);
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
	public static void rect(final PageContentStreamOptimized stream, final float x, final float y, final float width,
			final float height, final Color color) {
		try {
			stream.setNonStrokingColor(color);
			// negative height because we want to draw down (not up!)
			stream.addRect(x, y, width, -height);
			stream.fill();
		} catch (final IOException e) {
			throw new IllegalStateException("Unable to draw rectangle", e);
		}
	}

	/**
	 * <p>
	 * Provides ability to draw font metrics (font height, font ascent, font
	 * descent).
	 * </p>
	 * 
	 * @param stream
	 *            The {@link PDPageContentStream} where drawing will be applied.
	 * @param x
	 *            Start X coordinate for rectangle.
	 * @param y
	 *            Start Y coordinate for rectangle.
	 * @param font
	 *            {@link PDFont} from which will be obtained font metrics
	 * @param fontSize
	 *            Font size
	 */
	public static void rectFontMetrics(final PageContentStreamOptimized stream, final float x, final float y,
			final PDFont font, final float fontSize) {
		// height
		PDStreamUtils.rect(stream, x, y, 3, FontUtils.getHeight(font, fontSize), Color.BLUE);
		// ascent
		PDStreamUtils.rect(stream, x + 3, y, 3, FontUtils.getAscent(font, fontSize), Color.CYAN);
		// descent
		PDStreamUtils.rect(stream, x + 3, y - FontUtils.getHeight(font, fontSize), 3, FontUtils.getDescent(font, 14),
				Color.GREEN);
	}

	/**
	 * <p>
	 * Provides ability to set different line styles (line width, dotted line,
	 * dashed line)
	 * </p>
	 * 
	 * @param stream
	 *            The {@link PDPageContentStream} where drawing will be applied.
	 * @param line
	 *            The {@link LineStyle} that would be applied
	 * @throws IOException If the content stream could not be written or the line color cannot be retrieved.
	 */
	public static void setLineStyles(final PageContentStreamOptimized stream, final LineStyle line) throws IOException {
		stream.setStrokingColor(line.getColor());
		stream.setLineWidth(line.getWidth());
		stream.setLineCapStyle(0);
		if (line.getDashArray() != null) {
			stream.setLineDashPattern(line.getDashArray(), line.getDashPhase());
		} else {
			stream.setLineDashPattern(new float[] {}, 0.0f);
		}
	}
}
