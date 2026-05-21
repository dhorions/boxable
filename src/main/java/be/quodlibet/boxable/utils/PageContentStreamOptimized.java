package be.quodlibet.boxable.utils;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.util.Matrix;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;

/**
 * Optimized wrapper for PDPageContentStream providing caching and state management.
 * <p>
 * This class wraps Apache PDFBox's PDPageContentStream to optimize PDF content
 * generation by caching the state of text mode, fonts, colors, and line properties.
 * It prevents redundant operations and manages coordinate transformations for rotated content.
 * </p>
 */
public class PageContentStreamOptimized {
    private static final Matrix ROTATION = Matrix.getRotateInstance(Math.PI * 0.5, 0, 0);

    private final PDPageContentStream pageContentStream;
    private boolean textMode;
    private float textCursorAbsoluteX;
    private float textCursorAbsoluteY;
    private boolean rotated;

    /**
     * Constructs an optimized wrapper for the given PDPageContentStream.
     *
     * @param pageContentStream the underlying PDPageContentStream to wrap
     */
    public PageContentStreamOptimized(PDPageContentStream pageContentStream) {
        this.pageContentStream = pageContentStream;
    }

    /**
     * Sets the rotation state for text content.
     * <p>
     * When rotated is true, subsequent text will be transformed with a 90-degree rotation matrix.
     * When switching from rotated to non-rotated, any active text mode is ended.
     * </p>
     *
     * @param rotated true to enable rotation, false to disable
     * @throws IOException if an error occurs writing to the underlying stream
     */
    public void setRotated(boolean rotated) throws IOException {
        if (this.rotated == rotated) return;
        if (rotated) {
            if (textMode) {
                pageContentStream.setTextMatrix(ROTATION);
                textCursorAbsoluteX = 0;
                textCursorAbsoluteY = 0;
            }
        } else {
            endText();
        }
        this.rotated = rotated;
    }

    /**
     * Begins a text object if not already in text mode.
     * <p>
     * This method ensures text mode is activated before text operations. If a rotation
     * is active, the rotation matrix is applied to the text transformation.
     * </p>
     *
     * @throws IOException if an error occurs writing to the underlying stream
     */
    public void beginText() throws IOException {
        if (!textMode) {
            pageContentStream.beginText();
            if (rotated) {
                pageContentStream.setTextMatrix(ROTATION);
            }
            textMode = true;
            textCursorAbsoluteX = 0;
            textCursorAbsoluteY = 0;
        }
    }

    /**
     * Ends the current text object if in text mode.
     * <p>
     * This method marks the end of a text object and exits text mode.
     * It is automatically called before non-text operations.
     * </p>
     *
     * @throws IOException if an error occurs writing to the underlying stream
     */
    public void endText() throws IOException {
        if (textMode) {
            pageContentStream.endText();
            textMode = false;
        }
    }

    private PDFont currentFont;
    private float currentFontSize;

    /**
     * Sets the font and font size for text operations, with caching to avoid redundant calls.
     * <p>
     * This method only updates the underlying stream if the font or font size differs
     * from the previously set values.
     * </p>
     *
     * @param font the font to set
     * @param fontSize the font size in points
     * @throws IOException if an error occurs writing to the underlying stream
     */
    public void setFont(PDFont font, float fontSize) throws IOException {
        if (font != currentFont || fontSize != currentFontSize) {
            pageContentStream.setFont(font, fontSize);
            currentFont = font;
            currentFontSize = fontSize;
        }
    }

    /**
     * Shows the given text at the current text position.
     * <p>
     * This method ensures text mode is active before displaying the text.
     * </p>
     *
     * @param text the text to display
     * @throws IOException if an error occurs writing to the underlying stream
     */
    public void showText(String text) throws IOException {
        beginText();
        pageContentStream.showText(text);
    }

