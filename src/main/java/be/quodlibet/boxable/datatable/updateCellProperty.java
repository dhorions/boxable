package be.quodlibet.boxable.datatable;

import org.apache.pdfbox.pdmodel.PDPage;

import be.quodlibet.boxable.Cell;

public interface updateCellProperty {

	abstract void updateCellPropertysAtColumn(Cell<PDPage> cell, int column, int row);
}