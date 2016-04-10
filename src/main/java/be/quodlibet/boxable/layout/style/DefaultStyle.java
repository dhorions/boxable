package be.quodlibet.boxable.layout.style;

import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.VerticalAlignment;
import be.quodlibet.boxable.line.LineStyle;
import java.awt.Color;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 *
 * @author Dries Horions <dries@quodlibet.be>
 */
public class DefaultStyle extends Style
{
    public static enum Styles
    {

        DEFAULT, GREEN, RED, PINK, CANDY
    }
    public DefaultStyle()
    {
        defaultStyle();
    }

    public DefaultStyle(Styles s)
    {
        switch (s) {
            case DEFAULT:
                defaultStyle();
                break;
            case GREEN:
                greenStyle();
                break;
            case RED:
                redStyle();
                break;
            case PINK:
                pinkStyle();
                break;
            case CANDY:
                candyStyle();
                break;
        }
    }

    private void redStyle()
    {
        this.border = new LineStyle(Color.GRAY, (float) 0.3);
        this.font = PDType1Font.HELVETICA;
        this.fontBold = PDType1Font.HELVETICA_BOLD;
        this.fontsize = 10;
        this.fillcolorDefault = Color.WHITE;
        this.fillcolorAccent1 = new Color(232, 88, 49);
        this.fillcolorAccent2 = new Color(232, 96, 58);
        this.fillcolorAccent3 = new Color(245, 139, 110);
        this.fillcolorAccent4 = new Color(245, 139, 110);
        this.fillcolorAccent5 = new Color(250, 250, 250);
        this.fillcolorAccent6 = new Color(252, 252, 217);

        this.textcolorDefault = Color.BLACK;
        this.textcolorAccent1 = textcolorDefault;
        this.textcolorAccent2 = textcolorDefault;
        this.textcolorAccent3 = textcolorDefault;
        this.textcolorAccent4 = textcolorDefault;
        this.textcolorAccent5 = textcolorDefault;
        this.textcolorAccent6 = textcolorDefault;

        this.alignDefault = HorizontalAlignment.LEFT;
        this.alignAccent1 = HorizontalAlignment.LEFT;
        this.alignAccent2 = alignAccent1;
        this.alignAccent3 = alignAccent1;
        this.alignAccent4 = alignAccent1;
        this.alignAccent5 = alignAccent1;
        this.alignAccent6 = alignAccent1;

        this.valignDefault = VerticalAlignment.TOP;
        this.valignAccent1 = VerticalAlignment.TOP;
        this.valignAccent2 = valignAccent1;
        this.valignAccent3 = valignAccent1;
        this.valignAccent4 = valignAccent1;
        this.valignAccent5 = valignAccent1;
        this.valignAccent6 = valignAccent1;
    }

    private void pinkStyle()
    {
        this.border = new LineStyle(Color.GRAY, (float) 0.3);
        this.font = PDType1Font.HELVETICA;
        this.fontBold = PDType1Font.HELVETICA_BOLD;
        this.fontsize = 10;
        this.fillcolorDefault = Color.WHITE;
        this.fillcolorAccent1 = new Color(227, 200, 204);
        this.fillcolorAccent2 = new Color(222, 155, 166);
        this.fillcolorAccent3 = new Color(232, 186, 193);
        this.fillcolorAccent4 = new Color(232, 186, 193);
        this.fillcolorAccent5 = new Color(237, 225, 227);
        this.fillcolorAccent6 = new Color(250, 247, 248);

        this.textcolorDefault = Color.BLACK;
        this.textcolorAccent1 = textcolorDefault;
        this.textcolorAccent2 = textcolorDefault;
        this.textcolorAccent3 = textcolorDefault;
        this.textcolorAccent4 = textcolorDefault;
        this.textcolorAccent5 = textcolorDefault;
        this.textcolorAccent6 = textcolorDefault;

        this.alignDefault = HorizontalAlignment.LEFT;
        this.alignAccent1 = HorizontalAlignment.LEFT;
        this.alignAccent2 = alignAccent1;
        this.alignAccent3 = alignAccent1;
        this.alignAccent4 = alignAccent1;
        this.alignAccent5 = alignAccent1;
        this.alignAccent6 = alignAccent1;

        this.valignDefault = VerticalAlignment.TOP;
        this.valignAccent1 = VerticalAlignment.TOP;
        this.valignAccent2 = valignAccent1;
        this.valignAccent3 = valignAccent1;
        this.valignAccent4 = valignAccent1;
        this.valignAccent5 = valignAccent1;
        this.valignAccent6 = valignAccent1;
    }

