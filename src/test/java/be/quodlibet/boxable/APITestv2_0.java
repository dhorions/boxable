package be.quodlibet.boxable;

import be.quodlibet.boxable.layout.DefaultCellLayouter;
import be.quodlibet.boxable.layout.MatrixCellLayouter;
import be.quodlibet.boxable.layout.VerticalZebraCellLayouter;
import be.quodlibet.boxable.layout.ZebraCellLayouter;
import be.quodlibet.boxable.layout.style.DefaultStyle;
import be.quodlibet.boxable.layout.style.DefaultStyle.Styles;
import be.quodlibet.boxable.line.LineStyle;
import be.quodlibet.boxable.page.DefaultPageProvider;
import com.google.common.io.Files;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Dries Horions <dries@quodlibet.be>
 */
public class APITestv2_0
{
    //Initialize Document

    static PDDocument doc;
    public APITestv2_0()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
        doc = new PDDocument();
    }

    @AfterClass
    public static void tearDownClass()
    {
        try {
            saveDocument("target/APIv2.0.pdf");
        }
        catch (IOException ex) {
            Logger.getLogger(APITestv2_0.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }
    /*
     private static PDPage addNewPage()
    {
        PDPage page = new PDPage();
        doc.addPage(page);
        return page;
    }*/

    private static void saveDocument(String fileName) throws IOException
    {
        //Save the document
        File file = new File(fileName);
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        Files.createParentDirs(file);
        doc.save(file);
        doc.close();
    }

    /**
     * Test for API v2.0 SoC Release
     */
     @Test
    public void apiv2_0Test1()
    {
        //PDPage p = addNewPage();
        BaseTable table = new BaseTable();
        Row<PDPage> headerRow = table.createRow();
        headerRow.createCell(100, "Test 1 - Print table multiple times to document");
        table.addHeaderRow(headerRow);
        headerRow = table.createRow(
                new ArrayList<>(
                        Arrays.asList(
                                new Cell(10, "Column 1"),
                                new Cell(10, "Column 2"),
                                new Cell(80, "Column 3")
                        )));

        table.addHeaderRow(headerRow);
        //Non header rows will inherit their width from the last header row.
        //Any arbitrary collection can be used, the String representation will be used.
        for (int i = 1; i <= 3; i++) {
            Row<PDPage> row = table.createRow(
                    new ArrayList<>(
                            Arrays.asList(
                                    "Row " + i + " col 1",
                                    "Row " + i + " col 2",
                                    "Row " + i + " col 3")
                    ));
        }
        //Add 1 row with only 2 columns and a differently Styled cells
        table.createRow(
                new ArrayList<>(
                        Arrays.asList(
                                new Cell(80, "Wider Column")
                                .withFont(PDType1Font.TIMES_BOLD)
                                .withFillColor(Color.LIGHT_GRAY),
                                new Cell(20, "Value")
                                .withFont(PDType1Font.TIMES_BOLD)
                                .withFillColor(Color.red)
                                .withTextColor(Color.WHITE)
                        )));
        //Assign table to pageProvider
        DefaultPageProvider provider = new DefaultPageProvider(doc, PDRectangle.A4);
        try {
            table.draw(provider);
            //Draw it a second time
            table.getRows().get(0).getCells().get(0).setText("Test 1 -  Same table second time on same page");
            table.draw(provider);
            //Use the same table, but with different title, of a different size page (A4 landscape)
            //provider = new DefaultPageProvider(doc, new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
            provider.setSize(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
            provider.nextPage();
            table.getRows().get(0).getCells().get(0).setText("Test 1 -  Same table third time, on a different size page");
            //Draw it a third time on the next page
            float bottom = table.draw(provider);
            //draw ith a fourth time on the next page, but a little lower
            table.draw(bottom - 100, provider);

        }
        catch (IOException ex) {
            //Writing to a pdf page can always return a IOException because of
            //https://pdfbox.apache.org/docs/2.0.0/javadocs/org/apache/pdfbox/pdmodel/PDPageContentStream.html#PDPageContentStream(org.apache.pdfbox.pdmodel.PDDocument,%20org.apache.pdfbox.pdmodel.PDPage,%20org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode,%20boolean,%20boolean)
            Logger.getLogger(APITestv2_0.class.getName()).log(Level.SEVERE, null, ex);
        }



    }

    @Test
    public void apiv2_0Test2()
    {
        BaseTable table = new BaseTable();
        //The Header Row, width % is mandatory for this row
        Row<PDPage> headerRow = table.createRow();
        headerRow.createCell(100, "Test 2 - Multiple Pages");
        table.addHeaderRow(headerRow);
        headerRow = table.createRow();
        headerRow.createCell((float) 100 / 3, "Column 1");
        headerRow.createCell((float) 100 / 3, "Column 2");
        headerRow.createCell((float) 100 / 3, "Column 3");
        table.addHeaderRow(headerRow);

        //Other Rows, if no width is passed, will inherit from last header row
        for (int i = 1; i <= 100; i++) {
            Row<PDPage> row = table.createRow();
            row.createCell("Column 1 row " + i);
            row.createCell("Column 2 row " + i);
            row.createCell("Column 3 row " + i);

        }

        //Assign table to page
        DefaultPageProvider provider = new DefaultPageProvider(doc, PDRectangle.A4);
        provider.nextPage();
        //Writing to a pdf page can always return a IOException because of
        //https://pdfbox.apache.org/docs/2.0.0/javadocs/org/apache/pdfbox/pdmodel/PDPageContentStream.html#PDPageContentStream(org.apache.pdfbox.pdmodel.PDDocument,%20org.apache.pdfbox.pdmodel.PDPage,%20org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode,%20boolean,%20boolean)
        try {
            table.draw(provider);
        }
        catch (IOException ex) {
            Logger.getLogger(APITestv2_0.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Test
    public void apiv2_0Test3()
    {
        BaseTable table = new BaseTable();
        //The Header Row, width % is mandatory for this row
        Row<PDPage> headerRow = table.createRow();
        headerRow.createCell(100, "Test 3 - Auto Calculated Column Width");
        table.addHeaderRow(headerRow);
        //No widths are passed for the columns, so they should be auto calcilated
        headerRow = table.createRow(                new ArrayList<>(
                        Arrays.asList(
                                "Column 1 is the widest column of them all",
                                "Column 2 is narrower",
                                "Column 3 narrowest"
                        )), Row.HEADER);


        //Non header rows will inherit their width from the last header row.
        //Any arbitrary collection can be used, the String representation will be used.
        for (int i = 1; i <= 3; i++) {
            Row<PDPage> row = table.createRow(
                    new ArrayList<>(
                            Arrays.asList(
                                    "Row " + i + " col 1",
                                    "Row " + i + " col 2",
                                    "Row " + i + " col 3<br/>Only the last header column determines the actual width of the columns, the rest will be wrapped as needed.")
                    ));
        }

        //Assign table to pageProvider
        DefaultPageProvider provider = new DefaultPageProvider(doc, PDRectangle.A4);
        provider.nextPage();
        try {
            table.draw(provider);
        }
        catch (IOException ex) {
            //Writing to a pdf page can always return a IOException because of
            //https://pdfbox.apache.org/docs/2.0.0/javadocs/org/apache/pdfbox/pdmodel/PDPageContentStream.html#PDPageContentStream(org.apache.pdfbox.pdmodel.PDDocument,%20org.apache.pdfbox.pdmodel.PDPage,%20org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode,%20boolean,%20boolean)
            Logger.getLogger(APITestv2_0.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Test
    public void apiv2_0Test4()
    {
        BaseTable table = new BaseTable();
        //The Header Row, width % is mandatory for this row
        Row<PDPage> headerRow = table.createRow();
        headerRow.createCell(100, "Test 4 - Theming");
        table.addHeaderRow(headerRow);
        //No widths are passed for the columns, so they should be auto calcilated
        headerRow = table.createRow(
                new ArrayList<>(
                        Arrays.asList(
                                "XvsY",
                                "A",
                                "B",
                                "C",
                                "D",
                                "E",
                                "F"
                        )), Row.HEADER);

        //Non header rows will inherit their width from the last header row.
        //Any arbitrary collection can be used, the String representation will be used.
        for (int i = 1; i <= 6; i++) {
             table.createRow(
                    new ArrayList<>(
                            Arrays.asList(
                                    "" + i,
                                    "A" + i,
                                    "B" + i,
                                    "C" + i,
                                    "D" + i,
                                    "E" + i,
                                    "F" + i)
                    ));
        }

        //Assign table to pageProvider
        DefaultPageProvider provider = new DefaultPageProvider(doc, PDRectangle.A4);
        provider.nextPage();

        try {
            //Draw table once with default Style
            table.getRows().get(0).getCells().get(0).setText("Test 4 -  Default Style");
            table.addLayouter(new DefaultCellLayouter(Styles.DEFAULT));
            table.draw(provider);
            //Green Style with Zebra Layouter
            table.getRows().get(0).getCells().get(0).setText("Test 4 -  Green Style with Zebra Layouter");
            table.clearLayouters()
                    .addLayouter(new DefaultCellLayouter(Styles.GREEN))
                    .addLayouter(new ZebraCellLayouter(Styles.GREEN))
                    .draw(provider);

            //Candy Style with Vertical Zebra Layouter
            table.getRows().get(0).getCells().get(0).setText("Test 4 -  Candy Style with Vertical Zebra Layouter");
            table.clearLayouters()
                    .addLayouter(new DefaultCellLayouter(Styles.CANDY))
                    .addLayouter(new VerticalZebraCellLayouter(Styles.CANDY))
                    .draw(provider);

            //Default Style with  Zebra Layouter and Matrix Layouter
            table.getRows().get(0).getCells().get(0).setText("Test 4 -  Default Style with Zebra Layouter and Matrix Layouter and centered headers");
            table.clearLayouters()
                    .addLayouter(new DefaultCellLayouter(Styles.DEFAULT))
                    .addLayouter(new ZebraCellLayouter(Styles.DEFAULT))
                    .addLayouter(new MatrixCellLayouter(Styles.DEFAULT))
                    .draw(provider);

            //Custom style
            table.pageBreak();
            table.getRows().get(0).getCells().get(0).setText("Test 4 -  Custom Style");
            DefaultStyle customStyle = new DefaultStyle()
                    .withBorder(new LineStyle(Color.ORANGE, (float) 0.5))
                    .withFont(PDType1Font.COURIER)
                    .withAlignAccent2(HorizontalAlignment.CENTER);
            table.clearLayouters()
                    .addLayouter(new DefaultCellLayouter(customStyle))
                    .draw();

        }
        catch (IOException ex) {
            //Writing to a pdf page can always return a IOException because of
            //https://pdfbox.apache.org/docs/2.0.0/javadocs/org/apache/pdfbox/pdmodel/PDPageContentStream.html#PDPageContentStream(org.apache.pdfbox.pdmodel.PDDocument,%20org.apache.pdfbox.pdmodel.PDPage,%20org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode,%20boolean,%20boolean)
            Logger.getLogger(APITestv2_0.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
