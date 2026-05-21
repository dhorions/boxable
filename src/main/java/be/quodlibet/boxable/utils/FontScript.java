package be.quodlibet.boxable.utils;

/**
 * Defines the available font scripts for PDF generation.
 * <p>
 * This library uses Google's <strong>Noto</strong> font family. The name "Noto" comes from
 * "No Tofu" - referring to the blank boxes (□) that appear when a font doesn't support
 * a character. The Noto project aims to cover every Unicode character.
 * </p>
 *
 * <h2>About Noto Fonts</h2>
 * <p>
 * Because of the enormous number of scripts and characters involved, Noto fonts are
 * distributed as separate files by script. This library includes the most commonly
 * needed scripts:
 * </p>
 * <ul>
 *   <li>{@link #LATIN} - Noto Sans (Latin, Greek, Cyrillic)</li>
 *   <li>{@link #ARABIC} - Noto Sans Arabic</li>
 *   <li>{@link #HEBREW} - Noto Sans Hebrew</li>
 * </ul>
 *
 * <h2>Extending with Additional Scripts</h2>
 * <p>
 * Additional Noto fonts can be downloaded from
 * <a href="https://fonts.google.com/noto">Google Fonts Noto</a> or
 * <a href="https://github.com/googlefonts/noto-fonts">GitHub noto-fonts</a>.
 * </p>
 * <p>
 * Available Noto Sans variants include (not exhaustive):
 * </p>
 * <ul>
 *   <li>Noto Sans CJK - Chinese, Japanese, Korean</li>
 *   <li>Noto Sans Devanagari - Hindi, Sanskrit, Marathi, Nepali</li>
 *   <li>Noto Sans Thai - Thai</li>
 *   <li>Noto Sans Bengali - Bengali, Assamese</li>
 *   <li>Noto Sans Tamil - Tamil</li>
 *   <li>Noto Sans Georgian - Georgian</li>
 *   <li>Noto Sans Armenian - Armenian</li>
 *   <li>And many more for other scripts</li>
 * </ul>
 * <p>
 * To add a new script, download the TTF files, add them to the {@code fonts/} resources
 * directory, and add a new enum constant following the existing pattern.
 * </p>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Use Latin/Greek/Cyrillic (default)
 * FontUtils.setNotoSansFontsAsDefault(document, FontScript.LATIN);
 *
 * // Use Arabic script
 * FontUtils.setNotoSansFontsAsDefault(document, FontScript.ARABIC);
 *
 * // Use Hebrew script
 * FontUtils.setNotoSansFontsAsDefault(document, FontScript.HEBREW);
 * }</pre>
 *
 * @see FontUtils#setNotoSansFontsAsDefault(org.apache.pdfbox.pdmodel.PDDocument, FontScript)
 * @see <a href="https://fonts.google.com/noto">Google Fonts - Noto</a>
 * @see <a href="https://github.com/googlefonts/noto-fonts">GitHub - noto-fonts</a>
 */
public enum FontScript {

	/**
	 * Latin, Greek, and Cyrillic scripts.
	 * <p>
	 * Supports Western European, Eastern European, Greek, and Russian languages.
	 * Includes Regular, Bold, Italic, and BoldItalic variants.
	 * </p>
	 */
	LATIN("fonts/NotoSans-Regular.ttf", "fonts/NotoSans-Bold.ttf",
			"fonts/NotoSans-Italic.ttf", "fonts/NotoSans-BoldItalic.ttf"),

	/**
	 * Arabic script.
	 * <p>
	 * Supports Arabic, Persian, Urdu, and other languages using Arabic script.
	 * Note: Arabic script does not use italic variants; Regular is used for italic styles.
	 * </p>
	 */
	ARABIC("fonts/NotoSansArabic-Regular.ttf", "fonts/NotoSansArabic-Bold.ttf",
			"fonts/NotoSansArabic-Regular.ttf", "fonts/NotoSansArabic-Bold.ttf"),

	/**
	 * Hebrew script.
	 * <p>
	 * Supports Hebrew and Yiddish languages.
	 * Note: Hebrew script does not use italic variants; Regular is used for italic styles.
	 * </p>
	 */
	HEBREW("fonts/NotoSansHebrew-Regular.ttf", "fonts/NotoSansHebrew-Bold.ttf",
			"fonts/NotoSansHebrew-Regular.ttf", "fonts/NotoSansHebrew-Bold.ttf");

	private final String regularPath;
	private final String boldPath;
	private final String italicPath;
	private final String boldItalicPath;

	FontScript(String regularPath, String boldPath, String italicPath, String boldItalicPath) {
		this.regularPath = regularPath;
		this.boldPath = boldPath;
		this.italicPath = italicPath;
		this.boldItalicPath = boldItalicPath;
	}

	public String getRegularPath() {
		return regularPath;
	}

	public String getBoldPath() {
		return boldPath;
	}

	public String getItalicPath() {
		return italicPath;
	}

	public String getBoldItalicPath() {
		return boldItalicPath;
	}
}