    private void candyStyle()
    {
        this.border = new LineStyle(Color.GRAY, (float) 0.3);
        this.font = PDType1Font.HELVETICA;
        this.fontBold = PDType1Font.HELVETICA_BOLD;
        this.fontsize = 10;
        this.fillcolorDefault = Color.WHITE;
        this.fillcolorAccent1 = new Color(98, 188, 250);
        this.fillcolorAccent2 = new Color(252, 205, 211);
        this.fillcolorAccent3 = new Color(98, 188, 250);
        this.fillcolorAccent4 = new Color(98, 188, 250);
        this.fillcolorAccent5 = new Color(252, 232, 235);
        this.fillcolorAccent6 = new Color(187, 196, 239);

        this.textcolorDefault = Color.BLACK;
        this.textcolorAccent1 = textcolorDefault;
        this.textcolorAccent2 = textcolorDefault;
        this.textcolorAccent3 = textcolorDefault;
        this.textcolorAccent4 = textcolorDefault;
        this.textcolorAccent5 = textcolorDefault;
        this.textcolorAccent6 = textcolorDefault;

        this.alignDefault = HorizontalAlignment.LEFT;
        this.alignAccent1 = HorizontalAlignment.LEFT;
        this.alignAccent2 = alignAccent1;
        this.alignAccent3 = alignAccent1;
        this.alignAccent4 = alignAccent1;
        this.alignAccent5 = alignAccent1;
        this.alignAccent6 = alignAccent1;

        this.valignDefault = VerticalAlignment.TOP;
        this.valignAccent1 = VerticalAlignment.TOP;
        this.valignAccent2 = valignAccent1;
        this.valignAccent3 = valignAccent1;
        this.valignAccent4 = valignAccent1;
        this.valignAccent5 = valignAccent1;
        this.valignAccent6 = valignAccent1;

    }

    private void greenStyle()
    {
        this.border = new LineStyle(Color.GRAY, (float) 0.3);
        this.font = PDType1Font.HELVETICA;
        this.fontBold = PDType1Font.HELVETICA_BOLD;
        this.fontsize = 10;
        this.fillcolorDefault = Color.WHITE;
        this.fillcolorAccent1 = new Color(209, 245, 191);
        this.fillcolorAccent2 = new Color(221, 240, 211);
        this.fillcolorAccent3 = new Color(240, 245, 243);
        this.fillcolorAccent4 = new Color(240, 245, 243);
        this.fillcolorAccent5 = new Color(237, 240, 235);
        this.fillcolorAccent6 = new Color(209, 219, 204);

        this.textcolorDefault = Color.BLACK;
        this.textcolorAccent1 = textcolorDefault;
        this.textcolorAccent2 = textcolorDefault;
        this.textcolorAccent3 = textcolorDefault;
        this.textcolorAccent4 = textcolorDefault;
        this.textcolorAccent5 = textcolorDefault;
        this.textcolorAccent6 = textcolorDefault;

        this.alignDefault = HorizontalAlignment.LEFT;
        this.alignAccent1 = HorizontalAlignment.LEFT;
        this.alignAccent2 = alignAccent1;
        this.alignAccent3 = alignAccent1;
        this.alignAccent4 = alignAccent1;
        this.alignAccent5 = alignAccent1;
        this.alignAccent6 = alignAccent1;

        this.valignDefault = VerticalAlignment.TOP;
        this.valignAccent1 = VerticalAlignment.TOP;
        this.valignAccent2 = valignAccent1;
        this.valignAccent3 = valignAccent1;
        this.valignAccent4 = valignAccent1;
        this.valignAccent5 = valignAccent1;
        this.valignAccent6 = valignAccent1;

    }

    private void defaultStyle()
    {
        this.border = new LineStyle(Color.GRAY, (float) 0.3);
        this.font = PDType1Font.HELVETICA;
        this.fontBold = PDType1Font.HELVETICA_BOLD;
        this.fontsize = 10;
        this.fillcolorDefault = Color.WHITE;
        this.fillcolorAccent1 = new Color(192, 223, 217);
        this.fillcolorAccent2 = new Color(173, 194, 191);
        this.fillcolorAccent3 = new Color(233, 236, 229);
        this.fillcolorAccent4 = new Color(233, 236, 229);
        this.fillcolorAccent5 = new Color(233, 244, 229);
        this.fillcolorAccent6 = new Color(233, 236, 229);

        this.textcolorDefault = Color.BLACK;
        this.textcolorAccent1 = textcolorDefault;
        this.textcolorAccent2 = textcolorDefault;
        this.textcolorAccent3 = textcolorDefault;
        this.textcolorAccent4 = textcolorDefault;
        this.textcolorAccent5 = textcolorDefault;
        this.textcolorAccent6 = textcolorDefault;

        this.alignDefault = HorizontalAlignment.LEFT;
        this.alignAccent1 = HorizontalAlignment.LEFT;
        this.alignAccent2 = alignAccent1;
        this.alignAccent3 = alignAccent1;
        this.alignAccent4 = alignAccent1;
        this.alignAccent5 = alignAccent1;
        this.alignAccent6 = alignAccent1;

        this.valignDefault = VerticalAlignment.TOP;
        this.valignAccent1 = VerticalAlignment.TOP;
        this.valignAccent2 = valignAccent1;
        this.valignAccent3 = valignAccent1;
        this.valignAccent4 = valignAccent1;
        this.valignAccent5 = valignAccent1;
        this.valignAccent6 = valignAccent1;
    }
    public DefaultStyle withFont(PDFont font)
    {
        setFont(font);
        return this;
    }