    /**
     * Moves the text cursor to an absolute position on the page.
     * <p>
     * This method calculates the offset from the current cursor position and handles
     * rotation transformations when applicable. Text mode is activated if needed.
     * </p>
     *
     * @param tx the absolute X coordinate
     * @param ty the absolute Y coordinate
     * @throws IOException if an error occurs writing to the underlying stream
     */
    public void newLineAt(float tx, float ty) throws IOException {
        beginText();
        float dx = tx - textCursorAbsoluteX;
        float dy = ty - textCursorAbsoluteY;
        if (rotated) {
            pageContentStream.newLineAtOffset(dy, -dx);
        } else {
            pageContentStream.newLineAtOffset(dx, dy);
        }
        textCursorAbsoluteX = tx;
        textCursorAbsoluteY = ty;
    }

    /**
     * Draws an image at the specified position and size.
     * <p>
     * This method automatically ends any active text mode before drawing the image.
     * </p>
     *
     * @param image the image to draw
     * @param x the X coordinate of the image
     * @param y the Y coordinate of the image
     * @param width the width of the image
     * @param height the height of the image
     * @throws IOException if an error occurs writing to the underlying stream
     */
    public void drawImage(PDImageXObject image, float x, float y, float width, float height) throws IOException {
        endText();
        pageContentStream.drawImage(image, x, y, width, height);
    }

    private Color currentStrokingColor;

    /**
     * Sets the stroking (outline) color, with caching to avoid redundant calls.
     * <p>
     * This method only updates the underlying stream if the color differs
     * from the previously set stroking color. Also applies alpha transparency if needed.
     * </p>
     *
     * @param color the stroking color to set
     * @throws IOException if an error occurs writing to the underlying stream
     */
    public void setStrokingColor(Color color) throws IOException {
        if (color != currentStrokingColor) {
            pageContentStream.setStrokingColor(color);

            int alpha = color.getAlpha();
            int currentAlpha = currentStrokingColor == null ? 255 : currentStrokingColor.getAlpha();

            if (alpha != currentAlpha) {
                PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
                graphicsState.setStrokingAlphaConstant(alpha / 255f);
                pageContentStream.setGraphicsStateParameters(graphicsState);
            }

            currentStrokingColor = color;
        }
    }

    private Color currentNonStrokingColor;

    /**
     * Sets the non-stroking (fill) color, with caching to avoid redundant calls.
     * <p>
     * This method only updates the underlying stream if the color differs
     * from the previously set non-stroking color. Also applies alpha transparency if needed.
     * </p>
     *
     * @param color the non-stroking color to set
     * @throws IOException if an error occurs writing to the underlying stream
     */
    public void setNonStrokingColor(Color color) throws IOException {
        if (color != currentNonStrokingColor) {
            pageContentStream.setNonStrokingColor(color);

            int alpha = color.getAlpha();
            int currentAlpha = currentNonStrokingColor == null ? 255 : currentNonStrokingColor.getAlpha();

            if (alpha != currentAlpha) {
                PDExtendedGraphicsState graphicsState = new PDExtendedGraphicsState();
                graphicsState.setNonStrokingAlphaConstant(alpha / 255f);
                pageContentStream.setGraphicsStateParameters(graphicsState);
            }

            currentNonStrokingColor = color;
        }
    }

    /**
     * Appends a rectangle to the current path.
     * <p>
     * This method automatically ends any active text mode before adding the rectangle.
     * </p>
     *
     * @param x the X coordinate of the rectangle's lower-left corner
     * @param y the Y coordinate of the rectangle's lower-left corner
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @throws IOException if an error occurs writing to the underlying stream
     */
    public void addRect(float x, float y, float width, float height) throws IOException {
        endText();
        pageContentStream.addRect(x, y, width, height);
    }

