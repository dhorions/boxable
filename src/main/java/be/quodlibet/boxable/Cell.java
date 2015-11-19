
/*
 Quodlibet.be
 */
package be.quodlibet.boxable;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class Cell<T extends PDPage> {

	private float width;
	private String text;

	private PDFont font = PDType1Font.HELVETICA;
	private float fontSize = 8;
	private Color fillColor;
	private Color textColor = Color.BLACK;
	private final Row<T> row;

	// default padding
	private float leftPadding = 0f;
	private float rightPadding = 0f;
	private float topPadding = 0f;
	private float bottomPadding = 0f;

	// horizontal alignment
	public enum Align {
		LEFT, CENTER, RIGHT
	}
	
	// vertical alignment
	public enum Valign {
		TOP, MIDDLE, BOTTOM
	}

	private final Align align;
	private final Valign valign;

	public Align align() {
		return align;
	}
	
	public Valign valign() {
		return valign;
	}

	float horizontalFreeSpace = 0;
	float verticalFreeSpace = 0;

	/**
	 * 
	 * @param width
	 *            in % of table width
	 * @param text
	 */
	Cell(Row<T> row, float width, String text, boolean isCalculated, Align align, Valign valign) {
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
	}

	public PDFont getFont() {
		if (font == null) {
			throw new IllegalArgumentException("Font not set.");
		}
		return font;
	}

	public void setFont(PDFont font) {
		this.font = font;
	}

	public float getFontSize() {
		return fontSize;
	}

	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
	}

	public Paragraph getParagraph() {
		return new Paragraph(text, font, (int) fontSize, (int) getInnerWidth());
	}

	public float getExtraWidth() {
		return this.row.getLastCellExtraWidth() + getWidth();
	}

	public float getHeight() {
		return row.getHeight();
	}

	public float getTextHeight() {
		final Paragraph paragraph = getParagraph();
		return paragraph.getLines().size() * paragraph.getFontHeight();
	}

	public float getLeftPadding() {
		return leftPadding;
	}

	public void setLeftPadding(float cellLeftPadding) {
		this.leftPadding = cellLeftPadding;
	}

	public float getRightPadding() {
		return rightPadding;
	}

	public void setRightPadding(float cellRightPadding) {
		this.rightPadding = cellRightPadding;
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
}