    public DefaultStyle withFontBold(PDFont font)
    {
        setFontBold(font);
        return this;
    }

    public DefaultStyle withTextcolorDefault(Color c)
    {
        setTextcolorDefault(c);
        return this;
    }

    public DefaultStyle withTextcolorAccent1(Color c)
    {
        setTextcolorAccent1(c);
        return this;
    }

    public DefaultStyle withTextcolorAccent2(Color c)
    {
        setTextcolorAccent2(c);
        return this;
    }

    public DefaultStyle withTextcolorAccent3(Color c)
    {
        setTextcolorAccent3(c);
        return this;
    }

    public DefaultStyle withTextcolorAccent4(Color c)
    {
        setTextcolorAccent4(c);
        return this;
    }

    public DefaultStyle withTextcolorAccent5(Color c)
    {
        setTextcolorAccent5(c);
        return this;
    }

    public DefaultStyle withTextcolorAccent6(Color c)
    {
        setTextcolorAccent6(c);
        return this;
    }

    public DefaultStyle withFillcolorDefault(Color c)
    {
        setFillcolorDefault(c);
        return this;
    }

    public DefaultStyle withFillcolorAccent1(Color c)
    {
        setFillcolorAccent1(c);
        return this;
    }

    public DefaultStyle withFillcolorAccent2(Color c)
    {
        setFillcolorAccent2(c);
        return this;
    }

    public DefaultStyle withFillcolorAccent3(Color c)
    {
        setFillcolorAccent3(c);
        return this;
    }

    public DefaultStyle withFillcolorAccent4(Color c)
    {
        setFillcolorAccent4(c);
        return this;
    }

    public DefaultStyle withFillcolorAccent5(Color c)
    {
        setFillcolorAccent5(c);
        return this;
    }

    public DefaultStyle withFillcolorAccent6(Color c)
    {
        setFillcolorAccent6(c);
        return this;
    }

    public DefaultStyle withBorder(LineStyle border)
    {
        setBorder(border);
        return this;
    }

    public DefaultStyle withAlignDefault(HorizontalAlignment align)
    {
        this.setAlignDefault(align);
        return this;
    }

    public DefaultStyle withAlignAccent1(HorizontalAlignment align)
    {
        this.setAlignAccent1(align);
        return this;
    }

    public DefaultStyle withAlignAccent2(HorizontalAlignment align)
    {
        this.setAlignAccent2(align);
        return this;
    }

    public DefaultStyle withAlignAccent3(HorizontalAlignment align)
    {
        this.setAlignAccent3(align);
        return this;
    }

    public DefaultStyle withAlignAccent4(HorizontalAlignment align)
    {
        this.setAlignAccent4(align);
        return this;
    }

    public DefaultStyle withAlignAccent5(HorizontalAlignment align)
    {
        this.setAlignAccent5(align);
        return this;
    }

    public DefaultStyle withAlignAccent6(HorizontalAlignment align)
    {
        this.setAlignAccent6(align);
        return this;
    }

    public DefaultStyle withValignDefault(VerticalAlignment align)
    {
        this.setValignDefault(align);
        return this;
    }

    public DefaultStyle withValignAccent1(VerticalAlignment align)
    {
        this.setValignAccent1(align);
        return this;
    }

    public DefaultStyle withValignAccent2(VerticalAlignment align)
    {
        this.setValignAccent2(align);
        return this;
    }

    public DefaultStyle withValignAccent3(VerticalAlignment align)
    {
        this.setValignAccent3(align);
        return this;
    }

    public DefaultStyle withValignAccent4(VerticalAlignment align)
    {
        this.setValignAccent4(align);
        return this;
    }

    public DefaultStyle withValignAccent51(VerticalAlignment align)
    {
        this.setValignAccent5(align);
        return this;
    }

    public DefaultStyle withValignAccent6(VerticalAlignment align)
    {
        this.setValignAccent6(align);
        return this;
    }

}
