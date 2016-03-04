package be.quodlibet.boxable.image;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import be.quodlibet.boxable.utils.ImageUtils;

public class Image {

	private final BufferedImage image;

	private float width;

	private float height;

	private float[] dpi;
	
	/**
	 * <p>
	 * Constructor for default images
	 * </p>
	 * 
	 * @param image
	 * @return
	 * @throws IOException
	 */
	public Image(final BufferedImage image) {
		this.image = image;
		width = image.getWidth();
		height = image.getHeight();
	}
	
	public Image(final BufferedImage image, float dpi) {
		this(image, dpi, dpi);
	}
	
	public Image(final BufferedImage image, float dpiX, float dpiY) {
		this.image = image;
		width = image.getWidth();
		height = image.getHeight();
		this.dpi[0] = dpiX;
		this.dpi[1] = dpiY;
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
	 * @throws IOException
	 */
	public void draw(final PDDocument doc, final PDPageContentStream stream, float x, float y) throws IOException {
		PDImageXObject imageXObject = LosslessFactory.createFromImage(doc, image);
		stream.drawImage(imageXObject, x, y - height, width, height);
	}

	/**
	 * <p>
	 * Method which scale {@link Image} with designated width
	 * </p>
	 * @param width
	 *            Maximal width where {@link Image} needs to be scaled
	 * @return Scaled {@link Image}
	 */
	public Image scaleByWidth(float width) {
		float factorWidth = width / this.width;
		return scale(width, this.height * factorWidth);
	}

	/**
	 * <p>
	 * Method which scale {@link Image} with designated height
	 * </p>
	 * @param width
	 *            Maximal height where {@link Image} needs to be scaled
	 * @return Scaled {@link Image}
	 */
	public Image scaleByHeight(float height) {
		float factorHeight = height / this.height;
		return scale(this.width * factorHeight, height);
	}

	/**
	 * <p>
	 * Method which scale {@link Image} with designated width und height
	 * </p>
	 * @param width
	 *            Maximal width where {@link Image} needs to be scaled
	 * @param height
	 *            Maximal height where {@link Image} needs to be scaled
	 * @return
	 */
	public Image scale(float width, float height) {
		Dimension imageDim = new Dimension((int) image.getWidth(), (int) image.getHeight());
		Dimension newImageDim = new Dimension((int) width, (int) height);
		Dimension scaledImageDim = ImageUtils.getScaledDimension(imageDim, newImageDim);
		this.width = scaledImageDim.width;
		this.height = scaledImageDim.height;
		return this;
	}

	public float getHeight() {
		return height;
	}

	public float getWidth() {
		return width;
	}
}
