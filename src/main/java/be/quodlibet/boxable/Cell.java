
/*
 Quodlibet.be
 */
package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;

public class Cell {
    
    private float width;
    private String text;
    
    private PDFont font = PDType1Font.HELVETICA;
    private float fontSize = 8;
    private Color fillColor;
    private Color textColor;
    private final Row row;

    /**
     *  
     * @param width in % of table width
     * @param text
     */
    Cell(Row row,float width, String text) {
        this.row = row;
        this.width = (row.getWidth() * width)/100;
        this.text = text;
    }

    public Color getTextColor()
    {
        return textColor;
    }

    public void setTextColor(Color textColor)
    {
        this.textColor = textColor;
    }

    public Color getFillColor()
    {
        return fillColor;
    }

    public void setFillColor(Color fillColor)
    {
        this.fillColor = fillColor;
    }


    public float getWidth() {
        return width;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public PDFont getFont() {
        if (font == null){
            throw new IllegalArgumentException("Font not set.");
        }
        return font;
    }

    public void setFont(PDFont font)
    {
        this.font = font;
    }

    public float getFontSize()
    {
        return fontSize;
    }

    public void setFontSize(float fontSize)
    {
        this.fontSize = fontSize;
    }

    public Paragraph getParagraph()
    {
         return new Paragraph( text,  font,  (int)fontSize,  (int)width);
    }
    
    public float getExtraWidth(){
        return this.row.getLastCellExtraWidth() + getWidth();
    }

}
