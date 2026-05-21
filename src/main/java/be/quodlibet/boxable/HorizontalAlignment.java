package be.quodlibet.boxable;

/**
 * Horizontal alignment options for text and cells.
 */
public enum HorizontalAlignment {
	/** Left alignment */
	LEFT,
	/** Center alignment */
	CENTER,
	/** Right alignment */
	RIGHT;

	/**
	 * Gets the horizontal alignment from a string key.
	 * Defaults to LEFT if the key is null or not recognized.
	 *
	 * @param key the alignment key ("left", "center", or "right")
	 * @return the corresponding HorizontalAlignment
	 */
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
