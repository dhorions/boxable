package be.quodlibet.boxable.layout.style;

import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.VerticalAlignment;
import be.quodlibet.boxable.line.LineStyle;
import java.awt.Color;
import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 *
 * @author Dries Horions <dries@quodlibet.be>
 */
abstract class Style
{

    public LineStyle border;
    public PDFont font;
    public PDFont fontBold;
    public int fontsize;

    public Color textcolorDefault;
    public Color fillcolorDefault;
    public HorizontalAlignment alignDefault;
    public VerticalAlignment valignDefault;

    public Color textcolorAccent1;
    public Color fillcolorAccent1;
    public HorizontalAlignment alignAccent1;
    public VerticalAlignment valignAccent1;

    public Color textcolorAccent2;
    public Color fillcolorAccent2;
    public HorizontalAlignment alignAccent2;
    public VerticalAlignment valignAccent2;

    public Color textcolorAccent3;
    public Color fillcolorAccent3;
    public HorizontalAlignment alignAccent3;
    public VerticalAlignment valignAccent3;

    public Color textcolorAccent4;
    public Color fillcolorAccent4;
    public HorizontalAlignment alignAccent4;
    public VerticalAlignment valignAccent4;

    public Color textcolorAccent5;
    public Color fillcolorAccent5;
    public HorizontalAlignment alignAccent5;
    public VerticalAlignment valignAccent5;

    public Color textcolorAccent6;
    public Color fillcolorAccent6;
    public HorizontalAlignment alignAccent6;
    public VerticalAlignment valignAccent6;

    public LineStyle getBorder()
    {
        return border;
    }

    public void setBorder(LineStyle border)
    {
        this.border = border;
    }

    public PDFont getFont()
    {
        return font;
    }

    public void setFont(PDFont font)
    {
        this.font = font;
    }

    public PDFont getFontBold()
    {
        return fontBold;
    }

    public void setFontBold(PDFont fontBold)
    {
        this.fontBold = fontBold;
    }

    public int getFontsize()
    {
        return fontsize;
    }

    public void setFontsize(int fontsize)
    {
        this.fontsize = fontsize;
    }

    public Color getTextcolorDefault()
    {
        return textcolorDefault;
    }

    public void setTextcolorDefault(Color textcolorDefault)
    {
        this.textcolorDefault = textcolorDefault;
    }

    public Color getFillcolorDefault()
    {
        return fillcolorDefault;
    }

    public void setFillcolorDefault(Color fillcolorDefault)
    {
        this.fillcolorDefault = fillcolorDefault;
    }

    public Color getTextcolorAccent1()
    {
        return textcolorAccent1;
    }

    public void setTextcolorAccent1(Color textcolorAccent1)
    {
        this.textcolorAccent1 = textcolorAccent1;
    }

    public Color getFillcolorAccent1()
    {
        return fillcolorAccent1;
    }

    public void setFillcolorAccent1(Color fillcolorAccent1)
    {
        this.fillcolorAccent1 = fillcolorAccent1;
    }

    public Color getTextcolorAccent2()
    {
        return textcolorAccent2;
    }

    public void setTextcolorAccent2(Color textcolorAccent2)
    {
        this.textcolorAccent2 = textcolorAccent2;
    }

    public Color getFillcolorAccent2()
    {
        return fillcolorAccent2;
    }

    public void setFillcolorAccent2(Color fillcolorAccent2)
    {
        this.fillcolorAccent2 = fillcolorAccent2;
    }

    public Color getTextcolorAccent3()
    {
        return textcolorAccent3;
    }

    public void setTextcolorAccent3(Color textcolorAccent3)
    {
        this.textcolorAccent3 = textcolorAccent3;
    }

    public Color getFillcolorAccent3()
    {
        return fillcolorAccent3;
    }

    public void setFillcolorAccent3(Color fillcolorAccent3)
    {
        this.fillcolorAccent3 = fillcolorAccent3;
    }

    public Color getTextcolorAccent4()
    {
        return textcolorAccent4;
    }

    public void setTextcolorAccent4(Color textcolorAccent4)
    {
        this.textcolorAccent4 = textcolorAccent4;
    }

    public Color getFillcolorAccent4()
    {
        return fillcolorAccent4;
    }

    public void setFillcolorAccent4(Color fillcolorAccent4)
    {
        this.fillcolorAccent4 = fillcolorAccent4;
    }

}
