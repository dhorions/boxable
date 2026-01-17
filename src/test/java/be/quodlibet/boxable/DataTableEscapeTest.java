package be.quodlibet.boxable;

import be.quodlibet.boxable.datatable.DataTable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Test;

public class DataTableEscapeTest {

    @Test
    public void testAddListToTableWithSpecialCharacters() throws IOException {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        BaseTable baseTable = new BaseTable(100, 100, 10, 500, 10, doc, page, true, true);
        DataTable dataTable = new DataTable(baseTable, page);

        List<List> data = new ArrayList<>();
        // This string contains a semicolon which is the separator, so it needs proper escaping
        // The current implementation might double quote it, but complex cases like quotes inside quotes might fail
        // or just plain separators being handled poorly.
        // Let's try a case that requires robust escaping: "Text with ; separator"
        data.add(Arrays.asList("Header1", "Header2"));
        data.add(Arrays.asList("Normal Value", "Value;With \"Quotes\" and ; Separators"));

        // Use addListToTable which internally converts List -> CSV String -> addCsvToTable
        // If the CSV string generation is bad, addCsvToTable might fail parsing or split incorrectly
        dataTable.addListToTable(data, true);
        
        doc.close();
    }
}
