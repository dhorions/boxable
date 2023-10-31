package be.quodlibet.boxable.datatable;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.Table;
import be.quodlibet.boxable.VerticalAlignment;
import be.quodlibet.boxable.line.LineStyle;
import be.quodlibet.boxable.utils.FontUtils;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

/**
 * Write CSV documents directly to PDF Tables
 *
 * @author Dries Horions {@code <dries@quodlibet.be>}
 */
public class DataTable {
	public static final Boolean HASHEADER = true;
	public static final Boolean NOHEADER = false;
	private Table table;
	private List<Float> colWidths;
	private final Cell headerCellTemplate;
	private final List<Cell> dataCellTemplateEvenList = new ArrayList<>();
	private final List<Cell> dataCellTemplateOddList = new ArrayList<>();
	private final Cell defaultCellTemplate;
	private boolean copyFirstColumnCellTemplateOddToEven = false;
	private boolean copyLastColumnCellTemplateOddToEven = false;
	private UpdateCellProperty updateCellProperty = null;

	/**
	 * <p>
	 * Create a CSVTable object to be able to add CSV document to a Table. A
	 * page needs to be passed to the constructor so the Template Cells can be
	 * created.
	 * </p>
	 *
	 * @param table {@link Table}
	 * @param page {@link PDPage}
	 * @throws IOException  If there is an error releasing resources
	 */
	public DataTable(Table table, PDPage page) throws IOException {
		this(table, page, new ArrayList<Float>(),null);
	}

	/**
	 * <p>
	 * Create a CSVTable object to be able to add CSV document to a Table. A
	 * page needs to be passed to the constructor so the Template Cells can be
	 * created.The interface allows you to update the cell property
	 * </p>
	 *
	 * @param table {@link Table}
	 * @param page {@link PDPage}
	 * @param updateCellProperty {@link UpdateCellProperty}
	 * @throws IOException  If there is an error releasing resources
	 */
	public DataTable(Table table, PDPage page, UpdateCellProperty updateCellProperty) throws IOException {
		this(table, page, new ArrayList<Float>(), updateCellProperty);
	}

	/**
	 * <p>
	 * Create a CSVTable object to be able to add CSV document to a Table. A
	 * page needs to be passed to the constructor so the Template Cells can be
	 * created. The column widths can be given
	 * </p>
	 *
	 * @param table {@link Table}
	 * @param page {@link PDPage}
	 * @param colWidths column widths
	 * @throws IOException If there is an error releasing resources
	 */
	public DataTable(Table table, PDPage page, List<Float> colWidths) throws IOException {
		this(table, page, colWidths, null);
	}
	
	/**
	 * <p>
	 * Create a CSVTable object to be able to add CSV document to a Table. A
	 * page needs to be passed to the constructor so the Template Cells can be
	 * created. The column widths can be given and an interface allows you to update the cell property 
	 * </p>
	 *
	 * @param table {@link Table}
	 * @param page {@link PDPage}
	 * @param colWidths column widths
	 * @param updateCellProperty {@link UpdateCellProperty}
	 * @throws IOException If there is an error releasing resources
	 */
	public DataTable(Table table, PDPage page, List<Float> colWidths, UpdateCellProperty updateCellProperty) throws IOException {
		this.table = table;
		this.colWidths = (colWidths.size() == 0) ? null : colWidths;
		this.updateCellProperty = updateCellProperty;
		// Create a dummy pdf document, page and table to create template cells
		PDDocument ddoc = new PDDocument();
		PDPage dpage = new PDPage();
		dpage.setMediaBox(page.getMediaBox());
		dpage.setRotation(page.getRotation());
		ddoc.addPage(dpage);
		BaseTable dummyTable = new BaseTable(10f, 10f, 10f, table.getWidth(), 10f, ddoc, dpage, false, false);
		Row dr = dummyTable.createRow(0f);
		headerCellTemplate = dr.createCell(10f, "A", HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE);
		if (this.colWidths == null) {
			dataCellTemplateEvenList.add(dr.createCell(10f, "A", HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE));
			dataCellTemplateOddList.add(dr.createCell(10f, "A", HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE));
			dataCellTemplateEvenList.add(dr.createCell(10f, "A", HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE));
			dataCellTemplateOddList.add(dr.createCell(10f, "A", HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE));
			dataCellTemplateEvenList.add(dr.createCell(10f, "A", HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE));
			dataCellTemplateOddList.add(dr.createCell(10f, "A", HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE));
		} else {
			for (int i = 0 ; i < this.colWidths.size(); i++) {
				dataCellTemplateEvenList.add(dr.createCell(10f, "A", HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE));
				dataCellTemplateOddList.add(dr.createCell(10f, "A", HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE));
			}
		}
		defaultCellTemplate = dr.createCell(10f, "A", HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE);
		setDefaultStyles();
		ddoc.close();
	}

