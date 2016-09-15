package be.quodlibet.boxable.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public final class Tokenizer {

	private Tokenizer() {
	}

	public static List<Token> tokenize(final String text, final WrappingFunction wrappingFunction) {
		final List<Token> tokens = new ArrayList<>();
		if (text != null) {
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
					boolean consumed = false;
					if (textIndex < text.length() - 2) {
						final char lookahead1 = text.charAt(textIndex + 1);
						final char lookahead2 = text.charAt(textIndex + 2);
						if ('i' == lookahead1 && '>' == lookahead2) {
							// <i>
							if (sb.length() > 0) {
								tokens.add(new Token(TokenType.TEXT, sb.toString()));
								// clean string builder
								sb.delete(0, sb.length());
							}
							tokens.add(new Token(TokenType.OPEN_TAG, "i"));
							textIndex += 2;
							consumed = true;
						} else if ('b' == lookahead1 && '>' == lookahead2) {
							// <b>
							if (sb.length() > 0) {
								tokens.add(new Token(TokenType.TEXT, sb.toString()));
								// clean string builder
								sb.delete(0, sb.length());
							}
							tokens.add(new Token(TokenType.OPEN_TAG, "b"));
							textIndex += 2;
							consumed = true;
						} else if ('b' == lookahead1 && 'r' == lookahead2) {
							if (textIndex < text.length() - 3) {
								// <br>
								final char lookahead3 = text.charAt(textIndex + 3);
								if (lookahead3 == '>') {
									if (sb.length() > 0) {
										tokens.add(new Token(TokenType.TEXT, sb.toString()));
										// clean string builder
										sb.delete(0, sb.length());
									}
									tokens.add(new Token(TokenType.WRAP_POINT, "br"));
									// normal notation <br>
									textIndex += 3;
									consumed = true;
								} else if (textIndex < text.length() - 4) {
									// <br/>
									final char lookahead4 = text.charAt(textIndex + 4);
									if (lookahead3 == '/' && lookahead4 == '>') {
										if (sb.length() > 0) {
											tokens.add(new Token(TokenType.TEXT, sb.toString()));
											// clean string builder
											sb.delete(0, sb.length());
										}
										tokens.add(new Token(TokenType.WRAP_POINT, "br"));
										// normal notation <br/>
										textIndex += 4;
										consumed = true;
									} else if (textIndex < text.length() - 5) {
										final char lookahead5 = text.charAt(textIndex + 5);
										if (lookahead3 == ' ' && lookahead4 == '/' && lookahead5 == '>') {
											if (sb.length() > 0) {
												tokens.add(new Token(TokenType.TEXT, sb.toString()));
												// clean string builder
												sb.delete(0, sb.length());
											}
											tokens.add(new Token(TokenType.WRAP_POINT, "br"));
											// in case it is notation <br />
											textIndex += 5;
											consumed = true;
										}
									}
								}
							}
						} else if ('p' == lookahead1 && '>' == lookahead2) {
							// <p>
							if (sb.length() > 0) {
								tokens.add(new Token(TokenType.TEXT, sb.toString()));
								// clean string builder
								sb.delete(0, sb.length());
							}
							tokens.add(new Token(TokenType.WRAP_POINT, "p"));
							textIndex += 2;
							consumed = true;
						} else if ('o' == lookahead1 && 'l' == lookahead2) {
							// <ol>
							if (textIndex < text.length() - 3) {
								final char lookahead3 = text.charAt(textIndex + 3);
								if (lookahead3 == '>') {
									if (sb.length() > 0) {
										tokens.add(new Token(TokenType.TEXT, sb.toString()));
										// clean string builder
										sb.delete(0, sb.length());
									}
									tokens.add(new Token(TokenType.OPEN_TAG, "ol"));
									textIndex += 3;
									consumed = true;
								}
							}
						} else if ('u' == lookahead1 && 'l' == lookahead2) {
							// <ul>
							if (textIndex < text.length() - 3) {
								final char lookahead3 = text.charAt(textIndex + 3);
								if (lookahead3 == '>') {
									if (sb.length() > 0) {
										tokens.add(new Token(TokenType.TEXT, sb.toString()));
										// clean string builder
										sb.delete(0, sb.length());
									}
									tokens.add(new Token(TokenType.OPEN_TAG, "ul"));
									textIndex += 3;
									consumed = true;
								}
							}
						} else if ('l' == lookahead1 && 'i' == lookahead2) {
							// <li>
							if (textIndex < text.length() - 3) {
								final char lookahead3 = text.charAt(textIndex + 3);
								if (lookahead3 == '>') {
									if (sb.length() > 0) {
										tokens.add(new Token(TokenType.TEXT, sb.toString()));
										// clean string builder
										sb.delete(0, sb.length());
									}
									tokens.add(new Token(TokenType.WRAP_POINT, "li"));
									textIndex += 3;
									consumed = true;
								}
							}
						} else if ('/' == lookahead1) {
							// one character tags
							if (textIndex < text.length() - 3) {
								final char lookahead3 = text.charAt(textIndex + 3);
								if ('>' == lookahead3) {
									if ('i' == lookahead2) {
										// </i>
										if (sb.length() > 0) {
											tokens.add(new Token(TokenType.TEXT, sb.toString()));
											sb.delete(0, sb.length());
										}
										tokens.add(new Token(TokenType.CLOSE_TAG, "i"));
										textIndex += 3;
										consumed = true;
									} else if ('b' == lookahead2) {
										// </b>
										if (sb.length() > 0) {
											tokens.add(new Token(TokenType.TEXT, sb.toString()));
											sb.delete(0, sb.length());
										}
										tokens.add(new Token(TokenType.CLOSE_TAG, "b"));
										textIndex += 3;
										consumed = true;
									} else if ('p' == lookahead2) {
										//</p>
										if (sb.length() > 0) {
											tokens.add(new Token(TokenType.TEXT, sb.toString()));
											sb.delete(0, sb.length());
										}
										tokens.add(new Token(TokenType.CLOSE_TAG, "p"));
										textIndex += 3;
										consumed = true;
									}
								}
							}
							if (textIndex < text.length() - 4) {
								// lists
								final char lookahead3 = text.charAt(textIndex + 3);
								final char lookahead4 = text.charAt(textIndex + 4);
								if ('l' == lookahead3) {
									if ('o' == lookahead2 && '>' == lookahead4) {
										// </ol>
										if (sb.length() > 0) {
											tokens.add(new Token(TokenType.TEXT, sb.toString()));
											sb.delete(0, sb.length());
										}
										tokens.add(new Token(TokenType.CLOSE_TAG, "ol"));
										textIndex += 4;
										consumed = true;
									} else if ('u' == lookahead2 && '>' == lookahead4) {
										// </ul>
										if (sb.length() > 0) {
											tokens.add(new Token(TokenType.TEXT, sb.toString()));
											sb.delete(0, sb.length());
										}
										tokens.add(new Token(TokenType.CLOSE_TAG, "ul"));
										textIndex += 4;
										consumed = true;
									}
								} else if ('l' == lookahead2 && 'i' == lookahead3) {
									// </li>
									if ('>' == lookahead4) {
										if (sb.length() > 0) {
											tokens.add(new Token(TokenType.TEXT, sb.toString()));
											sb.delete(0, sb.length());
										}
										tokens.add(new Token(TokenType.CLOSE_TAG, "li"));
										textIndex += 4;
										consumed = true;
									}
								}
							}
						}
						
					}
					if (!consumed) {
						sb.append('<');
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
		} else

		{
			return Collections.emptyList();
		}
	}

}
