
/*
 Quodlibet.be
 */
package be.quodlibet.boxable;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import be.quodlibet.boxable.text.PipelineLayer;
import be.quodlibet.boxable.text.Token;
import be.quodlibet.boxable.text.Tokenizer;
import be.quodlibet.boxable.text.WrappingFunction;
import be.quodlibet.boxable.utils.FontUtils;
import be.quodlibet.boxable.utils.PDStreamUtils;

public class Paragraph {

	private float width = 500;
	private String text;
	private float fontSize;
	private PDFont font = PDType1Font.HELVETICA;
	private PDFont fontBold = PDType1Font.HELVETICA_BOLD;
	private PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;
	private PDFont fontBoldItalic = PDType1Font.HELVETICA_BOLD_OBLIQUE;
	private final WrappingFunction wrappingFunction;
	private HorizontalAlignment align;
	private TextType textType;

	private Color color;
	
	private boolean drawDebug;
	private final Map<Integer, Float> lineWidths = new HashMap<>();
	private float maxLineWidth;
	
	public Paragraph(String text, PDFont font, float fontSize, float width, final HorizontalAlignment align) {
		this(text, font, fontSize, width, align, null);
	}

	private static final WrappingFunction DEFAULT_WRAP_FUNC = new WrappingFunction() {
		@Override
		public String[] getLines(String t) {
			return t.split("(?<=\\s|-|@|,|\\.|:|;)");
		}
	};

	public Paragraph(String text, PDFont font, int fontSize, int width) {
		this(text, font, fontSize, width, HorizontalAlignment.LEFT, null);
	}

	public Paragraph(String text, PDFont font, float fontSize, float width, final HorizontalAlignment align,
			WrappingFunction wrappingFunction) {
		this(text, font, fontSize, width, align, Color.BLACK, (TextType) null, wrappingFunction);
	}

	public Paragraph(String text, PDFont font, float fontSize, float width, final HorizontalAlignment align,
			final Color color, final TextType textType, WrappingFunction wrappingFunction) {
		this.color = color;
		this.text = text;
		this.font = font;
		this.fontSize = fontSize;
		this.width = width;
		this.textType = textType;
		this.setAlign(align);
		this.wrappingFunction = wrappingFunction == null ? DEFAULT_WRAP_FUNC : wrappingFunction;
	}

