package be.quodlibet.boxable;

/**
 * Vertical alignment options for text and cells.
 * Provides enumeration of alignment positions: top, middle, and bottom.
 */
public enum VerticalAlignment {
	/** Top alignment */
	TOP,
	/** Middle (center) alignment */
	MIDDLE,
	/** Bottom alignment */
	BOTTOM;

	/**
	 * Gets the vertical alignment from a string key.
	 * Defaults to TOP if the key is null or not recognized.
	 *
	 * @param key the alignment key ("top", "middle", or "bottom")
	 * @return the corresponding VerticalAlignment
	 */
	public static VerticalAlignment get(final String key) {
		switch (key == null ? "top" : key.toLowerCase().trim()) {
		case "top":
			return TOP;
		case "middle":
			return MIDDLE;
		case "bottom":
			return BOTTOM;
			default:
				return TOP;
		}
	}
}
