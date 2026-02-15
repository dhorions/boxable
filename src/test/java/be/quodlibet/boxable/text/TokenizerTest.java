package be.quodlibet.boxable.text;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TokenizerTest {
	
	private WrappingFunction wrappingFunction = null;

	@Test
	public void testWrapPoints() throws Exception {
		final String text = "1 123 123456 12";
		final List<Token> tokens = Tokenizer.tokenize(text, wrappingFunction);
		Assert.assertEquals(Arrays.asList(
				Token.text(TokenType.TEXT, "1 "),
				new Token(TokenType.POSSIBLE_WRAP_POINT, ""),
				Token.text(TokenType.TEXT, "123 "),
				new Token(TokenType.POSSIBLE_WRAP_POINT, ""),
				Token.text(TokenType.TEXT, "123456 "),
				new Token(TokenType.POSSIBLE_WRAP_POINT, ""),
				Token.text(TokenType.TEXT, "12"),
				new Token(TokenType.POSSIBLE_WRAP_POINT, "")
			), tokens);
	}
	
	@Test
	public void testEndsWithLt() throws Exception {
		final String text = "1 123 123456 12<";
		final List<Token> tokens = Tokenizer.tokenize(text, wrappingFunction);
			if (TokenType.CLOSE_TAG.equals(tokens.get(tokens.size()-1).getType())) {
				Assert.assertEquals("Text doesn't end with '<' character", "<", tokens.get(tokens.size()-1).getData());
		}
	}
	
	@Test
	public void testSimpleItalic() throws Exception {
		{
			final String text = "1 <i>123 123456</i> 12";
			final StringBuilder italicText = new StringBuilder();
			final List<Token> tokens = Tokenizer.tokenize(text, wrappingFunction);
			boolean italic = false;
			for (final Token token : tokens) {
				if (TokenType.OPEN_TAG.equals(token.getType()) && token.getData().equals("i")) {
					italic = true;
				} else if(TokenType.CLOSE_TAG.equals(token.getType()) && token.getData().equals("i")){
					italic = false;
				}
				if(TokenType.TEXT.equals(token.getType()) && italic){
					italicText.append(token.getData());
				}
			}
			Assert.assertEquals("Italic text is parsed wrong", "123 123456", italicText.toString());
		}
		{
			final String text = "1 <i>123</i> <i> 123456</i> 12";
			final List<Token> tokens = Tokenizer.tokenize(text, wrappingFunction);
			final StringBuilder italicText = new StringBuilder();
			boolean italic = false;
			for (final Token token : tokens) {
				if (TokenType.OPEN_TAG.equals(token.getType()) && token.getData().equals("i")) {
					italic = true;
				} else if(TokenType.CLOSE_TAG.equals(token.getType()) && token.getData().equals("i")){
					italic = false;
				}
				if(TokenType.TEXT.equals(token.getType()) && italic){
					italicText.append(token.getData());
				}
			}
			Assert.assertEquals("Italic text is parsed wrong", "123 123456", italicText.toString());
		}
	}

	@Test
	public void testSimpleUnderline() throws Exception {
		{
			final String text = "1 <u>123 123456</u> 12";
			final StringBuilder underlineText = new StringBuilder();
			final List<Token> tokens = Tokenizer.tokenize(text, wrappingFunction);
			boolean underline = false;
			for (final Token token : tokens) {
				if (TokenType.OPEN_TAG.equals(token.getType()) && token.getData().equals("u")) {
					underline = true;
				} else if(TokenType.CLOSE_TAG.equals(token.getType()) && token.getData().equals("u")){
					underline = false;
				}
				if(TokenType.TEXT.equals(token.getType()) && underline){
					underlineText.append(token.getData());
				}
			}
			Assert.assertEquals("Underline text is parsed wrong", "123 123456", underlineText.toString());
		}
		{
			final String text = "1 <u>123</u> <u> 123456</u> 12";
			final List<Token> tokens = Tokenizer.tokenize(text, wrappingFunction);
			final StringBuilder underlineText = new StringBuilder();
			boolean underline = false;
			for (final Token token : tokens) {
				if (TokenType.OPEN_TAG.equals(token.getType()) && token.getData().equals("u")) {
					underline = true;
				} else if(TokenType.CLOSE_TAG.equals(token.getType()) && token.getData().equals("u")){
					underline = false;
				}
				if(TokenType.TEXT.equals(token.getType()) && underline){
					underlineText.append(token.getData());
				}
			}
			Assert.assertEquals("Underline text is parsed wrong", "123 123456", underlineText.toString());
		}
	}
	
	@Test
	public void testBoldAndItalic() throws Exception {
		{
			final String text = "1 <i><b>123</b> 123456</i> 12";
			final List<Token> tokens = Tokenizer.tokenize(text, wrappingFunction);
			final StringBuilder boldItalicText = new StringBuilder();
			boolean bold = false;
			boolean italic = false;
			for (final Token token : tokens) {
				if (TokenType.OPEN_TAG.equals(token.getType()) && token.getData().equals("b")) {
					bold = true;
				} else if(TokenType.CLOSE_TAG.equals(token.getType()) && token.getData().equals("b")){
					bold = false;
				}
				if (TokenType.OPEN_TAG.equals(token.getType()) && token.getData().equals("i")) {
					italic = true;
				} else if(TokenType.CLOSE_TAG.equals(token.getType()) && token.getData().equals("i")){
					italic = false;
				}
				
				if(TokenType.TEXT.equals(token.getType()) && bold && italic){
					boldItalicText.append(token.getData());
				}
			}
			Assert.assertEquals("Bold-italic text is parsed wrong", "123", boldItalicText.toString());
		}
		{
			final String text = "1 <i>123</i> <i> <b>123456</i></b> 12";
			final List<Token> tokens = Tokenizer.tokenize(text, wrappingFunction);
			final StringBuilder boldItalicText = new StringBuilder();
			boolean bold = false;
			boolean italic = false;
			for (final Token token : tokens) {
				if (TokenType.OPEN_TAG.equals(token.getType()) && token.getData().equals("b")) {
					bold = true;
				} else if(TokenType.CLOSE_TAG.equals(token.getType()) && token.getData().equals("b")){
					bold = false;
				}
				if (TokenType.OPEN_TAG.equals(token.getType()) && token.getData().equals("i")) {
					italic = true;
				} else if(TokenType.CLOSE_TAG.equals(token.getType()) && token.getData().equals("i")){
					italic = false;
				}
				
				if(TokenType.TEXT.equals(token.getType()) && bold && italic){
					boldItalicText.append(token.getData());
				}
			}
			Assert.assertEquals("Bold-italic text is parsed wrong", "123456", boldItalicText.toString());
		}
	}
	
	@Test
	public void testEmptyString() throws Exception {
		// ""
		{
			final String text = "";
			final List<Token> tokens = Tokenizer.tokenize(text, wrappingFunction);
			for (final Token token : tokens) {
				if (TokenType.TEXT.equals(token.getType()) && token.getData().equals("")) {
					Assert.assertEquals("Bold-italic text is parsed wrong", "", token.getData());
				}
			}
		}
		// null
		{
			final String textNull = null;
			final List<Token> tokens = Tokenizer.tokenize(textNull, wrappingFunction);
			Assert.assertEquals("Bold-italic text is parsed wrong", Collections.emptyList(), tokens);
		}
	
	}

	/**
	 * Regression test for issue #298: setWrappingFunction not working.
	 * Tests that a custom WrappingFunction that splits on '#' is respected,
	 * and the default wrapping behavior (splitting on '/' and other characters) is NOT applied.
	 */
	@Test
	public void testCustomWrappingFunctionWithHashDelimiter() throws Exception {
		// Create a custom wrapping function that splits AFTER '#' characters
		// (keeping the '#' in each segment, similar to the default behavior)
		final WrappingFunction customWrappingFunction = new WrappingFunction() {
			@Override
			public String[] getLines(String text) {
				if (text == null) {
					return new String[0];
				}
				// Use positive lookbehind to split after '#' but keep '#' in segments
				return text.split("(?<=#)");
			}
		};

		// Test text with many '/' characters and '#' separators
		// If the custom function works correctly, it should only split on '#', not on '/'
		final String text = "path/to/file/one#path/to/file/two#path/to/file/three";
		final List<Token> tokens = Tokenizer.tokenize(text, customWrappingFunction);

		// Count TEXT tokens (should be 3 segments split by '#')
		int textTokenCount = 0;
		for (Token token : tokens) {
			if (TokenType.TEXT.equals(token.getType())) {
				textTokenCount++;
			}
		}

		// Should have exactly 3 TEXT tokens (one for each segment between '#')
		Assert.assertEquals("Custom wrapping function should split text into 3 segments", 3, textTokenCount);

		// Verify that the TEXT segments contain '/' characters (i.e., they were NOT split on '/')
		boolean foundSlashInTextToken = false;
		for (Token token : tokens) {
			if (TokenType.TEXT.equals(token.getType()) && token.getData().contains("/")) {
				foundSlashInTextToken = true;
				break;
			}
		}
		Assert.assertTrue("TEXT tokens should contain '/' characters (not split on '/')", foundSlashInTextToken);

		// Verify the actual content of the TEXT tokens
		List<String> textSegments = new java.util.ArrayList<>();
		for (Token token : tokens) {
			if (TokenType.TEXT.equals(token.getType())) {
				textSegments.add(token.getData());
			}
		}

		// Expected segments after splitting on '#' (with '#' included in segments)
		Assert.assertEquals("First segment should be 'path/to/file/one#'", "path/to/file/one#", textSegments.get(0));
		Assert.assertEquals("Second segment should be 'path/to/file/two#'", "path/to/file/two#", textSegments.get(1));
		Assert.assertEquals("Third segment should be 'path/to/file/three'", "path/to/file/three", textSegments.get(2));
	}
}
