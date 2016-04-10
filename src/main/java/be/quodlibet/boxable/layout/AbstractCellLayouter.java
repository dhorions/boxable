package be.quodlibet.boxable.layout;

import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.layout.style.DefaultStyle;
import org.apache.pdfbox.pdmodel.PDPage;

/**
 *
 * @author Dries Horions <dries@quodlibet.be>
 */
abstract class AbstractCellLayouter<T extends PDPage>
{
    abstract void layout(Cell<T> cell);
    DefaultStyle style;


}
