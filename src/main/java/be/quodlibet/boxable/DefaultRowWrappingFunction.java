package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDPage;

import java.util.Arrays;
import java.util.List;

/**
 * This Wrapping function allows you to create advanced table layouts by hiding borders,
 * without braking them on pagebreaks. e.g.:
 * ┌────┬──────┬──────┐
 * ├────┼──────┤      │
 * ├────┼──────┤      │
 * ├────┼──────┤      │
 * ├────┼──────┤      │
 * ├────┼──────┤      │
 * └────┴──────┴──────┘
 * <p>
 * is not broken between pages
 */
public class DefaultRowWrappingFunction implements RowWrappingFunction {


    @Override
    public <T extends PDPage> int[] getWrappableRows(List<Row<T>> rows) {
        int[] wrapableRows = new int[rows.size()];
        wrapableRows[0] = 0;
        int j = 1;
        for (int i = 1; i < rows.size(); i++) {
            Row<?> row = rows.get(i);
            if (isProperTableRowStart(row)) {
                wrapableRows[j++] = i;
            }
        }
        return Arrays.copyOf(wrapableRows, j);
    }

    /*
     * Checks if this is a proper start of a table row,
     * i.e. every cell is visibly closed by a border on top or has no sides anyway.
     *
     * allowed:
     * "┌────┐", "      "
     * not allowed:
     * "│    │", "│     ","     │"
     * */
    private boolean isProperTableRowStart(Row<?> row) {
        if (row.getCells().isEmpty()) {
            return true;
        }
        boolean previousClosed = false;
        for (Cell<?> cell : row.getCells()) {

            if (cell.getTopBorder() == null && (cell.getLeftBorder() != null && !previousClosed)) {
                return false;
            }
            previousClosed = cell.getTopBorder() != null;
        }
        Cell<?> lastCell = row.getCells().get(row.getCells().size() - 1);

        return lastCell.getTopBorder() != null || (lastCell.getRightBorder() == null);
    }

}
