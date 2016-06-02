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

    public HorizontalAlignment getAlignDefault()
    {
        return alignDefault;
    }

    public void setAlignDefault(HorizontalAlignment alignDefault)
    {
        this.alignDefault = alignDefault;
    }

    public VerticalAlignment getValignDefault()
    {
        return valignDefault;
    }

    public void setValignDefault(VerticalAlignment valignDefault)
    {
        this.valignDefault = valignDefault;
    }

    public HorizontalAlignment getAlignAccent1()
    {
        return alignAccent1;
    }

    public void setAlignAccent1(HorizontalAlignment alignAccent1)
    {
        this.alignAccent1 = alignAccent1;
    }

    public VerticalAlignment getValignAccent1()
    {
        return valignAccent1;
    }

    public void setValignAccent1(VerticalAlignment valignAccent1)
    {
        this.valignAccent1 = valignAccent1;
    }

    public HorizontalAlignment getAlignAccent2()
    {
        return alignAccent2;
    }

    public void setAlignAccent2(HorizontalAlignment alignAccent2)
    {
        this.alignAccent2 = alignAccent2;
    }

    public VerticalAlignment getValignAccent2()
    {
        return valignAccent2;
    }

    public void setValignAccent2(VerticalAlignment valignAccent2)
    {
        this.valignAccent2 = valignAccent2;
    }

    public HorizontalAlignment getAlignAccent3()
    {
        return alignAccent3;
    }

    public void setAlignAccent3(HorizontalAlignment alignAccent3)
    {
        this.alignAccent3 = alignAccent3;
    }

    public VerticalAlignment getValignAccent3()
    {
        return valignAccent3;
    }

    public void setValignAccent3(VerticalAlignment valignAccent3)
    {
        this.valignAccent3 = valignAccent3;
    }

    public HorizontalAlignment getAlignAccent4()
    {
        return alignAccent4;
    }

    public void setAlignAccent4(HorizontalAlignment alignAccent4)
    {
        this.alignAccent4 = alignAccent4;
    }

    public VerticalAlignment getValignAccent4()
    {
        return valignAccent4;
    }

    public void setValignAccent4(VerticalAlignment valignAccent4)
    {
        this.valignAccent4 = valignAccent4;
    }

    public Color getTextcolorAccent5()
    {
        return textcolorAccent5;
    }

    public void setTextcolorAccent5(Color textcolorAccent5)
    {
        this.textcolorAccent5 = textcolorAccent5;
    }

    public Color getFillcolorAccent5()
    {
        return fillcolorAccent5;
    }

    public void setFillcolorAccent5(Color fillcolorAccent5)
    {
        this.fillcolorAccent5 = fillcolorAccent5;
    }

    public HorizontalAlignment getAlignAccent5()
    {
        return alignAccent5;
    }

    public void setAlignAccent5(HorizontalAlignment alignAccent5)
    {
        this.alignAccent5 = alignAccent5;
    }

    public VerticalAlignment getValignAccent5()
    {
        return valignAccent5;
    }

    public void setValignAccent5(VerticalAlignment valignAccent5)
    {
        this.valignAccent5 = valignAccent5;
    }

    public Color getTextcolorAccent6()
    {
        return textcolorAccent6;
    }

    public void setTextcolorAccent6(Color textcolorAccent6)
    {
        this.textcolorAccent6 = textcolorAccent6;
    }

    public Color getFillcolorAccent6()
    {
        return fillcolorAccent6;
    }

    public void setFillcolorAccent6(Color fillcolorAccent6)
    {
        this.fillcolorAccent6 = fillcolorAccent6;
    }

    public HorizontalAlignment getAlignAccent6()
    {
        return alignAccent6;
    }

    public void setAlignAccent6(HorizontalAlignment alignAccent6)
    {
        this.alignAccent6 = alignAccent6;
    }

    public VerticalAlignment getValignAccent6()
    {
        return valignAccent6;
    }

    public void setValignAccent6(VerticalAlignment valignAccent6)
    {
        this.valignAccent6 = valignAccent6;
    }


}
