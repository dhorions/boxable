package be.quodlibet.boxable;

import be.quodlibet.boxable.datatable.DataTable;
import com.google.common.io.Files;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Dries Horions <dries@quodlibet.be>
 */
public class DataTableTest
{

    public DataTableTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void listTestLandscape() throws IOException
    {



        //Initialize Document
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        //Create a landscape page
        page.setMediaBox(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
        doc.addPage(page);
        //Initialize table
        float margin = 10;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = 0;

        //Create the data
        List<List> data = new ArrayList();
        data.add(new ArrayList<>(
                Arrays.asList("Column One", "Column Two", "Column Three", "Column Four", "Column Five")));
        for (int i = 1; i <= 100; i++) {
            data.add(new ArrayList<>(
                    Arrays.asList("Row " + i + " Col One", "Row " + i + " Col Two", "Row " + i + " Col Three", "Row " + i + " Col Four", "Row " + i + " Col Five")));
        }

        BaseTable dataTable = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true,
                                            true);
        DataTable t = new DataTable(dataTable, page);
        t.addListToTable(data, DataTable.HASHEADER);
        dataTable.draw();
        File file = new File("target/ListExampleLandscape.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        Files.createParentDirs(file);
        doc.save(file);
        doc.close();
    }

    @Test
    public void csvTestColWidths() throws IOException
    {
        String data = readData("https://s3.amazonaws.com/misc.quodlibet.be/Boxable/teknologic.csv");
        //Initialize Document
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        //Create a landscape page
        page.setMediaBox(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
        doc.addPage(page);
        //Initialize table
        float margin = 10;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = 20;

        BaseTable dataTable = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true,
                                            true);
        DataTable t = new DataTable(dataTable, page);
        t.addCsvToTable(data, DataTable.HASHEADER, ';');
        dataTable.draw();
        File file = new File("target/CSVexampleColWidths.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        Files.createParentDirs(file);
        doc.save(file);
        doc.close();
    }

    @Test
    public void csvTestPortrait() throws IOException
    {
        String data = readData("https://s3.amazonaws.com/misc.quodlibet.be/Boxable/Eurostat_Immigration_Applications.csv");
        //Initialize Document
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);
        //Initialize table
        float margin = 10;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = 0;

        BaseTable dataTable = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true,
                                            true);
        DataTable t = new DataTable(dataTable, page);
        t.addCsvToTable(data, DataTable.HASHEADER, ';');
        dataTable.draw();
        File file = new File("target/CSVexamplePortrait.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        Files.createParentDirs(file);
        doc.save(file);
        doc.close();
    }

    @Test
    public void csvTestLandscape() throws IOException
    {
        String data = readData("https://s3.amazonaws.com/misc.quodlibet.be/Boxable/Eurostat_Immigration_Applications.csv");
        //Initialize Document
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        //Create a landscape page
        page.setMediaBox(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
        doc.addPage(page);
        //Initialize table
        float margin = 10;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = 0;

        BaseTable dataTable = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true,
                                            true);
        DataTable t = new DataTable(dataTable, page);
        t.addCsvToTable(data, DataTable.HASHEADER, ';');
        dataTable.draw();
        File file = new File("target/CSVexampleLandscape.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        Files.createParentDirs(file);
        doc.save(file);
        doc.close();
    }

    @Test
    public void csvTestSimple() throws IOException
    {

        String data = readData("https://s3.amazonaws.com/misc.quodlibet.be/Boxable/Eurostat_Energcy_Prices_Medium_Household.csv");
        //Initialize Document
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        //Create a landscape page
        page.setMediaBox(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
        doc.addPage(page);
        //Initialize table
        float margin = 10;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = 0;

        BaseTable dataTable = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true,
                                            true);
        //Add a few things to the table that's not coming from the csv file
        Row h1 = dataTable.createRow(0f);
        Cell c1 = h1.createCell(100, "Electricity Prices by type of user");
        c1.setFillColor(new Color(144, 195, 212));
        dataTable.addHeaderRow(h1);
        Row h2 = dataTable.createRow(0f);
        Cell c2 = h2.createCell(100, "Eur per kWh for Medium Size Households.<br/>Source <i>http://ec.europa.eu/eurostat/tgm/table.do?tab=table&init=1&plugin=1&language=en&pcode=ten00117</i>");
        c2.setFillColor(new Color(175, 212, 224));
        dataTable.addHeaderRow(h2);
        DataTable t = new DataTable(dataTable, page);
        t.addCsvToTable(data, DataTable.HASHEADER, ';');
        dataTable.draw();
        File file = new File("target/CSVexampleSimple.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        Files.createParentDirs(file);
        doc.save(file);
        doc.close();

    }

     @Test
    public void csvTestAdvanced() throws IOException
    {

        String data = readData("https://s3.amazonaws.com/misc.quodlibet.be/Boxable/Eurostat_Energcy_Prices_Medium_Household.csv");
        //Initialize Document
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        //Create a landscape page
        page.setMediaBox(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
        doc.addPage(page);
        //Initialize table
        float margin = 10;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = 0;

        BaseTable dataTable = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true,
                                            true);
        //Add a few things to the table that's not coming from the csv file
        Row h1 = dataTable.createRow(0f);
        Cell c1 = h1.createCell(100, "Electricity Prices by type of user");
        c1.setFillColor(new Color(144, 195, 212));
        dataTable.addHeaderRow(h1);
        Row h2 = dataTable.createRow(0f);
        Cell c2 = h2.createCell(100, "Eur per kWh for Medium Size Households.<br/>Source <i>http://ec.europa.eu/eurostat/tgm/table.do?tab=table&init=1&plugin=1&language=en&pcode=ten00117</i>");
        c2.setFillColor(new Color(175, 212, 224));
        dataTable.addHeaderRow(h2);
        DataTable t = new DataTable(dataTable, page);
        //set the style template for header cells
        t.getHeaderCellTemplate().setFillColor(new Color(13, 164, 214));
        //set the style template for first column
        t.getFirstColumnCellTemplate().setFillColor(new Color(13, 164, 214));
        //set the style template for last column
        t.getLastColumnCellTemplate().setFillColor(new Color(144, 195, 212));
        //set the style template for normal, data columns
        t.getDataCellTemplateEven().setFillColor(Color.WHITE);
        t.getDataCellTemplateOdd().setFillColor(new Color(250, 242, 242));

        t.addCsvToTable(data, DataTable.HASHEADER, ';');

        dataTable.draw();
        File file = new File("target/CSVexampleAdvanced.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        Files.createParentDirs(file);
        doc.save(file);
        doc.close();
    }

    private static String readData(String url)
    {
        InputStream in = null;
        try {
            in = new URL(url).openStream();
            return IOUtils.toString(in);
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        finally {
            IOUtils.closeQuietly(in);
        }
        return "";
    }

    private static PDPage addNewPage(PDDocument doc)
    {
        PDPage page = new PDPage();
        doc.addPage(page);
        return page;
    }
}
