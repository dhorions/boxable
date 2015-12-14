
/*
 Quodlibet.be
 */
package be.quodlibet.boxable;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import be.quodlibet.boxable.text.WrappingFunction;

public class Cell<T extends PDPage> {

	private float width;
	private String text;

	private PDFont font = PDType1Font.HELVETICA;
	private PDFont fontBold = PDType1Font.HELVETICA_BOLD;

	private float fontSize = 8;
	private Color fillColor;
	private Color textColor = Color.BLACK;
	private final Row<T> row;
	private WrappingFunction wrappingFunction;
	private boolean isHeaderCell = false;

	// default padding
	private float leftPadding = 5f;
	private float rightPadding = 5f;
	private float topPadding = 5f;
	private float bottomPadding = 5f;

	private Paragraph paragraph = null;

	private final HorizontalAlignment align;
	private final VerticalAlignment valign;

	float horizontalFreeSpace = 0;
	float verticalFreeSpace = 0;

	/**
	 * <p>
	 * Constructs a cell with the default alignment
	 * {@link VerticalAlignment#TOP} {@link HorizontalAlignment#LEFT}.
	 * </p>
	 * 
	 * @param row
	 * @param width
	 * @param text
	 * @param isCalculated
	 * @see Cell#Cell(Row, float, String, boolean, HorizontalAlignment,
	 *      VerticalAlignment)
	 */
	Cell(Row<T> row, float width, String text, boolean isCalculated) {
		this(row, width, text, isCalculated, HorizontalAlignment.LEFT, VerticalAlignment.TOP);
	}

	/**
	 * <p>
	 * Constructs a cell.
	 * </p>
	 * 
	 * @param row
	 *            The parent row
	 * @param width
	 *            absolute width in points or in % of table width (depending on
	 *            the parameter {@code isCalculated})
	 * @param text
	 *            The text content of the cell
	 * @param isCalculated
	 *            If {@code true}, the width is interpreted in % to the table
	 *            width
	 * @param align
	 *            The {@link HorizontalAlignment} of the cell content
	 * @param valign
	 *            The {@link VerticalAlignment} of the cell content
	 * @see Cell#Cell(Row, float, String, boolean)
	 */
	Cell(Row<T> row, float width, String text, boolean isCalculated, HorizontalAlignment align,
			VerticalAlignment valign) {
		this.row = row;
		if (isCalculated) {
			double calclulatedWidth = ((row.getWidth() * width) / 100);
			this.width = (float) calclulatedWidth;
		} else {
			this.width = width;
		}

		if (getWidth() > row.getWidth()) {
			throw new IllegalArgumentException(
					"Cell Width=" + getWidth() + " can't be bigger than row width=" + row.getWidth());
		}
		this.text = text == null ? "" : text;
		this.align = align;
		this.valign = valign;
		this.wrappingFunction = null;
	}

	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	public float getWidth() {
		return width;
	}

	public float getInnerWidth() {
		return getWidth() - getLeftPadding() - getRightPadding();
	}

	public float getInnerHeight() {
		return getHeight() - getBottomPadding() - getTopPadding();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;

		// paragraph invalidated
		paragraph = null;
	}

	public PDFont getFont() {
		if (font == null) {
			throw new IllegalArgumentException("Font not set.");
		}
		return font;
	}

	public void setFont(PDFont font) {
		this.font = font;

		// paragraph invalidated
		paragraph = null;
	}

	public float getFontSize() {
		return fontSize;
	}

	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;

		// paragraph invalidated
		paragraph = null;
	}

	public Paragraph getParagraph() {
		if (paragraph == null) {
			if (isHeaderCell) {
				paragraph = new Paragraph(text, fontBold, fontSize, getInnerWidth(), align, textColor, null, wrappingFunction);
			} else {
				paragraph = new Paragraph(text, font, fontSize, getInnerWidth(), align, textColor, null, wrappingFunction);
			}
		}
		return paragraph;
	}

	public float getExtraWidth() {
		return this.row.getLastCellExtraWidth() + getWidth();
	}

	public float getHeight() {
		return row.getHeight();
	}

	public float getTextHeight() {
		return getParagraph().getHeight();
	}

	public float getLeftPadding() {
		return leftPadding;
	}

	public void setLeftPadding(float cellLeftPadding) {
		this.leftPadding = cellLeftPadding;

		// paragraph invalidated
		paragraph = null;
	}

	public float getRightPadding() {
		return rightPadding;
	}

	public void setRightPadding(float cellRightPadding) {
		this.rightPadding = cellRightPadding;

		// paragraph invalidated
		paragraph = null;
	}

	public float getTopPadding() {
		return topPadding;
	}

	public void setTopPadding(float cellTopPadding) {
		this.topPadding = cellTopPadding;
	}

	public float getBottomPadding() {
		return bottomPadding;
	}

	public void setBottomPadding(float cellBottomPadding) {
		this.bottomPadding = cellBottomPadding;
	}

	public float getVerticalFreeSpace() {
		return getInnerHeight() - getTextHeight();
	}

	public float getHorizontalFreeSpace() {
		float tw = 0.0f;
		try {
			for (final String line : getParagraph().getLines()) {
				tw = Math.max(tw, getFont().getStringWidth(line.trim()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		tw = tw / 1000 * getFontSize();
		return getInnerWidth() - tw;
	}

	public HorizontalAlignment getAlign() {
		return align;
	}

	public VerticalAlignment getValign() {
		return valign;
	}

	public boolean isHeaderCell() {
		return isHeaderCell;
	}

	public void setHeaderCell(boolean isHeaderCell) {
		this.isHeaderCell = isHeaderCell;
	}

	public WrappingFunction getWrappingFunction() {
		return getParagraph().getWrappingFunction();
	}

	public void setWrappingFunction(WrappingFunction wrappingFunction) {
		this.wrappingFunction = wrappingFunction;

		// paragraph invalidated
		paragraph = null;
	}

	public PDFont getFontBold() {
		return fontBold;
	}

}
