package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDPage;

import be.quodlibet.boxable.image.Image;

public class ImageCell<T extends PDPage> extends Cell<T> {

	private Image img;
	
	private final HorizontalAlignment align;
	
	private final VerticalAlignment valign;

	ImageCell(Row<T> row, float width, Image image, boolean isCalculated) {
		super(row, width, null, isCalculated);
		this.img = image.scale(getInnerWidth());
		this.align = HorizontalAlignment.LEFT;
		this.valign = VerticalAlignment.TOP;
	}

	ImageCell(Row<T> row, float width, Image image, boolean isCalculated, HorizontalAlignment align,
			VerticalAlignment valign) {
		super(row, width, null, isCalculated);
		this.img = image;
		this.align = align;
		this.valign = valign;
	}

	@Override
	public float getTextHeight() {
		return img.getHeight();
	}

	@Override
	public float getHorizontalFreeSpace() {
		return getInnerWidth() - getImageWidth();
	}

	private float getImageWidth() {
		return img.getWidth();
	}

	/**
	 * <p>
	 * Method which retrieve {@link Image}
	 * </p>
	 * 
	 * @return {@link Image}
	 */
	public Image getImage() {
		return img;
	}
}
