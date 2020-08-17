package be.quodlibet.boxable.utils;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDInlineImage;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.apache.pdfbox.util.Matrix;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.Arrays;

public class PageContentStreamOptimized {
    final PDPageContentStream pageContentStream;
    PDFont currentFont;
    private float currentFontSize;
    Color currentNonStrokingColor;
    Color currentStrokingColor;
    float[] currentLineDashPattern;
    float currentLineDashPhase;


    public PageContentStreamOptimized(PDPageContentStream pageContentStream) {
        this.pageContentStream = pageContentStream;
    }

    public void beginText() throws IOException {
        pageContentStream.beginText();
    }

    public void endText() throws IOException {
        pageContentStream.endText();
    }

    public void setFont(PDFont font, float fontSize) throws IOException {
        if (font != currentFont || fontSize != currentFontSize) {
            pageContentStream.setFont(font, fontSize);
            currentFont = font;
            currentFontSize = fontSize;
        }
    }

    @Deprecated
    public void drawString(String text) throws IOException {
        pageContentStream.drawString(text);
    }

    public void showText(String text) throws IOException {
        pageContentStream.showText(text);
    }

    public void setLeading(double leading) throws IOException {
        pageContentStream.setLeading(leading);
    }

    public void newLine() throws IOException {
        pageContentStream.newLine();
    }

    @Deprecated
    public void moveTextPositionByAmount(float tx, float ty) throws IOException {
        pageContentStream.moveTextPositionByAmount(tx, ty);
    }

    public void newLineAtOffset(float tx, float ty) throws IOException {
        pageContentStream.newLineAtOffset(tx, ty);
    }

    @Deprecated
    public void setTextMatrix(double a, double b, double c, double d, double e, double f) throws IOException {
        pageContentStream.setTextMatrix(a, b, c, d, e, f);
    }

    @Deprecated
    public void setTextMatrix(AffineTransform matrix) throws IOException {
        pageContentStream.setTextMatrix(matrix);
    }

    public void setTextMatrix(Matrix matrix) throws IOException {
        pageContentStream.setTextMatrix(matrix);
    }

    @Deprecated
    public void setTextScaling(double sx, double sy, double tx, double ty) throws IOException {
        pageContentStream.setTextScaling(sx, sy, tx, ty);
    }

    @Deprecated
    public void setTextTranslation(double tx, double ty) throws IOException {
        pageContentStream.setTextTranslation(tx, ty);
    }

    @Deprecated
    public void setTextRotation(double angle, double tx, double ty) throws IOException {
        pageContentStream.setTextRotation(angle, tx, ty);
    }

    public void drawImage(PDImageXObject image, float x, float y) throws IOException {
        pageContentStream.drawImage(image, x, y);
    }

    public void drawImage(PDImageXObject image, float x, float y, float width, float height) throws IOException {
        pageContentStream.drawImage(image, x, y, width, height);
    }

    public void drawImage(PDImageXObject image, Matrix matrix) throws IOException {
        pageContentStream.drawImage(image, matrix);
    }

    @Deprecated
    public void drawInlineImage(PDInlineImage inlineImage, float x, float y) throws IOException {
        pageContentStream.drawInlineImage(inlineImage, x, y);
    }

    public void drawImage(PDInlineImage inlineImage, float x, float y) throws IOException {
        pageContentStream.drawImage(inlineImage, x, y);
    }

    @Deprecated
    public void drawInlineImage(PDInlineImage inlineImage, float x, float y, float width, float height) throws IOException {
        pageContentStream.drawInlineImage(inlineImage, x, y, width, height);
    }

    public void drawImage(PDInlineImage inlineImage, float x, float y, float width, float height) throws IOException {
        pageContentStream.drawImage(inlineImage, x, y, width, height);
    }

    @Deprecated
    public void drawXObject(PDXObject xobject, float x, float y, float width, float height) throws IOException {
        pageContentStream.drawXObject(xobject, x, y, width, height);
    }

    @Deprecated
    public void drawXObject(PDXObject xobject, AffineTransform transform) throws IOException {
        pageContentStream.drawXObject(xobject, transform);
    }

    public void drawForm(PDFormXObject form) throws IOException {
        pageContentStream.drawForm(form);
    }

    @Deprecated
    public void concatenate2CTM(double a, double b, double c, double d, double e, double f) throws IOException {
        pageContentStream.concatenate2CTM(a, b, c, d, e, f);
    }

    @Deprecated
    public void concatenate2CTM(AffineTransform at) throws IOException {
        pageContentStream.concatenate2CTM(at);
    }

