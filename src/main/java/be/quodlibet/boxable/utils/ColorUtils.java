package be.quodlibet.boxable.utils;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for parsing colors from String representations (names, hex, rgb, rgba).
 * Supports standard W3C CSS named colors.
 */
public class ColorUtils {

    private static final Map<String, Color> CSS_COLOR_MAP = new HashMap<>();

    static {
        // Standard W3C CSS Colors
        CSS_COLOR_MAP.put("aliceblue", new Color(0xF0F8FF));
        CSS_COLOR_MAP.put("antiquewhite", new Color(0xFAEBD7));
        CSS_COLOR_MAP.put("aqua", new Color(0x00FFFF));
        CSS_COLOR_MAP.put("aquamarine", new Color(0x7FFFD4));
        CSS_COLOR_MAP.put("azure", new Color(0xF0FFFF));
        CSS_COLOR_MAP.put("beige", new Color(0xF5F5DC));
        CSS_COLOR_MAP.put("bisque", new Color(0xFFE4C4));
        CSS_COLOR_MAP.put("black", new Color(0x000000));
        CSS_COLOR_MAP.put("blanchedalmond", new Color(0xFFEBCD));
        CSS_COLOR_MAP.put("blue", new Color(0x0000FF));
        CSS_COLOR_MAP.put("blueviolet", new Color(0x8A2BE2));
        CSS_COLOR_MAP.put("brown", new Color(0xA52A2A));
        CSS_COLOR_MAP.put("burlywood", new Color(0xDEB887));
        CSS_COLOR_MAP.put("cadetblue", new Color(0x5F9EA0));
        CSS_COLOR_MAP.put("chartreuse", new Color(0x7FFF00));
        CSS_COLOR_MAP.put("chocolate", new Color(0xD2691E));
        CSS_COLOR_MAP.put("coral", new Color(0xFF7F50));
        CSS_COLOR_MAP.put("cornflowerblue", new Color(0x6495ED));
        CSS_COLOR_MAP.put("cornsilk", new Color(0xFFF8DC));
        CSS_COLOR_MAP.put("crimson", new Color(0xDC143C));
        CSS_COLOR_MAP.put("cyan", new Color(0x00FFFF));
        CSS_COLOR_MAP.put("darkblue", new Color(0x00008B));
        CSS_COLOR_MAP.put("darkcyan", new Color(0x008B8B));
        CSS_COLOR_MAP.put("darkgoldenrod", new Color(0xB8860B));
        CSS_COLOR_MAP.put("darkgray", new Color(0xA9A9A9));
        CSS_COLOR_MAP.put("darkgreen", new Color(0x006400));
        CSS_COLOR_MAP.put("darkkhaki", new Color(0xBDB76B));
        CSS_COLOR_MAP.put("darkmagenta", new Color(0x8B008B));
        CSS_COLOR_MAP.put("darkolivegreen", new Color(0x556B2F));
        CSS_COLOR_MAP.put("darkorange", new Color(0xFF8C00));
        CSS_COLOR_MAP.put("darkorchid", new Color(0x9932CC));
        CSS_COLOR_MAP.put("darkred", new Color(0x8B0000));
        CSS_COLOR_MAP.put("darksalmon", new Color(0xE9967A));
        CSS_COLOR_MAP.put("darkseagreen", new Color(0x8FBC8F));
        CSS_COLOR_MAP.put("darkslateblue", new Color(0x483D8B));
        CSS_COLOR_MAP.put("darkslategray", new Color(0x2F4F4F));
        CSS_COLOR_MAP.put("darkturquoise", new Color(0x00CED1));
        CSS_COLOR_MAP.put("darkviolet", new Color(0x9400D3));
        CSS_COLOR_MAP.put("deeppink", new Color(0xFF1493));
        CSS_COLOR_MAP.put("deepskyblue", new Color(0x00BFFF));
        CSS_COLOR_MAP.put("dimgray", new Color(0x696969));
        CSS_COLOR_MAP.put("dodgerblue", new Color(0x1E90FF));
        CSS_COLOR_MAP.put("firebrick", new Color(0xB22222));
        CSS_COLOR_MAP.put("floralwhite", new Color(0xFFFAF0));
        CSS_COLOR_MAP.put("forestgreen", new Color(0x228B22));
        CSS_COLOR_MAP.put("fuchsia", new Color(0xFF00FF));
        CSS_COLOR_MAP.put("gainsboro", new Color(0xDCDCDC));
        CSS_COLOR_MAP.put("ghostwhite", new Color(0xF8F8FF));
        CSS_COLOR_MAP.put("gold", new Color(0xFFD700));
        CSS_COLOR_MAP.put("goldenrod", new Color(0xDAA520));
        CSS_COLOR_MAP.put("gray", new Color(0x808080));
        CSS_COLOR_MAP.put("green", new Color(0x008000));
        CSS_COLOR_MAP.put("greenyellow", new Color(0xADFF2F));
        CSS_COLOR_MAP.put("honeydew", new Color(0xF0FFF0));
        CSS_COLOR_MAP.put("hotpink", new Color(0xFF69B4));
        CSS_COLOR_MAP.put("indianred", new Color(0xCD5C5C));
        CSS_COLOR_MAP.put("indigo", new Color(0x4B0082));
        CSS_COLOR_MAP.put("ivory", new Color(0xFFFFF0));
        CSS_COLOR_MAP.put("khaki", new Color(0xF0E68C));
        CSS_COLOR_MAP.put("lavender", new Color(0xE6E6FA));
        CSS_COLOR_MAP.put("lavenderblush", new Color(0xFFF0F5));
        CSS_COLOR_MAP.put("lawngreen", new Color(0x7CFC00));
        CSS_COLOR_MAP.put("lemonchiffon", new Color(0xFFFACD));
        CSS_COLOR_MAP.put("lightblue", new Color(0xADD8E6));
        CSS_COLOR_MAP.put("lightcoral", new Color(0xF08080));
        CSS_COLOR_MAP.put("lightcyan", new Color(0xE0FFFF));
        CSS_COLOR_MAP.put("lightgoldenrodyellow", new Color(0xFAFAD2));
        CSS_COLOR_MAP.put("lightgray", new Color(0xD3D3D3));
        CSS_COLOR_MAP.put("lightgreen", new Color(0x90EE90));
        CSS_COLOR_MAP.put("lightpink", new Color(0xFFB6C1));
        CSS_COLOR_MAP.put("lightsalmon", new Color(0xFFA07A));
        CSS_COLOR_MAP.put("lightseagreen", new Color(0x20B2AA));
        CSS_COLOR_MAP.put("lightskyblue", new Color(0x87CEFA));
        CSS_COLOR_MAP.put("lightslategray", new Color(0x778899));
        CSS_COLOR_MAP.put("lightsteelblue", new Color(0xB0C4DE));
        CSS_COLOR_MAP.put("lightyellow", new Color(0xFFFFE0));
        CSS_COLOR_MAP.put("lime", new Color(0x00FF00));
        CSS_COLOR_MAP.put("limegreen", new Color(0x32CD32));
        CSS_COLOR_MAP.put("linen", new Color(0xFAF0E6));
        CSS_COLOR_MAP.put("magenta", new Color(0xFF00FF));
        CSS_COLOR_MAP.put("maroon", new Color(0x800000));
        CSS_COLOR_MAP.put("mediumaquamarine", new Color(0x66CDAA));
        CSS_COLOR_MAP.put("mediumblue", new Color(0x0000CD));
        CSS_COLOR_MAP.put("mediumorchid", new Color(0xBA55D3));
        CSS_COLOR_MAP.put("mediumpurple", new Color(0x9370DB));
        CSS_COLOR_MAP.put("mediumseagreen", new Color(0x3CB371));
        CSS_COLOR_MAP.put("mediumslateblue", new Color(0x7B68EE));
        CSS_COLOR_MAP.put("mediumspringgreen", new Color(0x00FA9A));
        CSS_COLOR_MAP.put("mediumturquoise", new Color(0x48D1CC));
        CSS_COLOR_MAP.put("mediumvioletred", new Color(0xC71585));
        CSS_COLOR_MAP.put("midnightblue", new Color(0x191970));
        CSS_COLOR_MAP.put("mintcream", new Color(0xF5FFFA));
        CSS_COLOR_MAP.put("mistyrose", new Color(0xFFE4E1));
        CSS_COLOR_MAP.put("moccasin", new Color(0xFFE4B5));
        CSS_COLOR_MAP.put("navajowhite", new Color(0xFFDEAD));
        CSS_COLOR_MAP.put("navy", new Color(0x000080));
        CSS_COLOR_MAP.put("oldlace", new Color(0xFDF5E6));
        CSS_COLOR_MAP.put("olive", new Color(0x808000));
        CSS_COLOR_MAP.put("olivedrab", new Color(0x6B8E23));
        CSS_COLOR_MAP.put("orange", new Color(0xFFA500));
        CSS_COLOR_MAP.put("orangered", new Color(0xFF4500));
        CSS_COLOR_MAP.put("orchid", new Color(0xDA70D6));
        CSS_COLOR_MAP.put("palegoldenrod", new Color(0xEEE8AA));
        CSS_COLOR_MAP.put("palegreen", new Color(0x98FB98));
        CSS_COLOR_MAP.put("paleturquoise", new Color(0xAFEEEE));
        CSS_COLOR_MAP.put("palevioletred", new Color(0xDB7093));
        CSS_COLOR_MAP.put("papayawhip", new Color(0xFFEFD5));
        CSS_COLOR_MAP.put("peachpuff", new Color(0xFFDAB9));
        CSS_COLOR_MAP.put("peru", new Color(0xCD853F));
        CSS_COLOR_MAP.put("pink", new Color(0xFFC0CB));
        CSS_COLOR_MAP.put("plum", new Color(0xDDA0DD));
        CSS_COLOR_MAP.put("powderblue", new Color(0xB0E0E6));
        CSS_COLOR_MAP.put("purple", new Color(0x800080));
        CSS_COLOR_MAP.put("red", new Color(0xFF0000));
        CSS_COLOR_MAP.put("rosybrown", new Color(0xBC8F8F));
        CSS_COLOR_MAP.put("royalblue", new Color(0x4169E1));
        CSS_COLOR_MAP.put("saddlebrown", new Color(0x8B4513));
        CSS_COLOR_MAP.put("salmon", new Color(0xFA8072));
        CSS_COLOR_MAP.put("sandybrown", new Color(0xF4A460));
        CSS_COLOR_MAP.put("seagreen", new Color(0x2E8B57));
        CSS_COLOR_MAP.put("seashell", new Color(0xFFF5EE));
        CSS_COLOR_MAP.put("sienna", new Color(0xA0522D));
        CSS_COLOR_MAP.put("silver", new Color(0xC0C0C0));
        CSS_COLOR_MAP.put("skyblue", new Color(0x87CEEB));
        CSS_COLOR_MAP.put("slateblue", new Color(0x6A5ACD));
        CSS_COLOR_MAP.put("slategray", new Color(0x708090));
        CSS_COLOR_MAP.put("snow", new Color(0xFFFAFA));
        CSS_COLOR_MAP.put("springgreen", new Color(0x00FF7F));
        CSS_COLOR_MAP.put("steelblue", new Color(0x4682B4));
        CSS_COLOR_MAP.put("tan", new Color(0xD2B48C));
        CSS_COLOR_MAP.put("teal", new Color(0x008080));
        CSS_COLOR_MAP.put("thistle", new Color(0xD8BFD8));
        CSS_COLOR_MAP.put("tomato", new Color(0xFF6347));
        CSS_COLOR_MAP.put("turquoise", new Color(0x40E0D0));
        CSS_COLOR_MAP.put("violet", new Color(0xEE82EE));
        CSS_COLOR_MAP.put("wheat", new Color(0xF5DEB3));
        CSS_COLOR_MAP.put("white", new Color(0xFFFFFF));
        CSS_COLOR_MAP.put("whitesmoke", new Color(0xF5F5F5));
        CSS_COLOR_MAP.put("yellow", new Color(0xFFFF00));
        CSS_COLOR_MAP.put("yellowgreen", new Color(0x9ACD32));
    }

