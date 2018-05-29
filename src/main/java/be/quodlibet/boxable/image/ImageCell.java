package be.quodlibet.boxable.image;

import org.apache.pdfbox.pdmodel.PDPage;

import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.VerticalAlignment;

public class ImageCell extends Cell {

	private Image img;
	
	private final HorizontalAlignment align;
	
	private final VerticalAlignment valign;

	public ImageCell(Row row, float width, Image image, boolean isCalculated) {
		super(row, width, null, isCalculated);
		this.img = image;
		if(image.getWidth() > getInnerWidth()){
			scaleToFit();
		}
		this.align = HorizontalAlignment.LEFT;
		this.valign = VerticalAlignment.TOP;
	}

	public void scaleToFit() {
		img = img.scale(getInnerWidth());
	}

	public ImageCell(Row row, float width, Image image, boolean isCalculated, HorizontalAlignment align,
			VerticalAlignment valign) {
		super(row, width, null, isCalculated, align, valign);
		this.img = image;
		if(image.getWidth() > getInnerWidth()){
			scaleToFit();
		}
		this.align = align;
		this.valign = valign;
	}

	@Override
	public float getTextHeight() {
		return img.getHeight();
	}

	@Override
	public float getHorizontalFreeSpace() {
		return getInnerWidth() - img.getWidth();
	}
	
	@Override
	public float getVerticalFreeSpace() {
		return getInnerHeight() - img.getHeight();
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