	/**
	 * <p>
	 * Default cell styles for all cells. By default, only the header cell has a
	 * different style than the rest of the table.
	 * </p>
	 */
	private void setDefaultStyles() {
		LineStyle thinline = new LineStyle(Color.BLACK, 0.75f);
		// Header style
		headerCellTemplate.setFillColor(new Color(137, 218, 245));
		headerCellTemplate.setTextColor(Color.BLACK);
		headerCellTemplate.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
		headerCellTemplate.setBorderStyle(thinline);

		// Normal cell style, all rows and columns are the same by default
		defaultCellTemplate.setFillColor(new Color(242, 242, 242));
		defaultCellTemplate.setTextColor(Color.BLACK);
		defaultCellTemplate.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA));
		defaultCellTemplate.setBorderStyle(thinline);
		Iterator<Cell> iterator = dataCellTemplateEvenList.iterator();
		while (iterator.hasNext()){
			iterator.next().copyCellStyle(defaultCellTemplate);
		}
		iterator = dataCellTemplateOddList.iterator();
		while (iterator.hasNext()){
			iterator.next().copyCellStyle(defaultCellTemplate);
		}
	}

	/**
	 * <p>
	 * Get the table to add the csv content to
	 * </p>
	 *
	 * @return {@link Table}
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * <p>
	 * Set the Table that the CSV document will be added to
	 * </p>
	 *
	 * @param table {@link Table}
	 */
	public void setTable(Table table) {
		this.table = table;
	}

	/**
	 * <p>
	 * return the column widths if provided otherwise null
	 * </p>
	 *
	 * @return column widths
	 */
	public List<Float> getColWidths() {
		return colWidths;
	}

	/**
	 * <p>
	 * Set the column widths
	 * </p>
	 *
	 * @param colWidths
	 */
	public void setColWidths(List<Float> colWidths) {
		this.colWidths = colWidths;
	}

	/**
	 * <p>
	 * Get the Cell Template that will be applied to header cells.
	 * <p>
	 * 
	 * @return header {@link Cell}'s template
	 */
	public Cell getHeaderCellTemplate() {
		return headerCellTemplate;
	}

	/**
	 * <p>
	 * Get the Cell Template that will be assigned to Data cells that are in
	 * even rows, and are not the first or last column
	 * </p>
	 *
	 * @return data {@link Cell}'s template
	 */
	@Deprecated
	public Cell getDataCellTemplateEven() {
		return dataCellTemplateEvenList.get(1);
	}

	/**
	 * <p>
	 * Get the Cell Template that will be assigned to Data cells that are in odd
	 * rows, and are not the first or last column
	 * </p>
	 *
	 * @return data {@link Cell}'s template
	 */
	@Deprecated
	public Cell getDataCellTemplateOdd() {
		return dataCellTemplateOddList.get(1);
	}

	/**
	 * <p>
	 * Get the Cell Templates that will be assigned to Data cells that are in even
	 * rows, and it contain first and last column. 
	 * By default dataCellTemplateEvenList.get(1) will be used for all data even cells
	 * </p>
	 *
	 * @return data {@link Cell}'s template
	 */
	public List<Cell> getDataCellTemplateEvenList() {
		return dataCellTemplateEvenList;
	}

	/**
	 * <p>
	 * Get the Cell Templates that will be assigned to Data cells that are in odd
	 * rows, and it contain first and last column. 
	 * By default dataCellTemplateOddList.get(1) will be used for all data odd cells
	 * </p>
	 *
	 * @return data {@link Cell}'s template
	 */
	public List<Cell> getDataCellTemplateOddList() {
		return dataCellTemplateOddList;
	}

	/**
	 * <p>
	 * Get the Cell Template that will be assigned to cells in the first column
	 * </p>
	 *
	 * @return {@link Cell}'s template
	 */
	public Cell getFirstColumnCellTemplate() {
		copyFirstColumnCellTemplateOddToEven =true;
		return dataCellTemplateOddList.get(0);
	}

	/**
	 * <p>
	 * Get the Cell Template that will be assigned to cells in the last columns
	 * </p>
	 *
	 * @return {@link Cell}'s template
	 */
	public Cell getLastColumnCellTemplate() {
		copyLastColumnCellTemplateOddToEven =true;
		return dataCellTemplateOddList.get(dataCellTemplateOddList.size()-1);
	}

	/**
	 * <p>
	 * Get the Cell Template that will be assigned to cells in the first column that are in
	 * odd rows
	 * </p>
	 *
	 * @return {@link Cell}'s template
	 */
	public Cell getFirstColumnCellTemplateOdd() {
		copyFirstColumnCellTemplateOddToEven = false;
		return dataCellTemplateOddList.get(0);
	}

	/**
	 * <p>
	 * Get the Cell Template that will be assigned to cells in the last columns that are in
	 * odd rows
	 * </p>
	 *
	 * @return {@link Cell}'s template
	 */
	public Cell getLastColumnCellTemplateOdd() {
		copyLastColumnCellTemplateOddToEven = false;
		return dataCellTemplateOddList.get(dataCellTemplateOddList.size()-1);
	}

	/**
	 * <p>
	 * Get the Cell Template that will be assigned to cells in the first column that are in
	 * even rows
	 * </p>
	 *
	 * @return {@link Cell}'s template
	 */
	public Cell getFirstColumnCellTemplateEven() {
		copyFirstColumnCellTemplateOddToEven = false;
		return dataCellTemplateEvenList.get(0);
	}

	/**
	 * <p>
	 * Get the Cell Template that will be assigned to cells in the last columns that are in
	 * even rows
	 *
	 * @return {@link Cell}'s template
	 */
	public Cell getLastColumnCellTemplateEven() {
		copyLastColumnCellTemplateOddToEven = false;
		return dataCellTemplateEvenList.get(dataCellTemplateOddList.size()-1);
	}

	/**
	 * <p>
	 * Add a List of Lists to the Table
	 * </p>
	 *
	 * @param data {@link Table}'s data
	 * @param hasHeader boolean if {@link Table} has header
	 * @throws IOException parsing error
	 */
	public void addListToTable(List<List> data, Boolean hasHeader) throws IOException {
		if (data == null || data.isEmpty()) {
			return;
		}
		StringBuilder output = new StringBuilder();
		char separator = ';';

		// Convert Map of arbitrary objects to a csv String
		for (List<? extends Object> inputList : data) {
			StringBuilder row = new StringBuilder();
			for (Object v : inputList) {
				String value = v.toString();
				if (value.contains("" + separator)) {
					// surround value with quotes if it contains the escape
					// character
					value = "\"" + value + "\"";
				}
				value = value.replaceAll("\n", "<br>");
				row.append(value).append(separator);
			}
			// remove the last separator
			row = new StringBuilder(row.substring(0, row.length() - 1));
			output.append(row).append("\n");
		}

		addCsvToTable(output.toString(), hasHeader, separator);
	}

	/**
	 * <p>
	 * Add a String representing a CSV document to the Table
	 * </p>
	 *
	 * @param data {@link Table}'s data
	 * @param hasHeader boolean if {@link Table} has header
	 * @param separator {@code char} on which data will be parsed
	 * @throws IOException parsing error
	 */
	public void addCsvToTable(String data, Boolean hasHeader, char separator) throws IOException {
		Iterable<CSVRecord> records = CSVParser.parse(data, CSVFormat.EXCEL.withDelimiter(separator));
		Boolean isHeader = hasHeader;
		Boolean isFirst = true;
		Boolean odd = true;
		Map<Integer, Float> colWidths = new HashMap<>();
		int numcols = 0;
		int numrow = 0;
		for (CSVRecord line : records) {

			if (isFirst) {

				// calculate the width of the columns
				float totalWidth = 0.0f;
				if (this.colWidths == null) {
                    
					for (int i = 0; i < line.size(); i++) {
						String cellValue = line.get(i);
						float textWidth = FontUtils.getStringWidth(headerCellTemplate.getFont(), " " + cellValue + " ",
								headerCellTemplate.getFontSize());
						totalWidth += textWidth;
						numcols = i;
					}
					// totalWidth has the total width we need to have all
					// columns
					// full sized.
					// calculate a factor to reduce/increase size by to make it
					// fit
					// in our table
					float sizefactor = table.getWidth() / totalWidth;
					for (int i = 0; i <= numcols; i++) {
						String cellValue = "";
						if (line.size() >= i) {
							cellValue = line.get(i);
						}
						float textWidth = FontUtils.getStringWidth(headerCellTemplate.getFont(), " " + cellValue + " ",
								headerCellTemplate.getFontSize());
						float widthPct = textWidth * 100 / table.getWidth();
						// apply width factor
						widthPct = widthPct * sizefactor;
						colWidths.put(i, widthPct);
					}
				} else {
					for (Float width : this.colWidths){
						totalWidth += width;
					}
					for (int i = 0; i < this.colWidths.size(); i++) {
						// to
						// percent
						colWidths.put(i,this.colWidths.get(i) / (totalWidth / 100));
						numcols = i;
					}

				}
				updateTemplateList(line.size());
				isFirst = false;
			}
			if (isHeader) {
				// Add Header Row
				Row h = table.createRow(headerCellTemplate.getCellHeight());
				for (int i = 0; i <= numcols; i++) {
					String cellValue = line.get(i);
					Cell c = h.createCell(colWidths.get(i), cellValue, headerCellTemplate.getAlign(),
							headerCellTemplate.getValign());
					// Apply style of header cell to this cell
					c.copyCellStyle(headerCellTemplate);
					c.setText(cellValue);
				}
				table.addHeaderRow(h);
				isHeader = false;
			} else {
				Row r = table.createRow(dataCellTemplateEvenList.get(0).getCellHeight());
				for (int i = 0; i <= numcols; i++) {
					// Choose the correct template for the cell
					Cell template = dataCellTemplateEvenList.get(i);
					if (odd) {
						template = dataCellTemplateOddList.get(i);;
					}
					
					String cellValue = "";
					if (line.size() >= i) {
						cellValue = line.get(i);
					}
					Cell c = r.createCell(colWidths.get(i), cellValue, template.getAlign(), template.getValign());
					// Apply style of header cell to this cell
					c.copyCellStyle(template);
					c.setText(cellValue);
					if (updateCellProperty != null)
						updateCellProperty.updateCellPropertiesAtColumn(c,i,numrow);
				}
				numrow++;
			}
			odd = !odd;
		}
	}

	private void updateTemplateList(int size) {
		if (copyFirstColumnCellTemplateOddToEven)
			dataCellTemplateEvenList.set(0, dataCellTemplateOddList.get(0));
		if (copyLastColumnCellTemplateOddToEven)
			dataCellTemplateEvenList.set(dataCellTemplateEvenList.size()-1, dataCellTemplateOddList.get(dataCellTemplateOddList.size()-1));
		if (size <= 3) return; // Only in case of more than 3 columns there are first last and data template 
		while (dataCellTemplateEvenList.size() < size) dataCellTemplateEvenList.add(1,dataCellTemplateEvenList.get(1) );
		while (dataCellTemplateOddList.size() < size) dataCellTemplateOddList.add(1,dataCellTemplateOddList.get(1) );
		while (dataCellTemplateEvenList.size() > size) dataCellTemplateEvenList.remove(dataCellTemplateEvenList.size()-2 );
		while (dataCellTemplateOddList.size() >size) dataCellTemplateOddList.remove(dataCellTemplateOddList.size()-2 );
		
	}
}