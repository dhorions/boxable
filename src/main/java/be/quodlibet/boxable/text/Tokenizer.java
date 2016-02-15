package be.quodlibet.boxable.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public final class Tokenizer {

	private Tokenizer() {
	}

	// font settings
	private static final PDFont font = PDType1Font.HELVETICA;
	private static final PDFont fontBold = PDType1Font.HELVETICA_BOLD;
	private static final PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;
	private static final PDFont fontBoldItalic = PDType1Font.HELVETICA_BOLD_OBLIQUE;

	public static List<Token> tokenize(final String text, final WrappingFunction wrappingFunction) {
		final List<Token> tokens = new ArrayList<>();

		final String[] split = wrappingFunction.getLines(text);
		int textIndex = text.length();
		final Stack<Integer> possibleWrapPoints = new Stack<>();
		possibleWrapPoints.push(textIndex);
		for (int i = split.length - 1; i > 0; i--) {
			final int splitLength = split[i].length();
			possibleWrapPoints.push(textIndex - splitLength);
			textIndex -= splitLength;
		}

		textIndex = 0;
		final StringBuilder sb = new StringBuilder();
		// taking first wrap point
		Integer currentWrapPoint = possibleWrapPoints.pop();
		while (textIndex < text.length()) {
			if (textIndex == currentWrapPoint) {
				if (sb.length() > 0) {
					tokens.add(new Token(TokenType.TEXT, sb.toString()));
					sb.delete(0, sb.length());
				}
				tokens.add(new Token(TokenType.POSSIBLE_WRAP_POINT, "" + textIndex));
				currentWrapPoint = possibleWrapPoints.pop();
			}

			final char c = text.charAt(textIndex);
			switch (c) {
			case '<':
				if (textIndex < text.length() - 2) {
					final char lookahead1 = text.charAt(textIndex + 1);
					final char lookahead2 = text.charAt(textIndex + 2);
					if ('i' == lookahead1 && '>' == lookahead2) {
						// it's standard italic tag <i>
						if (sb.length() > 0) {
							tokens.add(new Token(TokenType.TEXT, sb.toString()));
							// clean string builder
							sb.delete(0, sb.length());
						}
						tokens.add(new Token(TokenType.OPEN_TAG, "i"));
						textIndex += 2;
					} else if ('b' == lookahead1 && '>' == lookahead2) {
						// it's standard bold tag <b>
						if (sb.length() > 0) {
							tokens.add(new Token(TokenType.TEXT, sb.toString()));
							// clean string builder
							sb.delete(0, sb.length());
						}
						tokens.add(new Token(TokenType.OPEN_TAG, "b"));
						textIndex += 2;
					} else if ('/' == lookahead1) {
						// it's closing tag
						if (textIndex < text.length() - 3) {
							final char lookahead3 = text.charAt(textIndex + 3);
							if ('>' == lookahead3) {
								if ('i' == lookahead2) {
									if (sb.length() > 0) {
										tokens.add(new Token(TokenType.TEXT, sb.toString()));
										sb.delete(0, sb.length());
									}
									tokens.add(new Token(TokenType.CLOSE_TAG, "i"));
									textIndex += 3;
								} else if ('b' == lookahead2) {
									if (sb.length() > 0) {
										tokens.add(new Token(TokenType.TEXT, sb.toString()));
										sb.delete(0, sb.length());
									}
									tokens.add(new Token(TokenType.CLOSE_TAG, "b"));
									textIndex += 3;
								}
							}
						}
					}
				}
				break;
			default:
				sb.append(c);
				break;
			}
			textIndex++;
		}

		if (sb.length() > 0) {
			tokens.add(new Token(TokenType.TEXT, sb.toString()));
			sb.delete(0, sb.length());
		}
		tokens.add(new Token(TokenType.POSSIBLE_WRAP_POINT, "" + textIndex));

		return tokens;
	}

	public static PDFont getFont(boolean isBold, boolean isItalic) {
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
}
