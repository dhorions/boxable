package be.quodlibet.boxable.text;

public class Token {

	private final TokenType type;
	
	private final String data;

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
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + type + "/" + data + "]";
	}
}
