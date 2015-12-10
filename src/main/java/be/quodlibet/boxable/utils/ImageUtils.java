package be.quodlibet.boxable.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;

import org.w3c.dom.NodeList;

import be.quodlibet.boxable.image.Image;

/**
 * <p>
 * Utility methods for images
 * </p>
 * 
 * @author hstimac
 * @author mkuehne
 */
public final class ImageUtils {

	// utility class, no instance needed
	private ImageUtils() {
	}

	/**
	 * <p>
	 * Simple reading image from file
	 * </p>
	 * 
	 * @param imageFile
	 *            {@link File} from which image will be loaded
	 * @return {@link Image}
	 * @throws IOException
	 */
	public static Image readImage(File imageFile) throws IOException {
		final float[] dpi = getDPI(imageFile);
		final BufferedImage bufferedImage = ImageIO.read(imageFile);

		return new Image(bufferedImage, dpi[0], dpi[1]);
	}

	/**
	 * <p>
	 * Method calculates image's DPI (horizontal and vertical)
	 * </p>
	 * 
	 * @param imageFile
	 *            {@link File} from which image's DPI will be calculated
	 * @return {@link Float[]} array with calculated horizontal and vertical
	 *         image DPI's. First element is horizontal DPI and the second is
	 *         vertical DPI.
	 * @throws IOException
	 *             If a cache file is needed but cannot be created.
	 */
	public static float[] getDPI(File imageFile) throws IOException {
		ImageInputStream stream = ImageIO.createImageInputStream(imageFile);

		Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
		if (readers.hasNext()) {
			ImageReader reader = readers.next();
			reader.setInput(stream);

			IIOMetadata metadata = reader.getImageMetadata(0);
			IIOMetadataNode standardTree = (IIOMetadataNode) metadata
					.getAsTree(IIOMetadataFormatImpl.standardMetadataFormatName);
			IIOMetadataNode dimension = (IIOMetadataNode) standardTree.getElementsByTagName("Dimension").item(0);
			float horizontalPixelSizeMM = getPixelSizeMM(dimension, "HorizontalPixelSize");
			float verticalPixelSizeMM = getPixelSizeMM(dimension, "VerticalPixelSize");

			float[] result = new float[2];
			result[0] = pixelMMtoDPI(horizontalPixelSizeMM);
			result[1] = pixelMMtoDPI(verticalPixelSizeMM);
			return result;
		}

		throw new IllegalArgumentException("Cannot read DPI of [" + imageFile + "]");
	}

	/**
	 * <p>
	 * Extracting pixels per millimeter using the standard {@link ImageIO} API
	 * and the standard metadata format.
	 * </p>
	 * 
	 * @param dimension
	 * @param elementName
	 * @return
	 */
	private static float getPixelSizeMM(final IIOMetadataNode dimension, final String elementName) {
		// NOTE: The standard metadata format has defined dimension to pixels per millimeters, not DPI...
		NodeList pixelSizes = dimension.getElementsByTagName(elementName);
		IIOMetadataNode pixelSize = pixelSizes.getLength() > 0 ? (IIOMetadataNode) pixelSizes.item(0) : null;
		return pixelSize != null ? Float.parseFloat(pixelSize.getAttribute("value")) : -1;
	}

	/**
	 * <p>
	 * Converts pixel per millimeter into DPI value.
	 * </p>
	 * 
	 * @param pixelPerMM
	 *            Value in pixel per millimeter
	 * @return Calculated DPI value
	 */
	private static float pixelMMtoDPI(float pixelPerMM) {
		return (1 / pixelPerMM) * 25.4f;
	}
}
