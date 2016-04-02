package be.quodlibet.boxable.csv;

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
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 *
 * @author Dries Horions <dries@quodlibet.be>
 */
public class CSVTable
{
    public static final Boolean HASHEADER = true;
    public static final Boolean NOHEADER = false;
    private Table table;
    private Cell headerCellTemplate;
    private Cell dataCellTemplateEven;
    private Cell dataCellTemplateOdd;
    private Cell firstColumnCellTemplate;
    private Cell lastColumnCellTemplate;
    public CSVTable(Table table, PDPage page) throws IOException
    {
        this.table = table;
        //Create a dummy pdf document, page and table to create template cells
        PDDocument ddoc = new PDDocument();
        PDPage dpage = new PDPage();
        dpage.setMediaBox(page.getMediaBox());
        dpage.setRotation(page.getRotation());
        ddoc.addPage(dpage);
        BaseTable dummyTable = new BaseTable(10f, 10f, 10f, table.getWidth(), 10f, ddoc, dpage, false,
                                             false);
        Row dr = dummyTable.createRow(0f);
        headerCellTemplate = dr.createCell(10f, "A", HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE);
        dataCellTemplateEven = dr.createCell(10f, "A", HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE);
        dataCellTemplateOdd = dr.createCell(10f, "A", HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE);
        firstColumnCellTemplate = dr.createCell(10f, "A", HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE);
        lastColumnCellTemplate = dr.createCell(10f, "A", HorizontalAlignment.LEFT, VerticalAlignment.MIDDLE);
        setDefaultStyles();
        ddoc.close();
    }

    private void setDefaultStyles()
    {
        LineStyle thinline = new LineStyle(Color.DARK_GRAY, 0.5f);
        //Header style
        headerCellTemplate.setFillColor(new Color(137, 218, 245));
        headerCellTemplate.setTextColor(Color.BLACK);
        headerCellTemplate.setFont(PDType1Font.HELVETICA_BOLD);
        headerCellTemplate.setLeftBorderStyle(thinline);

        //Normal cell style, all rows and columns are the same by default
        dataCellTemplateEven.setFillColor(new Color(242, 242, 242));
        dataCellTemplateEven.setTextColor(Color.BLACK);
        dataCellTemplateEven.setFont(PDType1Font.HELVETICA);
        dataCellTemplateEven.setLeftBorderStyle(thinline);

        dataCellTemplateOdd.setFillColor(new Color(242, 242, 242));
        dataCellTemplateOdd.setTextColor(Color.BLACK);
        dataCellTemplateOdd.setFont(PDType1Font.HELVETICA);
        dataCellTemplateOdd.setLeftBorderStyle(thinline);

        firstColumnCellTemplate.setFillColor(new Color(242, 242, 242));
        firstColumnCellTemplate.setTextColor(Color.BLACK);
        firstColumnCellTemplate.setFont(PDType1Font.HELVETICA);
        firstColumnCellTemplate.setLeftBorderStyle(thinline);

        lastColumnCellTemplate.setFillColor(new Color(242, 242, 242));
        lastColumnCellTemplate.setTextColor(Color.BLACK);
        lastColumnCellTemplate.setFont(PDType1Font.HELVETICA);
        lastColumnCellTemplate.setLeftBorderStyle(thinline);
    }


    /**
     * Set the table to add the csv content to
     *
     * @return
     */
    public Table getTable()
    {
        return table;
    }

    public void setTable(Table table)
    {
        this.table = table;
    }

    public Cell getHeaderCellTemplate()
    {
        return headerCellTemplate;
    }

    public Cell getDataCellTemplateEven()
    {
        return dataCellTemplateEven;
    }

    public Cell getDataCellTemplateOdd()
    {
        return dataCellTemplateOdd;
    }

    public Cell getFirstColumnCellTemplate()
    {
        return firstColumnCellTemplate;
    }

    public Cell getLastColumnCellTemplate()
    {
        return lastColumnCellTemplate;
    }


    public void addCsvToTable(String data, Boolean hasHeader, char separator) throws IOException
    {
        Iterable<CSVRecord> records = CSVParser.parse(data, CSVFormat.EXCEL.withDelimiter(separator));
        Boolean isHeader = hasHeader;
        Boolean isFirst = true;
        Boolean odd = true;
        Map<Integer, Float> colWidths = new HashMap();
        int numcols = 0;
        for (CSVRecord line : records) {

            if (isFirst) {


                //calculate the width of the columns
                float totalPct = 0.0f;
                float totalWidth = 0.0f;
                for (int i = 0; i < line.size(); i++) {
                    String cellValue = line.get(i);
                    float textWidth = FontUtils.getStringWidth(headerCellTemplate.getFont(), " " + cellValue + " ", headerCellTemplate.getFontSize());
                    float widthPct = textWidth * 100 / table.getWidth();
                    totalPct += widthPct;
                    totalWidth += textWidth;
                    numcols = i;
                }
                //now totalPct has the total % of width we need to have all columns full sized.
                //now calculate a factor to reduce size by to make it fit
                float sizefactor = 100.0f / totalPct;
                sizefactor = table.getWidth() / totalWidth;
                for (int i = 0; i <= numcols; i++) {
                    String cellValue = "";
                    if (line.size() >= i) {
                        cellValue = line.get(i);
                    }
                    float textWidth = FontUtils.getStringWidth(headerCellTemplate.getFont(), " " + cellValue + " ", headerCellTemplate.getFontSize());
                    float widthPct = textWidth * 100 / table.getWidth();
                    //apply width factor
                    widthPct = widthPct * sizefactor;
                    colWidths.put(i, widthPct);
                }
                isFirst = false;
            }
            if (isHeader) {
                //Add Header Row
                Row h = table.createRow(headerCellTemplate.getCellHeight());
                for (int i = 0; i <= numcols; i++) {
                    String cellValue = line.get(i);
                    Cell c = h.createCell(colWidths.get(i), cellValue, headerCellTemplate.getAlign(), headerCellTemplate.getValign());
                    //Apply style of header cell to this cell
                    c.copyCellStyle(headerCellTemplate);
                    c.setText(cellValue);
                }
                table.addHeaderRow(h);
                isHeader = false;
            }
            else {
                Row r = table.createRow(dataCellTemplateEven.getCellHeight());
                for (int i = 0; i <= numcols; i++) {
                    String cellValue = "";
                    if (line.size() >= i) {
                        cellValue = line.get(i);
                    }
                    //Choose the correct template for the cell
                    Cell template = dataCellTemplateEven;
                    if (odd) {
                        template = dataCellTemplateOdd;
                    }
                    if (i == 0) {
                        template = firstColumnCellTemplate;
                    }
                    if (i == numcols) {
                        template = lastColumnCellTemplate;
                    }
                    Cell c = r.createCell(colWidths.get(i), cellValue, template.getAlign(), template.getValign());
                    //Apply style of header cell to this cell
                    c.copyCellStyle(template);
                    c.setText(cellValue);
                }
            }
            odd = !odd;
        }
    }
}
