package be.quodlibet.boxable.text;

import org.apache.pdfbox.pdmodel.font.PDFont;

import java.io.IOException;

public class Token {

	private final TokenType type;
	
	private final String data;

	private PDFont cachedWidthFont;
	private float cachedWidth;

	public Token(TokenType type, String data) {
		this.type = type;
		this.data = data;
	}
	
	public String getData() {
		return data;
	}
	
	public TokenType getType() {
		return type;
	}

	public float getWidth(PDFont font) throws IOException {
		if (font == cachedWidthFont) {
			return cachedWidth;
		}
		cachedWidth = font.getStringWidth(data);
		// must come after getStringWidth(), in case it throws
		cachedWidthFont = font;
		return cachedWidth;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + type + "/" + data + "]";
	}
}
