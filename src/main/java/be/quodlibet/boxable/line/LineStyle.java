package be.quodlibet.boxable.line;

import java.awt.BasicStroke;
import java.awt.Color;

/**
 * <p>
 * The <code>LineStyle</code> class defines a basic set of rendering attributes
 * for lines.
 * </p>
 * 
 * @author hstimac
 * @author mkuehne
 *
 */
public class LineStyle {

	private final Color color;

	private final float width;

	private float[] dashArray;

	private float dashPhase;

	/**
	 * <p>
	 * Simple constructor for setting line {@link Color} and line width
	 * </p>
	 * 
	 * @param color
	 *            The line {@link Color
	 * @param width
	 *            The line width
	 */
	public LineStyle(final Color color, final float width) {
		this.color = color;
		this.width = width;
	}

	/**
	 * <p>
	 * Provides ability to produce dotted line.
	 * </p>
	 * 
	 * @param color
	 *            The {@link Color} of the line
	 * @param width
	 *            The line width
	 * @return new styled line
	 */
	public static LineStyle produceDotted(final Color color, final int width) {
		final LineStyle line = new LineStyle(color, width);
		line.dashArray = new float[] { 1.0f };
		line.dashPhase = 0.0f;

		return line;
	}

	/**
	 * <p>
	 * Provides ability to produce dashed line.
	 * </p>
	 * 
	 * @param color
	 *            The {@link Color} of the line
	 * @param width
	 *            The line width
	 * @return new styled line
	 */
	public static LineStyle produceDashed(final Color color, final int width) {
		return produceDashed(color, width, new float[] { 5.0f }, 0.0f);
	}

	/**
	 * 
	 * @param color
	 *            The {@link Color} of the line
	 * @param width
	 *            The line width
	 * @param dashArray
	 *            Mimics the behavior of {@link BasicStroke#getDashArray()}
	 * @param dashPhase
	 *            Mimics the behavior of {@link BasicStroke#getDashPhase()}
	 * @return new styled line
	 */
	public static LineStyle produceDashed(final Color color, final int width, final float[] dashArray,
			final float dashPhase) {
		final LineStyle line = new LineStyle(color, width);
		line.dashArray = dashArray;
		line.dashPhase = dashPhase;

		return line;
	}

	public Color getColor() {
		return color;
	}

	public float getWidth() {
		return width;
	}

	public float[] getDashArray() {
		return dashArray;
	}

	public float getDashPhase() {
		return dashPhase;
	}

}