    private static final Pattern RGB_PATTERN = Pattern.compile("^rgb\\s*\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern RGBA_PATTERN = Pattern.compile("^rgba\\s*\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*([0-9.]+)\\s*\\)$", Pattern.CASE_INSENSITIVE);

    public static Color parseColor(String colorStr) {
        if (colorStr == null || colorStr.trim().isEmpty()) {
            return null;
        }
        String lower = colorStr.trim().toLowerCase();

        // 1. Check named colors
        if (CSS_COLOR_MAP.containsKey(lower)) {
            return CSS_COLOR_MAP.get(lower);
        }

        // 2. Check Hex colors
        if (lower.startsWith("#")) {
            try {
                // Support #RGB (3 digit) used in CSS
                if (lower.length() == 4) { 
                   String r = lower.substring(1, 2);
                   String g = lower.substring(2, 3);
                   String b = lower.substring(3, 4);
                   return Color.decode("#" + r + r + g + g + b + b); 
                }
                // Handle standard #RRGGBB
                return Color.decode(lower);
            } catch (NumberFormatException e) {
                // ignore, proceed to other checks
            }
        }

        // 3. Check rgb(r,g,b)
        Matcher rgbMatcher = RGB_PATTERN.matcher(colorStr.trim());
        if (rgbMatcher.matches()) {
            try {
                int r = Integer.parseInt(rgbMatcher.group(1));
                int g = Integer.parseInt(rgbMatcher.group(2));
                int b = Integer.parseInt(rgbMatcher.group(3));
                return new Color(clamp(r), clamp(g), clamp(b));
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        
        // 4. Check rgba(r,g,b,a) 
        Matcher rgbaMatcher = RGBA_PATTERN.matcher(colorStr.trim());
        if (rgbaMatcher.matches()) {
            try {
                int r = Integer.parseInt(rgbaMatcher.group(1));
                int g = Integer.parseInt(rgbaMatcher.group(2));
                int b = Integer.parseInt(rgbaMatcher.group(3));
                float a = Float.parseFloat(rgbaMatcher.group(4));
                 // alpha in Color is 0-255, in CSS it's 0.0-1.0
                int alpha = Math.round(a * 255);
                return new Color(clamp(r), clamp(g), clamp(b), clamp(alpha));
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        return null;
    }
    
    private static int clamp(int val) {
        return Math.max(0, Math.min(255, val));
    }
}
