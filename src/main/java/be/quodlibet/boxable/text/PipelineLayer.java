package be.quodlibet.boxable.text;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * 
 * @author Markus Kühne
 *
 */

public class PipelineLayer {
	
	private final Pattern whitespace = Pattern.compile("(?:\r\n|\n\r|[ \t\n\r\\f])+\\z");
	
	private final StringBuilder text = new StringBuilder();
	
	private String lastTextToken = "";
	
	private String trimmedLastTextToken = "";
	
	private float width;
	
	private float widthLastToken;
	
	private float widthTrimmedLastToken;
	
	public void push(final String tokenText, final PDFont font, final float fontSize) throws IOException {
		text.append(lastTextToken);
		width += widthLastToken;
		
		lastTextToken = tokenText;
		trimmedLastTextToken = whitespace.matcher(lastTextToken).replaceAll("");
		widthLastToken = (font.getStringWidth(lastTextToken) / 1000f * fontSize);
		widthTrimmedLastToken = (font.getStringWidth(trimmedLastTextToken) / 1000f * fontSize);
	}
	
	public void push(final PipelineLayer pipeline) {
		text.append(lastTextToken);
		width += widthLastToken;
		
		text.append(pipeline.text);
		
		lastTextToken = pipeline.lastTextToken;
		trimmedLastTextToken = pipeline.trimmedLastTextToken;
		widthLastToken = pipeline.widthLastToken;
		widthTrimmedLastToken = pipeline.widthTrimmedLastToken;
		
		pipeline.reset();
	}
	
	public void reset() {
		text.delete(0, text.length());
		width = 0.0f;
		lastTextToken = "";
		trimmedLastTextToken = "";
		widthLastToken = 0.0f;
		widthTrimmedLastToken = 0.0f;
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
	
	@Override
	public String toString() {
		return text.toString() + "(" + lastTextToken + ") [width: " + width() + ", trimmed: " + trimmedWidth() + "]";
	}
}