    public void transform(Matrix matrix) throws IOException {
        pageContentStream.transform(matrix);
    }

    public void saveGraphicsState() throws IOException {
        pageContentStream.saveGraphicsState();
    }

    public void restoreGraphicsState() throws IOException {
        pageContentStream.restoreGraphicsState();
    }

    @Deprecated
    public void setStrokingColorSpace(PDColorSpace colorSpace) throws IOException {
        pageContentStream.setStrokingColorSpace(colorSpace);
    }

    @Deprecated
    public void setNonStrokingColorSpace(PDColorSpace colorSpace) throws IOException {
        pageContentStream.setNonStrokingColorSpace(colorSpace);
    }

//    public void setStrokingColor(PDColor color) throws IOException {
//        pageContentStream.setStrokingColor(color);
//    }

    public void setStrokingColor(Color color) throws IOException {
        if (color != currentStrokingColor) {
            pageContentStream.setStrokingColor(color);
            currentStrokingColor = color;
        }
    }

//    @Deprecated
//    public void setStrokingColor(float[] components) throws IOException {
//        pageContentStream.setStrokingColor(components);
//    }
//
//    public void setStrokingColor(int r, int g, int b) throws IOException {
//        pageContentStream.setStrokingColor(r, g, b);
//    }
//
//    @Deprecated
//    public void setStrokingColor(int c, int m, int y, int k) throws IOException {
//        pageContentStream.setStrokingColor(c, m, y, k);
//    }
//
//    public void setStrokingColor(float c, float m, float y, float k) throws IOException {
//        pageContentStream.setStrokingColor(c, m, y, k);
//    }

//    @Deprecated
//    public void setStrokingColor(int g) throws IOException {
//        pageContentStream.setStrokingColor(g);
//    }
//
//    public void setStrokingColor(double g) throws IOException {
//        pageContentStream.setStrokingColor(g);
//    }

//    public void setNonStrokingColor(PDColor color) throws IOException {
//        pageContentStream.setNonStrokingColor(color);
//    }

    public void setNonStrokingColor(Color color) throws IOException {
        if (currentNonStrokingColor != color) {
            pageContentStream.setNonStrokingColor(color);
            currentNonStrokingColor = color;
        }
    }

//    @Deprecated
//    public void setNonStrokingColor(float[] components) throws IOException {
//        pageContentStream.setNonStrokingColor(components);
//    }
//
//    public void setNonStrokingColor(int r, int g, int b) throws IOException {
//        pageContentStream.setNonStrokingColor(r, g, b);
//    }
//
//    public void setNonStrokingColor(int c, int m, int y, int k) throws IOException {
//        pageContentStream.setNonStrokingColor(c, m, y, k);
//    }
//
//    public void setNonStrokingColor(double c, double m, double y, double k) throws IOException {
//        pageContentStream.setNonStrokingColor(c, m, y, k);
//    }
//
//    public void setNonStrokingColor(int g) throws IOException {
//        pageContentStream.setNonStrokingColor(g);
//    }
//
//    public void setNonStrokingColor(double g) throws IOException {
//        pageContentStream.setNonStrokingColor(g);
//    }

    public void addRect(float x, float y, float width, float height) throws IOException {
        pageContentStream.addRect(x, y, width, height);
    }

    @Deprecated
    public void fillRect(float x, float y, float width, float height) throws IOException {
        pageContentStream.fillRect(x, y, width, height);
    }