    /**
     * Starts a new subpath by moving to the specified point.
     * <p>
     * This method automatically ends any active text mode before moving the path position.
     * </p>
     *
     * @param x the X coordinate to move to
     * @param y the Y coordinate to move to
     * @throws IOException if an error occurs writing to the underlying stream
     */
    public void moveTo(float x, float y) throws IOException {
        endText();
        pageContentStream.moveTo(x, y);
    }

    /**
     * Appends a line segment to the current path.
     * <p>
     * This method automatically ends any active text mode before adding the line to the path.
     * </p>
     *
     * @param x the X coordinate of the line endpoint
     * @param y the Y coordinate of the line endpoint
     * @throws IOException if an error occurs writing to the underlying stream
     */
    public void lineTo(float x, float y) throws IOException {
        endText();
        pageContentStream.lineTo(x, y);
    }

    /**
     * Strokes the current path.
     * <p>
     * This method automatically ends any active text mode before stroking the path.
     * The path is rendered with the currently set stroking color and line properties.
     * </p>
     *
     * @throws IOException if an error occurs writing to the underlying stream
     */
    public void stroke() throws IOException {
        endText();
        pageContentStream.stroke();
    }

    /**
     * Fills the current path.
     * <p>
     * This method automatically ends any active text mode before filling the path.
     * The path is filled with the currently set non-stroking color.
     * </p>
     *
     * @throws IOException if an error occurs writing to the underlying stream
     */
    public void fill() throws IOException {
        endText();
        pageContentStream.fill();
    }

    private float currentLineWidth = -1;

    /**
     * Sets the line width for stroking operations, with caching to avoid redundant calls.
     * <p>
     * This method only updates the underlying stream if the line width differs
     * from the previously set value. Any active text mode is ended before setting.
     * </p>
     *
     * @param lineWidth the line width in user space units
     * @throws IOException if an error occurs writing to the underlying stream
     */
    public void setLineWidth(float lineWidth) throws IOException {
        if (lineWidth != currentLineWidth) {
            endText();
            pageContentStream.setLineWidth(lineWidth);
            currentLineWidth = lineWidth;
        }
    }

    private int currentLineCapStyle = -1;

    /**
     * Sets the line cap style, with caching to avoid redundant calls.
     * <p>
     * This method only updates the underlying stream if the line cap style differs
     * from the previously set value. Any active text mode is ended before setting.
     * </p>
     *
     * @param lineCapStyle the line cap style (0 = butt, 1 = round, 2 = projecting square)
     * @throws IOException if an error occurs writing to the underlying stream
     */
    public void setLineCapStyle(int lineCapStyle) throws IOException {
        if (lineCapStyle != currentLineCapStyle) {
            endText();
            pageContentStream.setLineCapStyle(lineCapStyle);
            currentLineCapStyle = lineCapStyle;
        }
    }

    private float[] currentLineDashPattern;
    private float currentLineDashPhase;

    /**
     * Sets the line dash pattern, with caching to avoid redundant calls.
     * <p>
     * This method only updates the underlying stream if the pattern or phase differs
     * from the previously set values. Any active text mode is ended before setting.
     * </p>
     *
     * @param pattern an array representing the dash pattern (e.g., [3, 2] for 3 on, 2 off)
     * @param phase the phase of the dash pattern
     * @throws IOException if an error occurs writing to the underlying stream
     */
    public void setLineDashPattern(float[] pattern, float phase) throws IOException {
        if ((pattern != currentLineDashPattern &&
            !Arrays.equals(pattern, currentLineDashPattern)) || phase != currentLineDashPhase) {
            endText();
            pageContentStream.setLineDashPattern(pattern, phase);
            currentLineDashPattern = pattern;
            currentLineDashPhase = phase;
        }
    }

    /**
     * Closes the underlying PDPageContentStream.
     * <p>
     * This method automatically ends any active text mode before closing the stream.
     * After calling this method, the wrapper should not be used.
     * </p>
     *
     * @throws IOException if an error occurs closing the underlying stream
     */
    public void close() throws IOException {
        endText();
        pageContentStream.close();
    }
}
