package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDPage;

import be.quodlibet.boxable.image.Image;
import be.quodlibet.boxable.line.LineStyle;

public class ImageCell<T extends PDPage> extends Cell<T> {

	private Image img;

	private float originalImageWidth;
	private float originalImageHeight;
	
	private final HorizontalAlignment align;
	
	private final VerticalAlignment valign;

	ImageCell(Row<T> row, float width, Image image, boolean isCalculated) {
		super(row, width, null, isCalculated);
		this.img = image;
		this.originalImageWidth = image.getWidth();
		this.originalImageHeight = image.getHeight();
		scaleToFit();
		this.align = HorizontalAlignment.LEFT;
		this.valign = VerticalAlignment.TOP;
	}

	public void scaleToFit() {
		// If image is larger than the inner width of the cell, we scale it down to fit.
		// If the image is smaller than the inner width of the cell, we make sure it is at its original size.
		// This allows the image to "grow back" if padding is removed, but never grow larger than the original image.
		
		// Reset image to original dimensions first, so that scaling (which usually only downscales) 
		// has the correct starting point.
		img.setWidth(originalImageWidth);
		img.setHeight(originalImageHeight);
		
		if(originalImageWidth > getInnerWidth()) {
			img = img.scaleByWidth(getInnerWidth());
		}
		
		// We use getRow().getLineHeight() instead of getHeight() (which triggers row expansion)
		// to determine the constraint.
		float currentHeight = 0;
		try {
		    currentHeight = getRow().getLineHeight();
		} catch (Exception e) {
		    // Fallback if getLineHeight fails, though unlikely
		    currentHeight = 0; 
		}

        float availableHeight = currentHeight - getTopPadding() - getBottomPadding();
        if(getTopBorder() != null) availableHeight -= getTopBorder().getWidth();
        if(getBottomBorder() != null) availableHeight -= getBottomBorder().getWidth();

		// Check if height exceeds available height (if row height is defined)
		if(availableHeight > 0 && img.getHeight() > availableHeight) {
			img = img.scaleByHeight(availableHeight);
		}
	}

	ImageCell(Row<T> row, float width, Image image, boolean isCalculated, HorizontalAlignment align,
			VerticalAlignment valign) {
		super(row, width, null, isCalculated, align, valign);
		this.img = image;
		this.originalImageWidth = image.getWidth();
		this.originalImageHeight = image.getHeight();
		scaleToFit();
		this.align = align;
		this.valign = valign;
	}

	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		scaleToFit();
	}
	
	@Override
	public void setLeftPadding(float cellLeftPadding) {
		super.setLeftPadding(cellLeftPadding);
		scaleToFit();
	}

	@Override
	public void setRightPadding(float cellRightPadding) {
		super.setRightPadding(cellRightPadding);
		scaleToFit();
	}

	@Override
	public void setTopPadding(float cellTopPadding) {
		super.setTopPadding(cellTopPadding);
		// Height changes might affect scaling if we ever support height-constrained scaling, 
		// but currently only width is constrained. 
		// However, it's safer to re-check if we ever add logic for height.
		// For now, consistent behavior:
		scaleToFit();
	}

	@Override
	public void setBottomPadding(float cellBottomPadding) {
		super.setBottomPadding(cellBottomPadding);
		scaleToFit();
	}
	
	@Override
	public void setLeftBorderStyle(LineStyle leftBorder) {
		super.setLeftBorderStyle(leftBorder);
		scaleToFit();
	}

	@Override
	public void setRightBorderStyle(LineStyle rightBorder) {
		super.setRightBorderStyle(rightBorder);
		scaleToFit();
	}

	@Override
	public void setBorderStyle(LineStyle border) {
		super.setBorderStyle(border);
		scaleToFit();
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
