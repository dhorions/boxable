package be.quodlibet.boxable.text;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.Assert;
import org.junit.Test;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;

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
	 * and the default wrapping behavior is NOT applied.
	 * Produces a PDF to visually verify the custom wrapping behavior.
	 */
	@Test
	public void testCustomWrappingFunctionWithHashDelimiter() throws Exception {
		// Create a custom wrapping function that splits AFTER '#' characters
		// The regex (?<=#) is a positive lookbehind that splits immediately after '#'
		// This keeps '#' at the end of each segment (matching default wrapping behavior
		// where delimiters like spaces are kept in segments)
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
		final List<String> textSegments = new ArrayList<>();
		for (Token token : tokens) {
			if (TokenType.TEXT.equals(token.getType())) {
				textSegments.add(token.getData());
			}
		}

		// Expected segments after splitting on '#' (with '#' included in segments)
		Assert.assertEquals("First segment should be 'path/to/file/one#'", "path/to/file/one#", textSegments.get(0));
		Assert.assertEquals("Second segment should be 'path/to/file/two#'", "path/to/file/two#", textSegments.get(1));
		Assert.assertEquals("Third segment should be 'path/to/file/three'", "path/to/file/three", textSegments.get(2));

		// --- Produce a PDF to visually verify custom wrapping ---
		PDDocument doc = new PDDocument();
		PDPage page = new PDPage(PDRectangle.A4);
		doc.addPage(page);

		float margin = 50;
		float yStart = page.getMediaBox().getHeight() - margin;
		float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
		float yStartNewPage = page.getMediaBox().getHeight() - margin;
		float bottomMargin = 70;

		BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);

		// Load DejaVuSansCondensed if available, otherwise fall back to Helvetica
		File fontFile = new File("src/test/resources/fonts/DejaVuSansCondensed.ttf");
		PDFont font;
		PDFont fontBold;
		if (fontFile.exists()) {
			font = PDType0Font.load(doc, fontFile);
			fontBold = font; // use same font for bold (no bold variant available)
		} else {
			font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
			fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
		}

		// Title row
		Row<PDPage> titleRow = table.createRow(20f);
		Cell<PDPage> titleCell = titleRow.createCell(100, "Issue #298: Custom WrappingFunction with '#' Delimiter");
		titleCell.setFont(fontBold);
		titleCell.setFontSize(12f);
		titleCell.setFillColor(new Color(41, 128, 185));
		titleCell.setTextColor(Color.WHITE);
		titleCell.setAlign(HorizontalAlignment.CENTER);

		// Description row explaining what the user should see
		Row<PDPage> descRow = table.createRow(30f);
		Cell<PDPage> descCell = descRow.createCell(100,
				"Expected: The custom wrapping function splits text on '#' only. "
				+ "The '/' characters should NOT cause line breaks. "
				+ "Each row below wraps at '#' boundaries, never at '/' boundaries.");
		descCell.setFont(font);
		descCell.setFillColor(new Color(236, 240, 241));
		descCell.setFontSize(8f);

		// Header row
		Row<PDPage> headerRow = table.createRow(15f);
		Cell<PDPage> h1 = headerRow.createCell(25, "Scenario");
		h1.setFont(fontBold);
		h1.setFillColor(new Color(52, 73, 94));
		h1.setTextColor(Color.WHITE);
		h1.setFontSize(9f);

		Cell<PDPage> h2 = headerRow.createCell(50, "Cell Content (with custom '#' wrapping)");
		h2.setFont(fontBold);
		h2.setFillColor(new Color(52, 73, 94));
		h2.setTextColor(Color.WHITE);
		h2.setFontSize(9f);

		Cell<PDPage> h3 = headerRow.createCell(25, "Expected Wrapping");
		h3.setFont(fontBold);
		h3.setFillColor(new Color(52, 73, 94));
		h3.setTextColor(Color.WHITE);
		h3.setFontSize(9f);

		table.addHeaderRow(headerRow);

		// --- Row 1: Basic '#' delimited paths in a narrow cell ---
		Row<PDPage> row1 = table.createRow(40f);
		Cell<PDPage> r1c1 = row1.createCell(25, "Paths with '#' separator");
		r1c1.setFont(font);
		r1c1.setFontSize(8f);
		Cell<PDPage> r1c2 = row1.createCell(50, text);
		r1c2.setFont(font);
		r1c2.setFontSize(8f);
		r1c2.setWrappingFunction(customWrappingFunction);
		Cell<PDPage> r1c3 = row1.createCell(25, "Wraps after each '#', slashes stay together on same line");
		r1c3.setFont(font);
		r1c3.setFontSize(7f);
		r1c3.setFillColor(new Color(234, 250, 241));

		// --- Row 2: Same text WITHOUT custom wrapping (default behavior) ---
		Row<PDPage> row2 = table.createRow(40f);
		Cell<PDPage> r2c1 = row2.createCell(25, "Same text, DEFAULT wrapping (no custom function)");
		r2c1.setFont(font);
		r2c1.setFontSize(8f);
		r2c1.setFillColor(new Color(253, 237, 236));
		Cell<PDPage> r2c2 = row2.createCell(50, text);
		r2c2.setFont(font);
		r2c2.setFontSize(8f);
		r2c2.setFillColor(new Color(253, 237, 236));
		// No custom wrapping function - uses default behavior
		Cell<PDPage> r2c3 = row2.createCell(25, "May wrap at spaces or other default points; compare with row above");
		r2c3.setFont(font);
		r2c3.setFontSize(7f);
		r2c3.setFillColor(new Color(253, 237, 236));

		// --- Row 3: Longer text with more '#' segments ---
		String longerText = "com/example/pkg/ClassA#com/example/pkg/ClassB#com/example/pkg/ClassC#com/example/pkg/ClassD";
		Row<PDPage> row3 = table.createRow(50f);
		Cell<PDPage> r3c1 = row3.createCell(25, "Longer paths with '#' separator");
		r3c1.setFont(font);
		r3c1.setFontSize(8f);
		Cell<PDPage> r3c2 = row3.createCell(50, longerText);
		r3c2.setFont(font);
		r3c2.setFontSize(8f);
		r3c2.setWrappingFunction(customWrappingFunction);
		Cell<PDPage> r3c3 = row3.createCell(25, "4 segments, each wrapping only at '#'. Slashes within each segment stay on the same line");
		r3c3.setFont(font);
		r3c3.setFontSize(7f);
		r3c3.setFillColor(new Color(234, 250, 241));

		// --- Row 4: Text without any '#' (should remain on one line) ---
		String noHashText = "path/to/some/deeply/nested/file/without/hash";
		Row<PDPage> row4 = table.createRow(30f);
		Cell<PDPage> r4c1 = row4.createCell(25, "No '#' in text");
		r4c1.setFont(font);
		r4c1.setFontSize(8f);
		Cell<PDPage> r4c2 = row4.createCell(50, noHashText);
		r4c2.setFont(font);
		r4c2.setFontSize(8f);
		r4c2.setWrappingFunction(customWrappingFunction);
		Cell<PDPage> r4c3 = row4.createCell(25, "No '#' means no custom wrap points; text treated as single segment");
		r4c3.setFont(font);
		r4c3.setFontSize(7f);
		r4c3.setFillColor(new Color(234, 250, 241));

		// --- Row 5: Heavy slash text with '#' delimiters (custom wrapping) ---
		String heavySlashText = "/////////////////////////aaa//////////////////////////#/////////////////////////aaa//////////////////////////#/////////////////////////aaa//////////////////////////#/////////////////////////aaa////////////////////////// /////////////////////////aaa//////////////////////////";
		Row<PDPage> row5 = table.createRow(80f);
		Cell<PDPage> r5c1 = row5.createCell(25, "Heavy slashes with '#' (custom wrapping)");
		r5c1.setFont(font);
		r5c1.setFontSize(8f);
		Cell<PDPage> r5c2 = row5.createCell(50, heavySlashText);
		r5c2.setFont(font);
		r5c2.setFontSize(8f);
		r5c2.setWrappingFunction(customWrappingFunction);
		Cell<PDPage> r5c3 = row5.createCell(25, "Wraps only at '#'. Each long slash segment stays together on one line");
		r5c3.setFont(font);
		r5c3.setFontSize(7f);
		r5c3.setFillColor(new Color(234, 250, 241));

		// --- Row 6: Same heavy slash text with DEFAULT wrapping ---
		Row<PDPage> row6 = table.createRow(80f);
		Cell<PDPage> r6c1 = row6.createCell(25, "Heavy slashes, DEFAULT wrapping (no custom function)");
		r6c1.setFont(font);
		r6c1.setFontSize(8f);
		r6c1.setFillColor(new Color(253, 237, 236));
		Cell<PDPage> r6c2 = row6.createCell(50, heavySlashText);
		r6c2.setFont(font);
		r6c2.setFontSize(8f);
		r6c2.setFillColor(new Color(253, 237, 236));
		// No custom wrapping function - uses default behavior
		Cell<PDPage> r6c3 = row6.createCell(25, "Default wrapping may break at different points; compare with row above");
		r6c3.setFont(font);
		r6c3.setFontSize(7f);
		r6c3.setFillColor(new Color(253, 237, 236));

		table.draw();

		File file = new File("target/CustomWrappingFunctionHashDelimiter.pdf");
		System.out.println("Sample file saved at : " + file.getAbsolutePath());
		file.getParentFile().mkdirs();
		doc.save(file);
		doc.close();
	}
}
