package be.quodlibet.boxable;

public enum HorizontalAlignment {
	LEFT, CENTER, RIGHT;

	public static HorizontalAlignment get(final String key) {
		switch (key == null ? "left" : key.toLowerCase().trim()) {
		case "left":
			return LEFT;
		case "center":
			return CENTER;
		case "right":
			return RIGHT;
		default:
			return LEFT;
		}
	}
}
