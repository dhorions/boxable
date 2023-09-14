package be.quodlibet.boxable.datatable;

import org.apache.pdfbox.pdmodel.PDPage;

import be.quodlibet.boxable.Cell;

/**
 * Allows changing the cell properties, while the CSV documents is written directly into the PDF tables
 *
 * @author Christoph Schemmelmann {@code <CSchemmy@gmx.de>}
 */
public interface UpdateCellProperty {

    void updateCellPropertiesAtColumn(Cell<PDPage> cell, int column, int row);
}