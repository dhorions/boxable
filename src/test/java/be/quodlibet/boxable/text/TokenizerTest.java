package be.quodlibet.boxable.text;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TokenizerTest {
	
	private WrappingFunction wrappingFunction = new WrappingFunction() {
		@Override
		public String[] getLines(String t) {
			return t.split("(?<=\\s|-|@|,|\\.|:|;)");
		}
	};

	@Test
	public void testWrapPoints() throws Exception {
		final String text = "1 123 123456 12";
		final int[] expected = {2,6,13,15};
		int index = 0;
		final List<Token> tokens = Tokenizer.tokenize(text, wrappingFunction);
		System.out.println(tokens);
		for (final Token token : tokens) {
			if (TokenType.POSSIBLE_WRAP_POINT.equals(token.getType())) {
				Assert.assertEquals("Wrap point " + index + " is wrong", "" + expected[index], token.getData());
				index++;
			}
		}
		Assert.assertEquals("Not all possible wrap points were defined", index, expected.length);
	}
	
	@Test
	public void testEndsWithLt() throws Exception {
		final String text = "1 123 123456 12<";
		final List<Token> tokens = Tokenizer.tokenize(text, wrappingFunction);
	}
	
	@Test
	public void testSimpleItalic() throws Exception {
		{
			final String text = "1 <i>123 123456</i> 12";
			final List<Token> tokens = Tokenizer.tokenize(text, wrappingFunction);
			System.out.println("1. " + tokens);
		}
		{
			final String text = "1 <i>123</i> <i> 123456</i> 12";
			final List<Token> tokens = Tokenizer.tokenize(text, wrappingFunction);
			System.out.println("2. " + tokens);
		}
	}
	
	@Test
	public void testBoldAndItalic() throws Exception {
		{
			final String text = "1 <i><b>123</b> 123456</i> 12";
			final List<Token> tokens = Tokenizer.tokenize(text, wrappingFunction);
			System.out.println("3. " + tokens);
		}
		{
			final String text = "1 <i>123</i> <i> <b>123456</i></b> 12";
			final List<Token> tokens = Tokenizer.tokenize(text, wrappingFunction);
			System.out.println("4. " + tokens);
		}
	}
	
	@Test
	public void testEmptyString() throws Exception {
		// ""
		// null
		final String text = "";
		final String textNull = null;
		final List<Token> tokens = Tokenizer.tokenize(text, wrappingFunction);
//		final List<Token> tokens2 = Tokenizer.tokenize(textNull, wrappingFunction);
		System.out.println("5. " + tokens);
//		System.out.println("6. " + tokens2);
	}
}
