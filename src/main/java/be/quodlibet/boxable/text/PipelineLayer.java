package be.quodlibet.boxable.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * 
 * @author Markus Kühne
 *
 */

public class PipelineLayer {

	private static String rtrim(String s) {
		int len = s.length();
		while ((len > 0) && (s.charAt(len - 1) <= ' ')) {
			len--;
		}
		if (len == s.length()) {
			return s;
		}
		if (len == 0) {
			return "";
		}
		return s.substring(0, len);
	}

	private final StringBuilder text = new StringBuilder();

	private String lastTextToken = "";

	private List<Token> tokens = new ArrayList<>();

	private String trimmedLastTextToken = "";

	private float width;

	private float widthLastToken;

	private float widthTrimmedLastToken;

	private float widthCurrentText;

	public boolean isEmpty() {
		return tokens.isEmpty();
	}

	public void push(final Token token) {
		tokens.add(token);
	}

	public void push(final PDFont font, final float fontSize, final Token token) throws IOException {
		if (token.getType().equals(TokenType.PADDING)) {
			width += Float.parseFloat(token.getData());
		}
		if (token.getType().equals(TokenType.BULLET)) {
			// just appending one space because our bullet width will be wide as one character of current font
			text.append(token.getData());
			width += (token.getWidth(font) / 1000f * fontSize);
		}

		if (token.getType().equals(TokenType.ORDERING)) {
			// just appending one space because our bullet width will be wide as one character of current font
			text.append(token.getData());
			width += (token.getWidth(font) / 1000f * fontSize);
		}

		if (token.getType().equals(TokenType.TEXT)) {
			text.append(lastTextToken);
			width += widthLastToken;
			lastTextToken = token.getData();
			trimmedLastTextToken = rtrim(lastTextToken);
			widthLastToken = token.getWidth(font) / 1000f * fontSize;

			if (trimmedLastTextToken.length() == lastTextToken.length()) {
				widthTrimmedLastToken = widthLastToken;
			} else {
				widthTrimmedLastToken = (font.getStringWidth(trimmedLastTextToken) / 1000f * fontSize);
			}

			widthCurrentText = text.length() == 0 ? 0 :
					(font.getStringWidth(text.toString()) / 1000f * fontSize);
		}

		push(token);
	}

	public void push(final PipelineLayer pipeline) {
		text.append(lastTextToken);
		width += widthLastToken;
		text.append(pipeline.text);
		if (pipeline.text.length() > 0) {
			width += pipeline.widthCurrentText;
		}
		lastTextToken = pipeline.lastTextToken;
		trimmedLastTextToken = pipeline.trimmedLastTextToken;
		widthLastToken = pipeline.widthLastToken;
		widthTrimmedLastToken = pipeline.widthTrimmedLastToken;
		tokens.addAll(pipeline.tokens);

		pipeline.reset();
	}

	public void reset() {
		text.delete(0, text.length());
		width = 0.0f;
		lastTextToken = "";
		trimmedLastTextToken = "";
		widthLastToken = 0.0f;
		widthTrimmedLastToken = 0.0f;
		tokens.clear();
	}

	public String trimmedText() {
		return text.toString() + trimmedLastTextToken;
	}

	public float width() {
		return width + widthLastToken;
	}

	public float trimmedWidth() {
		return width + widthTrimmedLastToken;
	}

	public List<Token> tokens() {
		return new ArrayList<>(tokens);
	}

	@Override
	public String toString() {
		return text.toString() + "(" + lastTextToken + ") [width: " + width() + ", trimmed: " + trimmedWidth() + "]";
	}
}