    @Deprecated
    public void addBezier312(float x1, float y1, float x2, float y2, float x3, float y3) throws IOException {
        pageContentStream.addBezier312(x1, y1, x2, y2, x3, y3);
    }

    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) throws IOException {
        pageContentStream.curveTo(x1, y1, x2, y2, x3, y3);
    }

    @Deprecated
    public void addBezier32(float x2, float y2, float x3, float y3) throws IOException {
        pageContentStream.addBezier32(x2, y2, x3, y3);
    }

    public void curveTo2(float x2, float y2, float x3, float y3) throws IOException {
        pageContentStream.curveTo2(x2, y2, x3, y3);
    }

    @Deprecated
    public void addBezier31(float x1, float y1, float x3, float y3) throws IOException {
        pageContentStream.addBezier31(x1, y1, x3, y3);
    }

    public void curveTo1(float x1, float y1, float x3, float y3) throws IOException {
        pageContentStream.curveTo1(x1, y1, x3, y3);
    }

    public void moveTo(float x, float y) throws IOException {
        pageContentStream.moveTo(x, y);
    }

    public void lineTo(float x, float y) throws IOException {
        pageContentStream.lineTo(x, y);
    }

    @Deprecated
    public void addLine(float xStart, float yStart, float xEnd, float yEnd) throws IOException {
        pageContentStream.addLine(xStart, yStart, xEnd, yEnd);
    }

    @Deprecated
    public void drawLine(float xStart, float yStart, float xEnd, float yEnd) throws IOException {
        pageContentStream.drawLine(xStart, yStart, xEnd, yEnd);
    }

    @Deprecated
    public void addPolygon(float[] x, float[] y) throws IOException {
        pageContentStream.addPolygon(x, y);
    }

    @Deprecated
    public void drawPolygon(float[] x, float[] y) throws IOException {
        pageContentStream.drawPolygon(x, y);
    }

    @Deprecated
    public void fillPolygon(float[] x, float[] y) throws IOException {
        pageContentStream.fillPolygon(x, y);
    }

    public void stroke() throws IOException {
        pageContentStream.stroke();
    }

    public void closeAndStroke() throws IOException {
        pageContentStream.closeAndStroke();
    }

    @Deprecated
    public void fill(int windingRule) throws IOException {
        pageContentStream.fill(windingRule);
    }

    public void fill() throws IOException {
        pageContentStream.fill();
    }

    public void fillEvenOdd() throws IOException {
        pageContentStream.fillEvenOdd();
    }

    public void fillAndStroke() throws IOException {
        pageContentStream.fillAndStroke();
    }

    public void fillAndStrokeEvenOdd() throws IOException {
        pageContentStream.fillAndStrokeEvenOdd();
    }

    public void closeAndFillAndStroke() throws IOException {
        pageContentStream.closeAndFillAndStroke();
    }

    public void closeAndFillAndStrokeEvenOdd() throws IOException {
        pageContentStream.closeAndFillAndStrokeEvenOdd();
    }

    public void shadingFill(PDShading shading) throws IOException {
        pageContentStream.shadingFill(shading);
    }

    @Deprecated
    public void closeSubPath() throws IOException {
        pageContentStream.closeSubPath();
    }

    public void closePath() throws IOException {
        pageContentStream.closePath();
    }

    @Deprecated
    public void clipPath(int windingRule) throws IOException {
        pageContentStream.clipPath(windingRule);
    }

    public void clip() throws IOException {
        pageContentStream.clip();
    }

    public void clipEvenOdd() throws IOException {
        pageContentStream.clipEvenOdd();
    }

    public void setLineWidth(float lineWidth) throws IOException {
        pageContentStream.setLineWidth(lineWidth);
    }

    public void setLineJoinStyle(int lineJoinStyle) throws IOException {
        pageContentStream.setLineJoinStyle(lineJoinStyle);
    }

    Integer currentLineCapStyle;

    public void setLineCapStyle(int lineCapStyle) throws IOException {
        if (currentLineCapStyle == null || lineCapStyle != currentLineCapStyle) {
            pageContentStream.setLineCapStyle(lineCapStyle);
            currentLineCapStyle = lineCapStyle;
        }
    }

    public void setLineDashPattern(float[] pattern, float phase) throws IOException {
        if ((pattern != currentLineDashPattern &&
            !Arrays.equals(pattern, currentLineDashPattern)) || phase != currentLineDashPhase) {
            pageContentStream.setLineDashPattern(pattern, phase);
            currentLineDashPattern = pattern;
            currentLineDashPhase = phase;
        }
    }

    @Deprecated
    public void beginMarkedContentSequence(COSName tag) throws IOException {
        pageContentStream.beginMarkedContentSequence(tag);
    }

    public void beginMarkedContent(COSName tag) throws IOException {
        pageContentStream.beginMarkedContent(tag);
    }

    @Deprecated
    public void beginMarkedContentSequence(COSName tag, COSName propsName) throws IOException {
        pageContentStream.beginMarkedContentSequence(tag, propsName);
    }

    public void beginMarkedContent(COSName tag, PDPropertyList propertyList) throws IOException {
        pageContentStream.beginMarkedContent(tag, propertyList);
    }

    @Deprecated
    public void endMarkedContentSequence() throws IOException {
        pageContentStream.endMarkedContentSequence();
    }

    public void endMarkedContent() throws IOException {
        pageContentStream.endMarkedContent();
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

    public void setGraphicsStateParameters(PDExtendedGraphicsState state) throws IOException {
        pageContentStream.setGraphicsStateParameters(state);
    }

    public void addComment(String comment) throws IOException {
        pageContentStream.addComment(comment);
    }

    public void close() throws IOException {
        pageContentStream.close();
    }

    public void setRenderingMode(RenderingMode rm) throws IOException {
        pageContentStream.setRenderingMode(rm);
    }
}
