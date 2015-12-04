
/*
 Quodlibet.be
 */
package be.quodlibet.boxable;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import be.quodlibet.boxable.utils.FontUtils;
import be.quodlibet.boxable.utils.PDStreamUtils;

public class Paragraph {

	private float width = 500;
	private String text;
	private PDFont font;
	private float fontSize;
	private final WrappingFunction wrappingFunction;
	private HorizontalAlignment align;
	private TextType textType;

	private Color color;

	private boolean drawDebug;

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
		this.wrappingFunction = wrappingFunction;
	}

	public List<String> getLines() {
		List<String> result = new ArrayList<>();

		String[] split = wrappingFunction.getLines(text);

		int[] possibleWrapPoints = new int[split.length];

		possibleWrapPoints[0] = split[0].length();

		for (int i = 1; i < split.length; i++) {
			possibleWrapPoints[i] = possibleWrapPoints[i - 1] + split[i].length();
		}

		int start = 0;
		int end = 0;
		for (int i : possibleWrapPoints) {
			float width = 0;
			try {
				width = font.getStringWidth(text.substring(start, i)) / 1000 * fontSize;
			} catch (IOException e) {
				throw new IllegalArgumentException(e.getMessage(), e);
			}
			if (start < end && width > this.width) {
				result.add(text.substring(start, end));
				start = end;
			}
			end = i;
		}
		// Last piece of text
		result.add(text.substring(start));
		return result;
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

	public PDFont getFont() {
		return font;
	}

	public float getFontSize() {
		return fontSize;
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
}
