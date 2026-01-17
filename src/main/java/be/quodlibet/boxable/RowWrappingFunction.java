package be.quodlibet.boxable;

import org.apache.pdfbox.pdmodel.PDPage;

import java.util.List;

/**
 * This interface allows you to specify where in the table page breaks may be inserted.
 */
public interface RowWrappingFunction {


	/**
	 * Allows you to specify at which indexes rows that can be moved to the next page are.
	 * @param rows list of rows to consider
	 * @return indexes of rows that may be moved to the next page, if the current page is full
	 */
	<T extends PDPage> int[] getWrappableRows(List<Row<T>> rows);
}
