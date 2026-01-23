
/*
 Quodlibet.be
 */
package be.quodlibet.boxable;

import java.awt.Color;
import java.net.URL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger logger = LoggerFactory.getLogger(Cell.class);

	private float width;
	private Float height;
	private String text;

	private URL url = null;

	private static final PDFont DEFAULT_FONT = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
	private static final PDFont DEFAULT_FONT_BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

	private PDFont font = DEFAULT_FONT;
	private PDFont fontBold = DEFAULT_FONT_BOLD;

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
	
	private static final LineStyle DEFAULT_BORDER = new LineStyle(Color.BLACK, 1);

	// default border
	private LineStyle leftBorderStyle = DEFAULT_BORDER;
	private LineStyle rightBorderStyle = DEFAULT_BORDER;
	private LineStyle topBorderStyle = DEFAULT_BORDER;
	private LineStyle bottomBorderStyle = DEFAULT_BORDER;

	private Paragraph paragraph = null;
	private float lineSpacing = 1;
	private boolean textRotated = false;

	private HorizontalAlignment align;
	private VerticalAlignment valign;
    
    private int lineStart = 0;

	float horizontalFreeSpace = 0;
	float verticalFreeSpace = 0;

	private List<CellContentDrawnListener<T>> contentDrawnListenerList = null;

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
		float innerHeight = getHeight() - getBottomPadding() - getTopPadding()
				- (topBorderStyle == null ? 0 : topBorderStyle.getWidth())
				- (bottomBorderStyle == null ? 0 : bottomBorderStyle.getWidth());
		if (innerHeight < 0) {
			logger.warn("Cell inner height is negative ({}). rowHeight={}, topPadding={}, bottomPadding={}, topBorder={}, bottomBorder={}",
					innerHeight,
					getHeight(),
					getTopPadding(),
					getBottomPadding(),
					(topBorderStyle == null ? 0 : topBorderStyle.getWidth()),
					(bottomBorderStyle == null ? 0 : bottomBorderStyle.getWidth()));
		}
		return innerHeight;
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

	void fitFontSizeToHeight(float rowHeight) {
		if (isTextRotated()) {
			return;
		}
		if (text == null || text.isEmpty()) {
			return;
		}
		float availableHeight = rowHeight - getTopPadding() - getBottomPadding()
				- (getTopBorder() == null ? 0 : getTopBorder().getWidth())
				- (getBottomBorder() == null ? 0 : getBottomBorder().getWidth());
		if (availableHeight <= 0) {
			return;
		}
		if (getRequiredTextHeight() <= availableHeight) {
			return;
		}

		float minFontSize = 1f;
		float maxFontSize = fontSize;
		float bestFit = minFontSize;

		for (int i = 0; i < 10; i++) {
			float mid = (minFontSize + maxFontSize) / 2f;
			setFontSize(mid);
			float textHeight = getRequiredTextHeight();
			if (textHeight <= availableHeight) {
				bestFit = mid;
				minFontSize = mid;
			} else {
				maxFontSize = mid;
			}
		}

		setFontSize(bestFit);
		getTextHeight();
	}

	private float getRequiredTextHeight() {
		Paragraph currentParagraph = getParagraph();
		float descent = Math.abs(FontUtils.getDescent(currentParagraph.getFont(), getFontSize()));
		return getTextHeight() + descent;
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

	/**
	 * <p>
	 * Clears the cached paragraph so it will be rebuilt on next access.
	 * </p>
	 */
	public void clearParagraph() {
		this.paragraph = null;
	}

	/**
	 * <p>
	 * Returns this cell's width including any extra width reserved for the last
	 * column.
	 * </p>
	 *
	 * @return Cell width including extra width
	 */
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
		return getParagraph().getHeight(lineStart);
	}

	/**
	 * <p>
	 * Returns the current line start index used when a row is split.
	 * </p>
	 *
	 * @return Line start index
	 */
    public int getLineStart() {
        return lineStart;
    }

	/**
	 * <p>
	 * Sets the line start index used when a row is split across pages.
	 * </p>
	 *
	 * @param lineStart
	 *            Line start index
	 */
    public void setLineStart(int lineStart) {
        this.lineStart = lineStart;
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

	/**
	 * <p>
	 * Returns the horizontal alignment of this cell.
	 * </p>
	 *
	 * @return Horizontal alignment
	 */
	public HorizontalAlignment getAlign() {
		return align;
	}

	/**
	 * <p>
	 * Returns the vertical alignment of this cell.
	 * </p>
	 *
	 * @return Vertical alignment
	 */
	public VerticalAlignment getValign() {
		return valign;
	}

	/**
	 * <p>
	 * Returns whether this cell is a header cell.
	 * </p>
	 *
	 * @return {@code true} when header cell
	 */
	public boolean isHeaderCell() {
		return isHeaderCell;
	}

	/**
	 * <p>
	 * Marks this cell as a header cell, which uses the bold font by default.
	 * </p>
	 *
	 * @param isHeaderCell
	 *            {@code true} to mark as header cell
	 */
	public void setHeaderCell(boolean isHeaderCell) {
		this.isHeaderCell = isHeaderCell;
	}

	/**
	 * <p>
	 * Returns the wrapping function used for line breaks. Defaults to the
	 * built-in wrapping implementation when none is set.
	 * </p>
	 *
	 * @return Wrapping function
	 */
	public WrappingFunction getWrappingFunction() {
		return getParagraph().getWrappingFunction();
	}

	/**
	 * <p>
	 * Sets the wrapping function used to tokenize and wrap text.
	 * </p>
	 *
	 * @param wrappingFunction
	 *            Wrapping function
	 */
	public void setWrappingFunction(WrappingFunction wrappingFunction) {
		this.wrappingFunction = wrappingFunction;

		// paragraph invalidated
		paragraph = null;
	}

	/**
	 * <p>
	 * Returns the left border style for this cell, or {@code null} if none.
	 * </p>
	 *
	 * @return Left border style
	 */
	public LineStyle getLeftBorder() {
		return leftBorderStyle;
	}

	/**
	 * <p>
	 * Returns the right border style for this cell, or {@code null} if none.
	 * </p>
	 *
	 * @return Right border style
	 */
	public LineStyle getRightBorder() {
		return rightBorderStyle;
	}

	/**
	 * <p>
	 * Returns the top border style for this cell, or {@code null} if none.
	 * </p>
	 *
	 * @return Top border style
	 */
	public LineStyle getTopBorder() {
		return topBorderStyle;
	}

	/**
	 * <p>
	 * Returns the bottom border style for this cell, or {@code null} if none.
	 * </p>
	 *
	 * @return Bottom border style
	 */
	public LineStyle getBottomBorder() {
		return bottomBorderStyle;
	}

	/**
	 * <p>
	 * Sets the left border style for this cell.
	 * </p>
	 *
	 * @param leftBorder
	 *            Left border style
	 */
	public void setLeftBorderStyle(LineStyle leftBorder) {
		this.leftBorderStyle = leftBorder;
	}

	/**
	 * <p>
	 * Sets the right border style for this cell.
	 * </p>
	 *
	 * @param rightBorder
	 *            Right border style
	 */
	public void setRightBorderStyle(LineStyle rightBorder) {
		this.rightBorderStyle = rightBorder;
	}

	/**
	 * <p>
	 * Sets the top border style for this cell.
	 * </p>
	 *
	 * @param topBorder
	 *            Top border style
	 */
	public void setTopBorderStyle(LineStyle topBorder) {
		this.topBorderStyle = topBorder;
	}

	/**
	 * <p>
	 * Sets the bottom border style for this cell.
	 * </p>
	 *
	 * @param bottomBorder
	 *            Bottom border style
	 */
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

	/**
	 * <p>
	 * Returns whether the text is rotated.
	 * </p>
	 *
	 * @return {@code true} when text is rotated
	 */
	public boolean isTextRotated() {
		return textRotated;
	}

	/**
	 * <p>
	 * Sets whether the text is rotated. Default is {@code false}.
	 * </p>
	 *
	 * @param textRotated
	 *            {@code true} to rotate text
	 */
	public void setTextRotated(boolean textRotated) {
		this.textRotated = textRotated;
	}

	/**
	 * <p>
	 * Returns the font used for bold text, such as in header cells.
	 * </p>
	 *
	 * @return Bold font
	 */
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

	/**
	 * <p>
	 * Returns whether this cell spans multiple columns.
	 * </p>
	 *
	 * @return {@code true} when cell spans multiple columns
	 */
	public boolean isColspanCell() {
		return isColspanCell;
	}

	/**
	 * <p>
	 * Marks this cell as a colspan cell.
	 * </p>
	 *
	 * @param isColspanCell
	 *            {@code true} when cell spans multiple columns
	 */
	public void setColspanCell(boolean isColspanCell) {
		this.isColspanCell = isColspanCell;
	}

	/**
	 * <p>
	 * Sets the horizontal alignment for this cell.
	 * </p>
	 *
	 * @param align
	 *            Horizontal alignment
	 */
	public void setAlign(HorizontalAlignment align) {
		this.align = align;
	}

	/**
	 * <p>
	 * Sets the vertical alignment for this cell.
	 * </p>
	 *
	 * @param valign
	 *            Vertical alignment
	 */
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
	public void copyCellStyle(Cell<?> sourceCell) {
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
	public Boolean hasSameStyle(Cell<?> sourceCell) {
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

	/**
	 * <p>
	 * Sets the absolute width of this cell in points.
	 * </p>
	 *
	 * @param width
	 *            Cell width in points
	 */
	public void setWidth(float width) {
		this.width = width;
	}

	/**
	 * <p>
	 * Returns the line spacing multiplier for this cell. Default is {@code 1}.
	 * </p>
	 *
	 * @return Line spacing multiplier
	 */
	public float getLineSpacing() {
		return lineSpacing;
	}

	/**
	 * <p>
	 * Sets the line spacing multiplier for this cell.
	 * </p>
	 *
	 * @param lineSpacing
	 *            Line spacing multiplier
	 */
	public void setLineSpacing(float lineSpacing) {
		this.lineSpacing = lineSpacing;
	}

	/**
	 * <p>
	 * Registers a listener that is notified after this cell's content is drawn.
	 * </p>
	 *
	 * @param listener
	 *            Content drawn listener
	 */
	public void addContentDrawnListener(CellContentDrawnListener<T> listener) {
		if (contentDrawnListenerList == null) {
			contentDrawnListenerList = new ArrayList<CellContentDrawnListener<T>>();
		}
		contentDrawnListenerList.add(listener);
	}

	/**
	 * <p>
	 * Returns the registered content drawn listeners.
	 * </p>
	 *
	 * @return List of listeners (possibly empty)
	 */
	public List<CellContentDrawnListener<T>> getCellContentDrawnListeners() {
		if (contentDrawnListenerList == null) {
			return Collections.emptyList();
		}
		return contentDrawnListenerList;
	}

	/**
	 * <p>
	 * Notifies all content drawn listeners for this cell.
	 * </p>
	 *
	 * @param document
	 *            Document being rendered
	 * @param page
	 *            Page being rendered
	 * @param rectangle
	 *            Content bounds for this cell
	 */
	public void notifyContentDrawnListeners(PDDocument document, PDPage page, PDRectangle rectangle) {
		for(CellContentDrawnListener<T> listener : getCellContentDrawnListeners()) {
			listener.onContentDrawn(this, document, page, rectangle);
		}
	}

	/**
	 * <p>
	 * Returns the URL associated with this cell, if any.
	 * </p>
	 *
	 * @return URL or {@code null}
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * <p>
	 * Sets the URL associated with this cell.
	 * </p>
	 *
	 * @param url
	 *            URL to associate
	 */
	public void setUrl(URL url) {
		this.url = url;
	}

	/**
	 * <p>
	 * Returns the row that owns this cell.
	 * </p>
	 *
	 * @return Parent row
	 */
	public Row<T> getRow() {
		return row;
	}
}
