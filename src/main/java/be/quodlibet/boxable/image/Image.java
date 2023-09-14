package be.quodlibet.boxable.image;

import be.quodlibet.boxable.utils.ImageUtils;
import be.quodlibet.boxable.utils.PageContentStreamOptimized;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class Image {

	private final BufferedImage image;

	private float width;

	private float height;

	private PDImageXObject imageXObject = null;

	// standard DPI
	private float[] dpi = { 72, 72 };

	/**
	 * <p>
	 * Constructor for default images
	 * </p>
	 *
	 * @param image
	 *            {@link BufferedImage}
	 */
	public Image(final BufferedImage image) {
		this.image = image;
		this.width = image.getWidth();
		this.height = image.getHeight();
	}

	public Image(final BufferedImage image, float dpi) {
		this(image, dpi, dpi);
	}

	public Image(final BufferedImage image, float dpiX, float dpiY) {
		this.image = image;
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.dpi[0] = dpiX;
		this.dpi[1] = dpiY;
		scaleImageFromPixelToPoints();
	}

	/**
	 * <p>
	 * Drawing simple {@link Image} in {@link PDPageContentStream}.
	 * </p>
	 *
	 * @param doc
	 *            {@link PDDocument} where drawing will be applied
	 * @param stream
	 *            {@link PDPageContentStream} where drawing will be applied
	 * @param x
	 *            X coordinate for image drawing
	 * @param y
	 *            Y coordinate for image drawing
	 * @throws IOException if loading image fails
	 */
    public void draw(final PDDocument doc, final PageContentStreamOptimized stream, float x, float y) throws IOException
    {
		if (imageXObject == null) {
			imageXObject = LosslessFactory.createFromImage(doc, image);
		}
		stream.drawImage(imageXObject, x, y - height, width, height);
	}

	/**
	 * <p>
	 * Method which scale {@link Image} with designated width
	 * </p>
	 *
	 * @deprecated Use {@link #scaleByWidth}
	 * @param width
	 *            Maximal height where {@link Image} needs to be scaled
	 * @return Scaled {@link Image}
	 */
	public Image scale(float width) {
		return scaleByWidth(width);
	}

	/**
	 * <p>
	 * Method which scale {@link Image} with designated width
	 * </p>
	 *
	 * @param width
	 *            Maximal width where {@link Image} needs to be scaled
	 * @return Scaled {@link Image}
	 */
	public Image scaleByWidth(float width) {
		float factorWidth = width / this.width;
		return scale(width, this.height * factorWidth);
	}

	private void scaleImageFromPixelToPoints() {
		float dpiX = dpi[0];
		float dpiY = dpi[1];
		scale(getImageWidthInPoints(dpiX), getImageHeightInPoints(dpiY));
	}

	/**
	 * <p>
	 * Method which scale {@link Image} with designated height
	 *
	 * @param height
	 *            Maximal height where {@link Image} needs to be scaled
	 * @return Scaled {@link Image}
	 */
	public Image scaleByHeight(float height) {
		float factorHeight = height / this.height;
		return scale(this.width * factorHeight, height);
	}

	public float getImageWidthInPoints(float dpiX) {
		return this.width * 72f / dpiX;
	}

	public float getImageHeightInPoints(float dpiY) {
		return this.height * 72f / dpiY;
	}

	/**
	 * <p>
	 * Method which scale {@link Image} with designated width und height
	 *
	 * @param boundWidth
	 *            Maximal width where {@link Image} needs to be scaled
	 * @param boundHeight
	 *            Maximal height where {@link Image} needs to be scaled
	 * @return scaled {@link Image}
	 */
	public Image scale(float boundWidth, float boundHeight) {
		float[] imageDimension = ImageUtils.getScaledDimension(this.width, this.height, boundWidth, boundHeight);
		this.width = imageDimension[0];
		this.height = imageDimension[1];
		return this;
	}

	public float getHeight() {
		return height;
	}

	public float getWidth() {
		return width;
	}
}
