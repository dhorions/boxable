package be.quodlibet.boxable.utils;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;

public class PageContentStreamOptimized {
    final PDPageContentStream pageContentStream;

    public PageContentStreamOptimized(PDPageContentStream pageContentStream) {
        this.pageContentStream = pageContentStream;
    }

    public void beginText() throws IOException {
        pageContentStream.beginText();
    }

    public void endText() throws IOException {
        pageContentStream.endText();
    }

    private PDFont currentFont;
    private float currentFontSize;

    public void setFont(PDFont font, float fontSize) throws IOException {
        if (font != currentFont || fontSize != currentFontSize) {
            pageContentStream.setFont(font, fontSize);
            currentFont = font;
            currentFontSize = fontSize;
        }
    }

    public void showText(String text) throws IOException {
        pageContentStream.showText(text);
    }

    public void newLineAtOffset(float tx, float ty) throws IOException {
        pageContentStream.newLineAtOffset(tx, ty);
    }

    public void setTextMatrix(Matrix matrix) throws IOException {
        pageContentStream.setTextMatrix(matrix);
    }

    public void drawImage(PDImageXObject image, float x, float y, float width, float height) throws IOException {
        pageContentStream.drawImage(image, x, y, width, height);
    }

    private Color currentStrokingColor;

    public void setStrokingColor(Color color) throws IOException {
        if (color != currentStrokingColor) {
            pageContentStream.setStrokingColor(color);
            currentStrokingColor = color;
        }
    }

    private Color currentNonStrokingColor;

    public void setNonStrokingColor(Color color) throws IOException {
        if (color != currentNonStrokingColor) {
            pageContentStream.setNonStrokingColor(color);
            currentNonStrokingColor = color;
        }
    }

    public void addRect(float x, float y, float width, float height) throws IOException {
        pageContentStream.addRect(x, y, width, height);
    }

    public void moveTo(float x, float y) throws IOException {
        pageContentStream.moveTo(x, y);
    }

    public void lineTo(float x, float y) throws IOException {
        pageContentStream.lineTo(x, y);
    }

    public void stroke() throws IOException {
        pageContentStream.stroke();
    }

    public void fill() throws IOException {
        pageContentStream.fill();
    }

    private float currentLineWidth = -1;

    public void setLineWidth(float lineWidth) throws IOException {
        if (lineWidth != currentLineWidth) {
            pageContentStream.setLineWidth(lineWidth);
            currentLineWidth = lineWidth;
        }
    }

    private int currentLineCapStyle = -1;

    public void setLineCapStyle(int lineCapStyle) throws IOException {
        if (lineCapStyle != currentLineCapStyle) {
            pageContentStream.setLineCapStyle(lineCapStyle);
            currentLineCapStyle = lineCapStyle;
        }
    }

    private float[] currentLineDashPattern;
    private float currentLineDashPhase;

    public void setLineDashPattern(float[] pattern, float phase) throws IOException {
        if ((pattern != currentLineDashPattern &&
            !Arrays.equals(pattern, currentLineDashPattern)) || phase != currentLineDashPhase) {
            pageContentStream.setLineDashPattern(pattern, phase);
            currentLineDashPattern = pattern;
            currentLineDashPhase = phase;
        }
    }

    @Deprecated
    public void appendRawCommands(String commands) throws IOException {
        pageContentStream.appendRawCommands(commands);
    }

    @Deprecated
    public void appendRawCommands(byte[] commands) throws IOException {
        pageContentStream.appendRawCommands(commands);
    }

    @Deprecated
    public void appendRawCommands(int data) throws IOException {
        pageContentStream.appendRawCommands(data);
    }

    @Deprecated
    public void appendRawCommands(double data) throws IOException {
        pageContentStream.appendRawCommands(data);
    }

    @Deprecated
    public void appendRawCommands(float data) throws IOException {
        pageContentStream.appendRawCommands(data);
    }

    @Deprecated
    public void appendCOSName(COSName name) throws IOException {
        pageContentStream.appendCOSName(name);
    }

    public void close() throws IOException {
        pageContentStream.close();
    }
}
