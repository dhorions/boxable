package be.quodlibet.boxable.utils;

/**
 * Utility class for converting between different units of measurement.
 * PDF uses points (pt) as the base unit, where 1 point = 1/72 inch.
 * 
 * @author [Your name]
 */
public final class UnitConverter {
    
    // Conversion constants
    private static final float POINTS_PER_INCH = 72f;
    private static final float INCHES_PER_MM = 0.0393701f;
    private static final float INCHES_PER_CM = 0.393701f;
    private static final float MM_PER_INCH = 25.4f;
    private static final float CM_PER_INCH = 2.54f;
    
    // Derived constants
    private static final float POINTS_PER_MM = POINTS_PER_INCH * INCHES_PER_MM;  // ~2.834645
    private static final float POINTS_PER_CM = POINTS_PER_INCH * INCHES_PER_CM;  // ~28.34645
    private static final float MM_PER_POINT = MM_PER_INCH / POINTS_PER_INCH;     // ~0.352778
    private static final float CM_PER_POINT = CM_PER_INCH / POINTS_PER_INCH;     // ~0.0352778
    
    private UnitConverter() {
        // Utility class - prevent instantiation
    }
    
    // ========== Millimeters to Points ==========
    
    /**
     * Convert millimeters to PDF points.
     * @param mm value in millimeters
     * @return value in points
     */
    public static float mmToPoints(float mm) {
        return mm * POINTS_PER_MM;
    }
    
    /**
     * Convert millimeters to PDF points.
     * @param mm value in millimeters
     * @return value in points
     */
    public static double mmToPoints(double mm) {
        return mm * POINTS_PER_MM;
    }
    
    // ========== Points to Millimeters ==========
    
    /**
     * Convert PDF points to millimeters.
     * @param points value in points
     * @return value in millimeters
     */
    public static float pointsToMm(float points) {
        return points * MM_PER_POINT;
    }
    
    /**
     * Convert PDF points to millimeters.
     * @param points value in points
     * @return value in millimeters
     */
    public static double pointsToMm(double points) {
        return points * MM_PER_POINT;
    }
    
    // ========== Centimeters to Points ==========
    
    /**
     * Convert centimeters to PDF points.
     * @param cm value in centimeters
     * @return value in points
     */
    public static float cmToPoints(float cm) {
        return cm * POINTS_PER_CM;
    }
    
    /**
     * Convert centimeters to PDF points.
     * @param cm value in centimeters
     * @return value in points
     */
    public static double cmToPoints(double cm) {
        return cm * POINTS_PER_CM;
    }
    
    // ========== Points to Centimeters ==========
    
    /**
     * Convert PDF points to centimeters.
     * @param points value in points
     * @return value in centimeters
     */
    public static float pointsToCm(float points) {
        return points * CM_PER_POINT;
    }
    
    /**
     * Convert PDF points to centimeters.
     * @param points value in points
     * @return value in centimeters
     */
    public static double pointsToCm(double points) {
        return points * CM_PER_POINT;
    }
    
    // ========== Inches to Points ==========
    
    /**
     * Convert inches to PDF points.
     * @param inches value in inches
     * @return value in points
     */
    public static float inchesToPoints(float inches) {
        return inches * POINTS_PER_INCH;
    }
    
    /**
     * Convert inches to PDF points.
     * @param inches value in inches
     * @return value in points
     */
    public static double inchesToPoints(double inches) {
        return inches * POINTS_PER_INCH;
    }
    
    // ========== Points to Inches ==========
    
    /**
     * Convert PDF points to inches.
     * @param points value in points
     * @return value in inches
     */
    public static float pointsToInches(float points) {
        return points / POINTS_PER_INCH;
    }
    
    /**
     * Convert PDF points to inches.
     * @param points value in points
     * @return value in inches
     */
    public static double pointsToInches(double points) {
        return points / POINTS_PER_INCH;
    }
}
