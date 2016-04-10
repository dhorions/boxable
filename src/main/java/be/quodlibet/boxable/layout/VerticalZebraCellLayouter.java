package be.quodlibet.boxable.layout;

import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.layout.style.DefaultStyle;
import org.apache.pdfbox.pdmodel.PDPage;

/**
 *
 * @author Dries Horions <dries@quodlibet.be>
 */
public class VerticalZebraCellLayouter<T extends PDPage> extends DefaultCellLayouter<T>
{

    public VerticalZebraCellLayouter(DefaultStyle style)
    {
        super(style);
    }

    public VerticalZebraCellLayouter(DefaultStyle.Styles s)
    {
        super(s);
    }

    @Override
    public void layout(Cell<T> cell)
    {
        if (!cell.isHeaderCell()) {
            if (cell.getColumnIndex() % 2 == 0) {
                cell.withFillColor(style.fillcolorAccent5)
                        .withTextColor(style.textcolorAccent5)
                        .withAlign(style.alignAccent5)
                        .withValign(style.valignAccent5);
            }
            else {
                cell.withFillColor(style.fillcolorAccent6)
                        .withTextColor(style.textcolorAccent6)
                        .withAlign(style.alignAccent6)
                        .withValign(style.valignAccent6);
            }
        }
    }

}
