
/*
 Quodlibet.be
 */
package be.quodlibet.boxable;

import java.awt.Color;
import java.net.URL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import be.quodlibet.boxable.line.LineStyle;
import be.quodlibet.boxable.text.WrappingFunction;
import be.quodlibet.boxable.utils.FontUtils;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class Cell<T extends PDPage> {

	private float width;
	private Float height;
	private String text;

	private URL url = null;

	private PDFont font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
	private PDFont fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

	private float fontSize = 8;
	private Color fillColor;
	private Color textColor = Color.BLACK;
	private final Row<T> row;
	private WrappingFunction wrappingFunction;
	private boolean isHeaderCell = false;
	private boolean isColspanCell = false;

	// default padding
	private float leftPadding = 5f;
	private float rightPadding = 5f;
	private float topPadding = 5f;
	private float bottomPadding = 5f;

	// default border
	private LineStyle leftBorderStyle = new LineStyle(Color.BLACK, 1);
	private LineStyle rightBorderStyle = new LineStyle(Color.BLACK, 1);
	private LineStyle topBorderStyle = new LineStyle(Color.BLACK, 1);
	private LineStyle bottomBorderStyle = new LineStyle(Color.BLACK, 1);

	private Paragraph paragraph = null;
	private float lineSpacing = 1;
	private boolean textRotated = false;

	private HorizontalAlignment align;
	private VerticalAlignment valign;

	float horizontalFreeSpace = 0;
	float verticalFreeSpace = 0;

	private final List<CellContentDrawnListener<T>> contentDrawnListenerList = new ArrayList<CellContentDrawnListener<T>>();

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
			double calculatedWidth = row.getWidth() * (width / 100);
			this.width = (float) calculatedWidth;
		} else {
			this.width = width;
		}

		if (getWidth() > row.getWidth()) {
			throw new IllegalArgumentException(
					"Cell Width=" + getWidth() + " can't be bigger than row width=" + row.getWidth());
		}
		//check if we have new default font
		if(!FontUtils.getDefaultfonts().isEmpty()){
			font = FontUtils.getDefaultfonts().get("font");
			fontBold = FontUtils.getDefaultfonts().get("fontBold");
		}
		this.text = text == null ? "" : text;
		this.align = align;
		this.valign = valign;
		this.wrappingFunction = null;
	}

	/**
	 * <p>
	 * Retrieves cell's text {@link Color}. Default color is black.
	 * </p>
	 *
	 * @return {@link Color} of the cell's text
	 */
	public Color getTextColor() {
		return textColor;
	}

	/**
	 * <p>
	 * Sets cell's text {@link Color}.
	 * </p>
	 *
	 * @param textColor
	 *            designated text {@link Color}
	 */
	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}

	/**
	 * <p>
	 * Gets fill (background) {@link Color} for the current cell.
	 * </p>
	 *
	 * @return Fill {@link Color} for the cell
	 */
	public Color getFillColor() {
		return fillColor;
	}

	/**
	 * <p>
	 * Sets fill (background) {@link Color} for the current cell.
	 * </p>
	 *
	 * @param fillColor
	 *            Fill {@link Color} for the cell
	 */
	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	/**
	 * <p>
	 * Gets cell's width.
	 * </p>
	 *
	 * @return Cell's width
	 */
	public float getWidth() {
		return width;
	}

	/**
	 * <p>
	 * Gets cell's width without (left,right) padding.
	 *
	 * @return Inner cell's width
	 */
	public float getInnerWidth() {
		return getWidth() - getLeftPadding() - getRightPadding()
				- (leftBorderStyle == null ? 0 : leftBorderStyle.getWidth())
				- (rightBorderStyle == null ? 0 : rightBorderStyle.getWidth());
	}

	/**
	 * <p>
	 * Gets cell's height without (top,bottom) padding.
	 *
	 * @return Inner cell's height
	 */
	public float getInnerHeight() {
		return getHeight() - getBottomPadding() - getTopPadding()
				- (topBorderStyle == null ? 0 : topBorderStyle.getWidth())
				- (bottomBorderStyle == null ? 0 : bottomBorderStyle.getWidth());
	}

	/**
	 * <p>
	 * Retrieves text from current cell
	 * </p>
	 *
	 * @return cell's text
	 */
	public String getText() {
		return text;
	}

	/**
	 * <p>
	 * Sets cell's text value
	 * </p>
	 *
	 * @param text
	 *            Text value of the cell
	 */
	public void setText(String text) {
		this.text = text;

		// paragraph invalidated
		paragraph = null;
	}

	/**
	 * <p>
	 * Gets appropriate {@link PDFont} for current cell.
	 * </p>
	 *
	 * @return {@link PDFont} for current cell
	 * @throws IllegalArgumentException
	 *             if <code>font</code> is not set.
	 */
	public PDFont getFont() {
		if (font == null) {
			throw new IllegalArgumentException("Font not set.");
		}
		if (isHeaderCell) {
			return fontBold;
		} else {
			return font;
		}
	}

	/**
	 * <p>
	 * Sets appropriate {@link PDFont} for current cell.
	 * </p>
	 *
	 * @param font
	 *            {@link PDFont} for current cell
	 */
	public void setFont(PDFont font) {
		this.font = font;

		// paragraph invalidated
		paragraph = null;
	}

	/**
	 * <p>
	 * Gets {@link PDFont} size for current cell (in points).
	 * </p>
	 *
	 * @return {@link PDFont} size for current cell (in points).
	 */
	public float getFontSize() {
		return fontSize;
	}

	/**
	 * <p>
	 * Sets {@link PDFont} size for current cell (in points).
	 * </p>
	 *
	 * @param fontSize
	 *            {@link PDFont} size for current cell (in points).
	 */
	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;

		// paragraph invalidated
		paragraph = null;
	}

	/**
	 * <p>
	 * Retrieves a valid {@link Paragraph} depending of cell's {@link PDFont}
	 * and value rotation.
	 * </p>
	 *
	 * <p>
	 * If cell has rotated value then {@link Paragraph} width is depending of
	 * {@link Cell#getInnerHeight()} otherwise {@link Cell#getInnerWidth()}
	 * </p>
	 *
	 *
	 * @return Cell's {@link Paragraph}
	 */
	public Paragraph getParagraph() {
		if (paragraph == null) {
			// if it is header cell then use font bold
			if (isHeaderCell) {
				if (isTextRotated()) {
					paragraph = new Paragraph(text, fontBold, fontSize, getInnerHeight(), align, textColor, null,
							wrappingFunction, lineSpacing);
				} else {
					paragraph = new Paragraph(text, fontBold, fontSize, getInnerWidth(), align, textColor, null,
							wrappingFunction, lineSpacing);
				}
			} else {
				if (isTextRotated()) {
					paragraph = new Paragraph(text, font, fontSize, getInnerHeight(), align, textColor, null,
							wrappingFunction, lineSpacing);
				} else {
					paragraph = new Paragraph(text, font, fontSize, getInnerWidth(), align, textColor, null,
							wrappingFunction, lineSpacing);
				}
			}
		}
		return paragraph;
	}

	public float getExtraWidth() {
		return this.row.getLastCellExtraWidth() + getWidth();
	}

	/**
	 * <p>
	 * Gets the cell's height according to {@link Row}'s height
	 * </p>
	 *
	 * @return {@link Row}'s height
	 */
	public float getHeight() {
		return row.getHeight();
	}

	/**
	 * <p>
	 * Gets the height of the single cell, opposed to {@link #getHeight()},
	 * which returns the row's height.
	 * </p>
	 * <p>
	 * Depending of rotated/normal cell's value there is two cases for
	 * calculation:
	 * </p>
	 * <ol>
	 * <li>Rotated value - cell's height is equal to overall text length in the
	 * cell with necessery paddings (top,bottom)</li>
	 * <li>Normal value - cell's height is equal to {@link Paragraph}'s height
	 * with necessery paddings (top,bottom)</li>
	 * </ol>
	 *
	 * @return Cell's height
	 * @throws IllegalStateException
	 *             if <code>font</code> is not set.
	 */
	public float getCellHeight() {
		if (height != null) {
			return height;
		}

		if (isTextRotated()) {
			try {
				// TODO: maybe find more optimal way then this
				return getFont().getStringWidth(getText()) / 1000 * getFontSize() + getTopPadding()
						+ (getTopBorder() == null ? 0 : getTopBorder().getWidth()) + getBottomPadding()
						+ (getBottomBorder() == null ? 0 : getBottomBorder().getWidth());
			} catch (final IOException e) {
				throw new IllegalStateException("Font not set.", e);
			}
		} else {
			return getTextHeight() + getTopPadding() + getBottomPadding()
					+ (getTopBorder() == null ? 0 : getTopBorder().getWidth())
					+ (getBottomBorder() == null ? 0 : getBottomBorder().getWidth());
		}
	}

	/**
	 * <p>
	 * Sets the height of the single cell.
	 * </p>
	 *
	 * @param height
	 *            Cell's height
	 */
	public void setHeight(final Float height) {
		this.height = height;
	}

	/**
	 * <p>
	 * Gets {@link Paragraph}'s height
	 * </p>
	 *
	 * @return {@link Paragraph}'s height
	 */
	public float getTextHeight() {
		return getParagraph().getHeight();
	}

	/**
	 * <p>
	 * Gets {@link Paragraph}'s width
	 * </p>
	 *
	 * @return {@link Paragraph}'s width
	 */
	public float getTextWidth() {
		return getParagraph().getWidth();
	}

	/**
	 * <p>
	 * Gets cell's left padding (in points).
	 * </p>
	 *
	 * @return Cell's left padding (in points).
	 */
	public float getLeftPadding() {
		return leftPadding;
	}

	/**
	 * <p>
	 * Sets cell's left padding (in points)
	 * </p>
	 *
	 * @param cellLeftPadding
	 *            Cell's left padding (in points).
	 */
	public void setLeftPadding(float cellLeftPadding) {
		this.leftPadding = cellLeftPadding;

		// paragraph invalidated
		paragraph = null;
	}

	/**
	 * <p>
	 * Gets cell's right padding (in points).
	 * </p>
	 *
	 * @return Cell's right padding (in points).
	 */
	public float getRightPadding() {
		return rightPadding;
	}

	/**
	 * <p>
	 * Sets cell's right padding (in points)
	 * </p>
	 *
	 * @param cellRightPadding
	 *            Cell's right padding (in points).
	 */
	public void setRightPadding(float cellRightPadding) {
		this.rightPadding = cellRightPadding;

		// paragraph invalidated
		paragraph = null;
	}

	/**
	 * <p>
	 * Gets cell's top padding (in points).
	 * </p>
	 *
	 * @return Cell's top padding (in points).
	 */
	public float getTopPadding() {
		return topPadding;
	}

	/**
	 * <p>
	 * Sets cell's top padding (in points)
	 * </p>
	 *
	 * @param cellTopPadding
	 *            Cell's top padding (in points).
	 */
	public void setTopPadding(float cellTopPadding) {
		this.topPadding = cellTopPadding;
	}

	/**
	 * <p>
	 * Gets cell's bottom padding (in points).
	 * </p>
	 *
	 * @return Cell's bottom padding (in points).
	 */
	public float getBottomPadding() {
		return bottomPadding;
	}

	/**
	 * <p>
	 * Sets cell's bottom padding (in points)
	 * </p>
	 *
	 * @param cellBottomPadding
	 *            Cell's bottom padding (in points).
	 */
	public void setBottomPadding(float cellBottomPadding) {
		this.bottomPadding = cellBottomPadding;
	}

	/**
	 * <p>
	 * Gets free vertical space of cell.
	 * </p>
	 *
	 * <p>
	 * If cell has rotated value then free vertical space is equal inner cell's
	 * height ({@link #getInnerHeight()}) subtracted to the longest line of
	 * rotated {@link Paragraph} otherwise it's just cell's inner height (
	 * {@link #getInnerHeight()}) subtracted with width of the normal
	 * {@link Paragraph}.
	 * </p>
	 *
	 * @return Free vertical space of the cell's.
	 */
	public float getVerticalFreeSpace() {
		if (isTextRotated()) {
			// need to calculate max line width so we just iterating through
			// lines
			for (String line : getParagraph().getLines()) {
			}
			return getInnerHeight() - getParagraph().getMaxLineWidth();
		} else {
			return getInnerHeight() - getTextHeight();
		}
	}

	/**
	 * <p>
	 * Gets free horizontal space of cell.
	 * </p>
	 *
	 * <p>
	 * If cell has rotated value then free horizontal space is equal cell's
	 * inner width ({@link #getInnerWidth()}) subtracted to the
	 * {@link Paragraph}'s height otherwise it's just cell's
	 * {@link #getInnerWidth()} subtracted with width of longest line in normal
	 * {@link Paragraph}.
	 * </p>
	 *
	 * @return Free vertical space of the cell's.
	 */
	public float getHorizontalFreeSpace() {
		if (isTextRotated()) {
			return getInnerWidth() - getTextHeight();
		} else {
			return getInnerWidth() - getParagraph().getMaxLineWidth();
		}
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

	public LineStyle getLeftBorder() {
		return leftBorderStyle;
	}

	public LineStyle getRightBorder() {
		return rightBorderStyle;
	}

	public LineStyle getTopBorder() {
		return topBorderStyle;
	}

	public LineStyle getBottomBorder() {
		return bottomBorderStyle;
	}

	public void setLeftBorderStyle(LineStyle leftBorder) {
		this.leftBorderStyle = leftBorder;
	}

	public void setRightBorderStyle(LineStyle rightBorder) {
		this.rightBorderStyle = rightBorder;
	}

	public void setTopBorderStyle(LineStyle topBorder) {
		this.topBorderStyle = topBorder;
	}

	public void setBottomBorderStyle(LineStyle bottomBorder) {
		this.bottomBorderStyle = bottomBorder;
	}

	/**
	 * <p>
	 * Easy setting for cell border style.
	 *
	 * @param border
	 *            It is {@link LineStyle} for all borders
	 * @see LineStyle Rendering line attributes
	 */
	public void setBorderStyle(LineStyle border) {
		this.leftBorderStyle = border;
		this.rightBorderStyle = border;
		this.topBorderStyle = border;
		this.bottomBorderStyle = border;
	}

	public boolean isTextRotated() {
		return textRotated;
	}

	public void setTextRotated(boolean textRotated) {
		this.textRotated = textRotated;
	}

	public PDFont getFontBold() {
		return fontBold;
	}

	/**
	 * <p>
	 * Sets the {@linkplain PDFont font} used for bold text, for example in
	 * {@linkplain #isHeaderCell() header cells}.
	 * </p>
	 *
	 * @param fontBold
	 *            The {@linkplain PDFont font} to use for bold text
	 */
	public void setFontBold(final PDFont fontBold) {
		this.fontBold = fontBold;
	}

	public boolean isColspanCell() {
		return isColspanCell;
	}

	public void setColspanCell(boolean isColspanCell) {
		this.isColspanCell = isColspanCell;
	}

	public void setAlign(HorizontalAlignment align) {
		this.align = align;
	}

	public void setValign(VerticalAlignment valign) {
		this.valign = valign;
	}

	/**
	 * <p>
	 * Copies the style of an existing cell to this cell
	 * </p>
	 *
	 * @param sourceCell Source {@link Cell} from which cell style will be copied.
	 */
	public void copyCellStyle(Cell sourceCell) {
		Boolean leftBorder = this.leftBorderStyle == null;
		setBorderStyle(sourceCell.getTopBorder());
		if (leftBorder) {
			this.leftBorderStyle = null;// if left border wasn't set, don't set
										// it now
		}
		this.font = sourceCell.getFont();// otherwise paragraph gets invalidated
		this.fontBold = sourceCell.getFontBold();
		this.fontSize = sourceCell.getFontSize();
		setFillColor(sourceCell.getFillColor());
		setTextColor(sourceCell.getTextColor());
		setAlign(sourceCell.getAlign());
		setValign(sourceCell.getValign());
	}

	/**
	 * <p>
	 * Compares the style of a cell with another cell
	 * </p>
	 *
	 * @param sourceCell Source {@link Cell} which will be used for style comparation
	 * @return boolean if source cell has the same style
	 */
	public Boolean hasSameStyle(Cell sourceCell) {
		if (!sourceCell.getTopBorder().equals(getTopBorder())) {
			return false;
		}
		if (!sourceCell.getFont().equals(getFont())) {
			return false;
		}
		if (!sourceCell.getFontBold().equals(getFontBold())) {
			return false;
		}
		if (!sourceCell.getFillColor().equals(getFillColor())) {
			return false;
		}
		if (!sourceCell.getTextColor().equals(getTextColor())) {
			return false;
		}
		if (!sourceCell.getAlign().equals(getAlign())) {
			return false;
		}
		if (!sourceCell.getValign().equals(getValign())) {
			return false;
		}
		return true;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getLineSpacing() {
		return lineSpacing;
	}

	public void setLineSpacing(float lineSpacing) {
		this.lineSpacing = lineSpacing;
	}

	public void addContentDrawnListener(CellContentDrawnListener<T> listener) {
		contentDrawnListenerList.add(listener);
	}

	public List<CellContentDrawnListener<T>> getCellContentDrawnListeners() {
		return contentDrawnListenerList;
	}

	public void notifyContentDrawnListeners(PDDocument document, PDPage page, PDRectangle rectangle) {
		for(CellContentDrawnListener<T> listener : getCellContentDrawnListeners()) {
			listener.onContentDrawn(this, document, page, rectangle);
		}
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}


}
