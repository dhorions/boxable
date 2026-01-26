package be.quodlibet.boxable;

import be.quodlibet.boxable.datatable.DataTable;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Test;

public class DataTableMissingColumnTest {

    @Test
    public void testMissingColumnInCSV() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        BaseTable baseTable = new BaseTable(100, 100, 10, 500, 10, doc, page, true, true);
        DataTable dataTable = new DataTable(baseTable, page);

        // CSV data: Header has 2 columns. Second row has 1 column.
        String csvData = "Col1;Col2\nValue1"; 
        
        // This should throw ArrayIndexOutOfBoundsException if the bug exists
        dataTable.addCsvToTable(csvData, true, ';');
        
        doc.close();
    }
}
