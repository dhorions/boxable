package be.quodlibet.boxable.layout.style;

import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.VerticalAlignment;
import be.quodlibet.boxable.line.LineStyle;
import java.awt.Color;
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

}
