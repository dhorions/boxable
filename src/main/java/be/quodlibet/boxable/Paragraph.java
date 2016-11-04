
/*
 Quodlibet.be
 */
package be.quodlibet.boxable;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import be.quodlibet.boxable.text.PipelineLayer;
import be.quodlibet.boxable.text.Token;
import be.quodlibet.boxable.text.TokenType;
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
	private float lineSpacing;

	private final static int DEFAULT_TAB = 4;
	private final static int DEFAULT_TAB_AND_BULLET = 6;
	private final static int BULLET_SPACE = 2;

	private boolean drawDebug;
	private final Map<Integer, Float> lineWidths = new HashMap<>();
	private Map<Integer, List<Token>> mapLineTokens = new LinkedHashMap<>();
	private float maxLineWidth = Integer.MIN_VALUE;

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
		this(text, font, fontSize, width, align, color, textType, wrappingFunction, 1);
	}
	
	public Paragraph(String text, PDFont font, float fontSize, float width, final HorizontalAlignment align,
			final Color color, final TextType textType, WrappingFunction wrappingFunction, float lineSpacing) {
		this.color = color;
		this.text = text;
		this.font = font;
		this.fontSize = fontSize;
		this.width = width;
		this.textType = textType;
		this.setAlign(align);
		this.wrappingFunction = wrappingFunction == null ? DEFAULT_WRAP_FUNC : wrappingFunction;
		this.lineSpacing = lineSpacing;
	}

	public List<String> getLines() {
		final List<String> result = new ArrayList<>();
		final List<Token> tokens = Tokenizer.tokenize(text, wrappingFunction);

		int lineCounter = 0;
		boolean italic = false;
		boolean bold = false;
		boolean listElement = false;
		PDFont currentFont = font;
		int orderListElement = 1;
		int numberOfOrderedLists = 0;
		int listLevel = 0;
		Stack<HTMLListNode> stack= new Stack<>();
		
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
				} else if (isList(token)) {
					listLevel++;
					if (token.getData().equals("ol")) {
						numberOfOrderedLists++;
						if(listLevel > 1){
							stack.add(new HTMLListNode(orderListElement-1, stack.isEmpty() ? String.valueOf("1.") : stack.peek().getValue() + String.valueOf(orderListElement-1) + "."));
						}
						orderListElement = 1;

						textInLine.push(sinceLastWrapPoint);
						// check if you have some text before this list, if you don't then you really don't need extra line break for that
						if (textInLine.trimmedWidth() > 0) {
							// this is our line
							result.add(textInLine.trimmedText());
							lineWidths.put(lineCounter, textInLine.trimmedWidth());
							mapLineTokens.put(lineCounter, textInLine.tokens());
							maxLineWidth = Math.max(maxLineWidth, textInLine.trimmedWidth());
							textInLine.reset();
							lineCounter++;
						}
					} else if (token.getData().equals("ul")) {
						textInLine.push(sinceLastWrapPoint);
						// check if you have some text before this list, if you don't then you really don't need extra line break for that
						if (textInLine.trimmedWidth() > 0) {
							// this is our line
							result.add(textInLine.trimmedText());
							lineWidths.put(lineCounter, textInLine.trimmedWidth());
							mapLineTokens.put(lineCounter, textInLine.tokens());
							maxLineWidth = Math.max(maxLineWidth, textInLine.trimmedWidth());
							textInLine.reset();
							lineCounter++;
						}
					}
				}
				sinceLastWrapPoint.push(token);
				break;
			case CLOSE_TAG:
				if (isBold(token)) {
					bold = false;
					currentFont = getFont(bold, italic);
					sinceLastWrapPoint.push(token);
				} else if (isItalic(token)) {
					italic = false;
					currentFont = getFont(bold, italic);
					sinceLastWrapPoint.push(token);
				} else if (isList(token)) {
					listLevel--;
					if (token.getData().equals("ol")) {
						numberOfOrderedLists--;
						// reset elements
						orderListElement = stack.peek().getOrderingNumber()+1;
						if(numberOfOrderedLists > 1){
							stack.pop();
						}
					}
					// ensure extra space after each lists
					// no need to worry about current line text because last closing <li> tag already done that
					if(listLevel == 0){
						result.add(" ");
						lineWidths.put(lineCounter, 0.0f);
						mapLineTokens.put(lineCounter, new ArrayList<Token>());
						lineCounter++;
					}
				} else if (isListElement(token)) {
					if(!getAlign().equals(HorizontalAlignment.LEFT)) {
						listLevel = 0;
					}
					// wrap at last wrap point?
					if (textInLine.width() + sinceLastWrapPoint.trimmedWidth() > width) {
						// this is our line
						result.add(textInLine.trimmedText());
						lineWidths.put(lineCounter, textInLine.trimmedWidth());
						mapLineTokens.put(lineCounter, textInLine.tokens());
						maxLineWidth = Math.max(maxLineWidth, textInLine.trimmedWidth());
						textInLine.reset();
						lineCounter++;
						// wrapping at last wrap point
						if (numberOfOrderedLists>0) {
							String orderingNumber = stack.isEmpty() ? String.valueOf(orderListElement) + "." : stack.pop().getValue() + ".";
							stack.add(new HTMLListNode(orderListElement, orderingNumber));
							String tab = String.valueOf(indentLevel(DEFAULT_TAB));
							String orderingNumberAndTab = orderingNumber + tab;
							try {
								textInLine.push(currentFont, fontSize, new Token(TokenType.PADDING, String
										.valueOf(font.getStringWidth(orderingNumberAndTab) / 1000 * getFontSize())));
							} catch (IOException e) {
								e.printStackTrace();
							}
							orderListElement++;
						} else {
							try {
								// if it's not left aligned then ignore list and list element and deal with it as normal text where <li> mimic <br> behaviour
								String tabBullet = getAlign().equals(HorizontalAlignment.LEFT) ? indentLevel(DEFAULT_TAB*Math.max(listLevel - 1, 0)) + indentLevel(DEFAULT_TAB_AND_BULLET) : indentLevel(DEFAULT_TAB);
								textInLine.push(currentFont, fontSize, new Token(TokenType.PADDING,
										String.valueOf(font.getStringWidth(tabBullet) / 1000 * getFontSize())));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						textInLine.push(sinceLastWrapPoint);
					}
					// wrapping at this must-have wrap point
					textInLine.push(sinceLastWrapPoint);
					// this is our line
					result.add(textInLine.trimmedText());
					lineWidths.put(lineCounter, textInLine.trimmedWidth());
					mapLineTokens.put(lineCounter, textInLine.tokens());
					maxLineWidth = Math.max(maxLineWidth, textInLine.trimmedWidth());
					textInLine.reset();
					lineCounter++;
					listElement = false;
				}
				if (isParagraph(token)) {
					if (textInLine.width() + sinceLastWrapPoint.trimmedWidth() > width) {
						// this is our line
						result.add(textInLine.trimmedText());
						lineWidths.put(lineCounter, textInLine.trimmedWidth());
						maxLineWidth = Math.max(maxLineWidth, textInLine.trimmedWidth());
						mapLineTokens.put(lineCounter, textInLine.tokens());
						lineCounter++;
						textInLine.reset();
					}
					// wrapping at this must-have wrap point
					textInLine.push(sinceLastWrapPoint);
					// this is our line
					result.add(textInLine.trimmedText());
					lineWidths.put(lineCounter, textInLine.trimmedWidth());
					mapLineTokens.put(lineCounter, textInLine.tokens());
					maxLineWidth = Math.max(maxLineWidth, textInLine.trimmedWidth());
					textInLine.reset();
					lineCounter++;
					
					// extra spacing because it's a paragraph
					result.add(" ");
					lineWidths.put(lineCounter, 0.0f);
					mapLineTokens.put(lineCounter, new ArrayList<Token>());
					lineCounter++;
				}
				break;
			case POSSIBLE_WRAP_POINT:
				if (textInLine.width() + sinceLastWrapPoint.trimmedWidth() > width) {
					// this is our line
					if (!textInLine.isEmpty()) {
						result.add(textInLine.trimmedText());
						lineWidths.put(lineCounter, textInLine.trimmedWidth());
						maxLineWidth = Math.max(maxLineWidth, textInLine.trimmedWidth());
						mapLineTokens.put(lineCounter, textInLine.tokens());
						lineCounter++;
						textInLine.reset();
					}
					// wrapping at last wrap point
					if (listElement) {
						if(!getAlign().equals(HorizontalAlignment.LEFT)) {
							listLevel = 0;
						}
						if (numberOfOrderedLists>0) {
							String tab = getAlign().equals(HorizontalAlignment.LEFT) ? indentLevel(DEFAULT_TAB*Math.max(listLevel - 1, 0)) : indentLevel(DEFAULT_TAB);
							String orderingNumber = stack.isEmpty() ? String.valueOf(orderListElement) + "." : stack.peek().getValue() + "." + String.valueOf(orderListElement-1) + ".";
							try {
								textInLine.push(currentFont, fontSize, new Token(TokenType.PADDING,
										String.valueOf(font.getStringWidth(tab+orderingNumber) / 1000 * getFontSize())));
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							try {
								// if it's not left aligned then ignore list and list element and deal with it as normal text where <li> mimic <br> behavior
								String tabBullet = getAlign().equals(HorizontalAlignment.LEFT) ? indentLevel(DEFAULT_TAB*Math.max(listLevel - 1, 0)) + indentLevel(BULLET_SPACE)  : indentLevel(DEFAULT_TAB);
								textInLine.push(currentFont, fontSize, new Token(TokenType.PADDING,
										String.valueOf(font.getStringWidth(tabBullet) / 1000 * getFontSize())));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					textInLine.push(sinceLastWrapPoint);
				} else {
					textInLine.push(sinceLastWrapPoint);
				}
				break;
			case WRAP_POINT:
				// wrap at last wrap point?
				if (textInLine.width() + sinceLastWrapPoint.trimmedWidth() > width) {
					// this is our line
					result.add(textInLine.trimmedText());
					lineWidths.put(lineCounter, textInLine.trimmedWidth());
					mapLineTokens.put(lineCounter, textInLine.tokens());
					maxLineWidth = Math.max(maxLineWidth, textInLine.trimmedWidth());
					textInLine.reset();
					lineCounter++;
					// wrapping at last wrap point
					if (listElement) {
						if(!getAlign().equals(HorizontalAlignment.LEFT)) {
							listLevel = 0;
						}
						if (numberOfOrderedLists>0) {
//							String orderingNumber = String.valueOf(orderListElement) + ". ";
							String orderingNumber = stack.isEmpty() ? String.valueOf("1") + "." : stack.pop().getValue() + ". ";
							String tab = String.valueOf(indentLevel(DEFAULT_TAB));
							String orderingNumberAndTab = orderingNumber + tab;
							try {
								textInLine.push(currentFont, fontSize, new Token(TokenType.PADDING, String
										.valueOf(font.getStringWidth(orderingNumberAndTab) / 1000 * getFontSize())));
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							try {
								// if it's not left aligned then ignore list and list element and deal with it as normal text where <li> mimic <br> behaviour
								String tabBullet = getAlign().equals(HorizontalAlignment.LEFT) ? indentLevel(DEFAULT_TAB*Math.max(listLevel - 1, 0)) + indentLevel(DEFAULT_TAB_AND_BULLET) : indentLevel(DEFAULT_TAB);
								textInLine.push(currentFont, fontSize, new Token(TokenType.PADDING,
										String.valueOf(font.getStringWidth(tabBullet) / 1000 * getFontSize())));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					textInLine.push(sinceLastWrapPoint);
				}
				if (isParagraph(token)) {
					// check if you have some text before this paragraph, if you don't then you really don't need extra line break for that
					if (textInLine.trimmedWidth() > 0) {
						// extra spacing because it's a paragraph
						result.add(" ");
						lineWidths.put(lineCounter, 0.0f);
						mapLineTokens.put(lineCounter, new ArrayList<Token>());
						lineCounter++;
					}
				} else if (isListElement(token)) {
					if(!getAlign().equals(HorizontalAlignment.LEFT)) {
						listLevel = 0;
					}
					listElement = true;
					// token padding, token bullet
					try {
						// if it's not left aligned then ignore list and list element and deal with it as normal text where <li> mimic <br> behaviour
						String tab = getAlign().equals(HorizontalAlignment.LEFT) ? indentLevel(DEFAULT_TAB*Math.max(listLevel - 1, 0)) : indentLevel(DEFAULT_TAB);
						textInLine.push(currentFont, fontSize, new Token(TokenType.PADDING,
								String.valueOf(font.getStringWidth(tab) / 1000 * getFontSize())));
						if (numberOfOrderedLists>0) {
							// if it's ordering list then move depending on your: ordering number + ". "
							String orderingNumber;
							if(listLevel > 1){
								orderingNumber = stack.peek().getValue() + String.valueOf(orderListElement) + ". "; 
							} else {
								orderingNumber = String.valueOf(orderListElement) + ". ";
							}
							textInLine.push(currentFont, fontSize, new Token(TokenType.ORDERING, orderingNumber));
							orderListElement++;
						} else {
							// if it's unordered list then just move by bullet character (take care of alignment!)
							if(getAlign().equals(HorizontalAlignment.LEFT)){
								textInLine.push(currentFont, fontSize, new Token(TokenType.BULLET, " "));
							} 
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					// wrapping at this must-have wrap point
					textInLine.push(sinceLastWrapPoint);
					result.add(textInLine.trimmedText());
					lineWidths.put(lineCounter, textInLine.trimmedWidth());
					mapLineTokens.put(lineCounter, textInLine.tokens());
					maxLineWidth = Math.max(maxLineWidth, textInLine.trimmedWidth());
					textInLine.reset();
					lineCounter++;
				}
				break;
			case TEXT:
				try {
					String word = token.getData();
					if(font.getStringWidth(word) / 1000f * fontSize > width && width > font.getAverageFontWidth() / 1000f * fontSize) {
						// you need to check if you have already something in your line 
						boolean alreadyTextInLine = false;
						if(textInLine.trimmedWidth()>0){
							alreadyTextInLine = true;
						}
						while (font.getStringWidth(word) / 1000f * fontSize > width) {
						float width = 0;
						float firstPartWordWidth = 0;
						float restOfTheWordWidth = 0;
						String lastTextToken = word;
						StringBuilder firstPartOfWord = new StringBuilder();
						StringBuilder restOfTheWord = new StringBuilder();
						for (int i = 0; i < lastTextToken.length(); i++) {
							char c = lastTextToken.charAt(i);
							try {
								width += (font.getStringWidth("" + c) / 1000f * fontSize);
							} catch (IOException e) {
								e.printStackTrace();
							}
							if(alreadyTextInLine){
								if (width < this.width - textInLine.trimmedWidth()) {
									firstPartOfWord.append("" + c);
									firstPartWordWidth = Math.max(width, firstPartWordWidth);
								} else {
									restOfTheWord.append("" + c);
									restOfTheWordWidth = Math.max(width, restOfTheWordWidth);
								}
							} else {
								if (width < this.width) {
									firstPartOfWord.append("" + c);
									firstPartWordWidth = Math.max(width, firstPartWordWidth);
								} else {
									if(i==0){
										firstPartOfWord.append("" + c);
										for (int j = 1; j< lastTextToken.length(); j++){
											restOfTheWord.append("" + lastTextToken.charAt(j));
										}
										break;
									} else {
										restOfTheWord.append("" + c);
										restOfTheWordWidth = Math.max(width, restOfTheWordWidth);
										
									}
								}
							}
						}
						// reset
						alreadyTextInLine = false;
						sinceLastWrapPoint.push(currentFont, fontSize,
								new Token(TokenType.TEXT, firstPartOfWord.toString()));
						textInLine.push(sinceLastWrapPoint);
						// this is our line
						result.add(textInLine.trimmedText());
						lineWidths.put(lineCounter, textInLine.trimmedWidth());
						mapLineTokens.put(lineCounter, textInLine.tokens());
						maxLineWidth = Math.max(maxLineWidth, textInLine.trimmedWidth());
						textInLine.reset();
						lineCounter++;
						word = restOfTheWord.toString();
						}
						sinceLastWrapPoint.push(currentFont, fontSize, new Token(TokenType.TEXT, word));
					} else {
						sinceLastWrapPoint.push(currentFont, fontSize, token);
					}
				
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		if (sinceLastWrapPoint.trimmedWidth() + textInLine.trimmedWidth() > 0)

		{
			textInLine.push(sinceLastWrapPoint);
			result.add(textInLine.trimmedText());
			lineWidths.put(lineCounter, textInLine.trimmedWidth());
			mapLineTokens.put(lineCounter, textInLine.tokens());
			maxLineWidth = Math.max(maxLineWidth, textInLine.trimmedWidth());
		}

		return result;

	}

	private boolean isItalic(final Token token) {
		return "i".equals(token.getData());
	}

	private boolean isBold(final Token token) {
		return "b".equals(token.getData());
	}

	private boolean isParagraph(final Token token) {
		return "p".equals(token.getData());
	}

	private boolean isListElement(final Token token) {
		return "li".equals(token.getData());
	}

	private boolean isList(final Token token) {
		return "ul".equals(token.getData()) || "ol".equals(token.getData());
	}

	private static String indentLevel(int numberOfSpaces) {
		//String builder is efficient at concatenating strings together
		StringBuilder sb = new StringBuilder();

		//Loop as many times as specified; each time add a space to the string
		for (int i = 0; i < numberOfSpaces; i++) {
			sb.append(" ");
		}

		//Return the string
		return sb.toString();
	}

	public PDFont getFont(boolean isBold, boolean isItalic) {
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
		if(getLines().size() == 0){
			return 0;
		} else {
			return (getLines().size()-1)*getLineSpacing()*getFontHeight() + getFontHeight();
		}
	}

	public float getFontHeight() {
		return FontUtils.getHeight(font, fontSize);
	}

	/**
	 * @deprecated This method will be removed in a future release
	 * @return current font width
	 */
	@Deprecated
	public float getFontWidth() {
		return font.getFontDescriptor().getFontBoundingBox().getWidth() / 1000 * fontSize;
	}

	/**
	 * @deprecated This method will be removed in a future release
	 * @param width Paragraph's width
	 * @return {@link Paragraph} with designated width
	 */
	@Deprecated
	public Paragraph withWidth(int width) {
		this.width = width;
		return this;
	}

	/**
	 * @deprecated This method will be removed in a future release
	 * @param font {@link PDFont} for {@link Paragraph}
	 * @param fontSize font size for {@link Paragraph}
	 * @return {@link Paragraph} with designated font and font size
	 */
	@Deprecated
	public Paragraph withFont(PDFont font, int fontSize) {
		this.font = font;
		this.fontSize = fontSize;
		return this;
	}

	/**
	 * /**
	 * @deprecated This method will be removed in a future release
	 * @param color {@code int} rgb value for color
	 * @return Paragraph's {@link Color}
	 */
	@Deprecated
	public Paragraph withColor(int color) {
		this.color = new Color(color);
		return this;
	}

	/**
	 * @deprecated This method will be replaced by
	 *             {@code public Color getColor()} in a future release
	 * @return Paragraph's {@link Color} 
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

	public Map<Integer, List<Token>> getMapLineTokens() {
		return mapLineTokens;
	}

	public float getLineSpacing() {
		return lineSpacing;
	}

	public void setLineSpacing(float lineSpacing) {
		this.lineSpacing = lineSpacing;
	}

}