	public List<String> getLines() {
		final List<String> result = new ArrayList<>();
		final List<Token> tokens = Tokenizer.tokenize(text, wrappingFunction);
		
		int lineCounter = 0; 
		boolean italic = false;
		boolean bold = false;
		PDFont currentFont = font;
		
		final PipelineLayer textInLine = new PipelineLayer();
		final PipelineLayer sinceLastWrapPoint = new PipelineLayer();
		
		for (final Token token : tokens) {
			switch (token.getType()) {
			case OPEN_TAG:
				if (isBold(token)) {
					bold = true;
					currentFont = getFont(bold, italic);
				} else if (isItalic(token)) {
					italic = true;
					currentFont = getFont(bold, italic);
				}
				break;
			case CLOSE_TAG:
				if (isBold(token)) {
					bold = false;
					currentFont = getFont(bold, italic);
				} else if (isItalic(token)) {
					italic = false;
					currentFont = getFont(bold, italic);
				}
				break;
			case POSSIBLE_WRAP_POINT:
				if (textInLine.width() + sinceLastWrapPoint.trimmedWidth() > width) {
					// this is our line
					result.add(textInLine.trimmedText());
					lineWidths.put(lineCounter, textInLine.trimmedWidth());
					lineCounter++;
					textInLine.reset();
					
					// wrapping at last wrap point
					textInLine.push(sinceLastWrapPoint);
				} else {
					textInLine.push(sinceLastWrapPoint);
				}
				break;
			case WRAP_POINT:
				// this is our line
				result.add(textInLine.trimmedText());
				lineWidths.put(lineCounter, textInLine.trimmedWidth());
				lineCounter++;
				textInLine.reset();
				
				// wrapping at last wrap point
				textInLine.push(sinceLastWrapPoint);
				break;
			case TEXT:
				try {
					sinceLastWrapPoint.push(token.getData(), currentFont, fontSize);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
		
		if (sinceLastWrapPoint.trimmedWidth() + textInLine.trimmedWidth() > 0) {
			result.add(textInLine.trimmedText());
			lineWidths.put(lineCounter, textInLine.trimmedWidth());
		}
		
		return result;
	}

	private boolean isItalic(final Token token) {
		return "i".equals(token.getData());
	}

	private boolean isBold(final Token token) {
		return "b".equals(token.getData());
	}
	
	private PDFont getFont(boolean isBold, boolean isItalic) {
		if (isBold) {
			if (isItalic) {
				return fontBoldItalic;
			} else {
				return fontBold;
			}
		} else if (isItalic) {
			return fontItalic;
		} else {
			return font;
		}
	}

	public float write(final PDPageContentStream stream, float cursorX, float cursorY) {
		if (drawDebug) {
			PDStreamUtils.rectFontMetrics(stream, cursorX, cursorY, font, fontSize);

			// width
			PDStreamUtils.rect(stream, cursorX, cursorY, width, 1, Color.RED);
		}

		for (String line : getLines()) {
			line = line.trim();

			float textX = cursorX;
			switch (align) {
			case CENTER:
				textX += getHorizontalFreeSpace(line) / 2;
				break;
			case LEFT:
				break;
			case RIGHT:
				textX += getHorizontalFreeSpace(line);
				break;
			}

			PDStreamUtils.write(stream, line, font, fontSize, textX, cursorY, color);

			if (textType != null) {
				switch (textType) {
				case HIGHLIGHT:
				case SQUIGGLY:
				case STRIKEOUT:
					throw new UnsupportedOperationException("Not implemented.");
				case UNDERLINE:
					float y = (float) (cursorY - FontUtils.getHeight(font, fontSize)
							- FontUtils.getDescent(font, fontSize) - 1.5);
					try {
						float titleWidth = font.getStringWidth(line) / 1000 * fontSize;
						stream.moveTo(textX, y);
						stream.lineTo(textX + titleWidth, y);
						stream.stroke();
					} catch (final IOException e) {
						throw new IllegalStateException("Unable to underline text", e);
					}
					break;
				default:
					break;
				}
			}

			// move one "line" down
			cursorY -= getFontHeight();
		}

		return cursorY;
	}

	public float getHeight() {
		return getLines().size() * getFontHeight();
	}

	public float getFontHeight() {
		return FontUtils.getHeight(font, fontSize);
	}

	/**
	 * @deprecated This method will be removed in a future release
	 */
	@Deprecated
	public float getFontWidth() {
		return font.getFontDescriptor().getFontBoundingBox().getWidth() / 1000 * fontSize;
	}

	/**
	 * @deprecated This method will be removed in a future release
	 */
	@Deprecated
	public Paragraph withWidth(int width) {
		this.width = width;
		return this;
	}

	/**
	 * @deprecated This method will be removed in a future release
	 */
	@Deprecated
	public Paragraph withFont(PDFont font, int fontSize) {
		this.font = font;
		this.fontSize = fontSize;
		return this;
	}

	/**
	 * @deprecated This method will be removed in a future release
	 */
	@Deprecated
	public Paragraph withColor(int color) {
		this.color = new Color(color);
		return this;
	}

	/**
	 * @deprecated This method will be replaced by
	 *             {@code public Color getColor()} in a future release
	 */
	@Deprecated
	public int getColor() {
		return color.getRGB();
	}

	private float getHorizontalFreeSpace(final String text) {
		try {
			final float tw = font.getStringWidth(text.trim()) / 1000 * fontSize;
			return width - tw;
		} catch (IOException e) {
			throw new IllegalStateException("Unable to calculate text width", e);
		}
	}

	public float getWidth() {
		return width;
	}

	public String getText() {
		return text;
	}

	public float getFontSize() {
		return fontSize;
	}
	
	public PDFont getFont() {
        return font;
    }

	public HorizontalAlignment getAlign() {
		return align;
	}

	public void setAlign(HorizontalAlignment align) {
		this.align = align;
	}

	public boolean isDrawDebug() {
		return drawDebug;
	}

	public void setDrawDebug(boolean drawDebug) {
		this.drawDebug = drawDebug;
	}

	public WrappingFunction getWrappingFunction() {
		return wrappingFunction;
	}

	public float getMaxLineWidth() {
		return maxLineWidth;
	}

	public float getLineWidth(int key) {
		return lineWidths.get(key);
	}

}
