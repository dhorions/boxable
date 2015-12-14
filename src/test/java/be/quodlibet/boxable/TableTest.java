/*
 Quodlibet.be
 */
package be.quodlibet.boxable;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.junit.Test;

import com.google.common.io.Files;


public class TableTest {

    @Test
    public void Sample1 () throws IOException {

        //Set margins
        float margin = 10;


        List<String[]> facts = getFacts();

        //Initialize Document
        PDDocument doc = new PDDocument();
        PDPage page = addNewPage(doc);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);

        //Initialize table
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        boolean drawContent = true;
        float yStart = yStartNewPage;
        float bottomMargin = 70;
        BaseTable table  = new BaseTable(yStart,yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, drawContent);

        //Create Header row
        Row<PDPage> headerRow = table.createRow(15f);
        Cell<PDPage> cell = headerRow.createCell(100, "Awesome Facts About Belgium");
        cell.setFont(PDType1Font.HELVETICA_BOLD);
        cell.setFillColor(Color.BLACK);
        cell.setTextColor(Color.WHITE);


        table.setHeader(headerRow);

        //Create 2 column row
        Row<PDPage> row = table.createRow(15f);
        cell = row.createCell(30,"Source:");
        cell.setFont(PDType1Font.HELVETICA);

        cell = row.createCell(70, "http://www.factsofbelgium.com/");
        cell.setFont(PDType1Font.HELVETICA_OBLIQUE);

        //Create Fact header row
        Row<PDPage> factHeaderrow = table.createRow(15f);

        cell = factHeaderrow.createCell((100 / 3) * 2, "Fact");
        cell.setFont(PDType1Font.HELVETICA);
        cell.setFontSize(6);
        cell.setFillColor(Color.LIGHT_GRAY);

        cell = factHeaderrow.createCell((100 / 3), "Tags");
        cell.setFillColor(Color.LIGHT_GRAY);
        cell.setFont(PDType1Font.HELVETICA_OBLIQUE);
        cell.setFontSize(6);

        //Add multiple rows with random facts about Belgium
        for (String[] fact : facts) {

            row = table.createRow(10f);
            cell = row.createCell((100 / 3) * 2, fact[0]);
            cell.setFont(PDType1Font.HELVETICA);
            cell.setFontSize(6);

            for (int i = 1; i < fact.length; i++) {

                cell = row.createCell((100 / 9), fact[i]);
                cell.setFont(PDType1Font.HELVETICA_OBLIQUE);
                cell.setFontSize(6);
                //Set colors
                if (fact[i].contains("beer")) cell.setFillColor(Color.yellow);
                if (fact[i].contains("champion")) cell.setTextColor(Color.GREEN);
            }
        }

        table.draw();

        //Close Stream and save pdf
        File file = new File("target/BoxableSample1.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        Files.createParentDirs(file);
        doc.save(file);
        doc.close();

    }



    private static List<String[]> getFacts() {
        List<String[]> facts = new ArrayList<String[]>();
        facts.add(new String[]{"Oil Painting was invented by the Belgian van Eyck brothers", "art", "inventions", "science"});
        facts.add(new String[]{"The Belgian Adolphe Sax invented the Saxophone", "inventions", "music", ""});
        facts.add(new String[]{"11 sites in Belgium are on the UNESCO World Heritage List", "art", "history", ""});
        facts.add(new String[]{"Belgium was the second country in the world to legalize same-sex marriage", "politics", "", ""});
        facts.add(new String[]{"In the seventies, schools served light beer during lunch", "health", "school", "beer"});
        facts.add(new String[]{"Belgium has the sixth fastest domestic internet connection in the world", "science", "technology", ""});
        facts.add(new String[]{"Belgium hosts the World's Largest Sand Sculpture Festival", "art", "festivals", "world championship"});
        facts.add(new String[]{"Belgium has compulsary education between the ages of 6 and 18", "education", "", ""});
        facts.add(new String[]{"Belgium also has more comic makers per square kilometer than any other country in the world", "art", "social", "world championship"});
        facts.add(new String[]{"Belgium has one of the lowest proportion of McDonald's restaurants per inhabitant in the developed world", "food", "health", ""});
        facts.add(new String[]{"Belgium has approximately 178 beer breweries", "beer", "food", ""});
        facts.add(new String[]{"Gotye was born in Bruges, Belgium", "music", "celebrities", ""});
        facts.add(new String[]{"The Belgian Coast Tram is the longest tram line in the world", "technology", "world championship", ""});
        facts.add(new String[]{"Stefan Everts is the only motocross racer with 10 World Championship titles.", "celebrities", "sports", "world champions"});
        facts.add(new String[]{"Tintin was conceived by Belgian artist Hergé", "art", "celebrities", "inventions"});
        facts.add(new String[]{"Brussels Airport is the world's biggest selling point of chocolate", "food", "world champions", ""});
        facts.add(new String[]{"Tomorrowland is the biggest electronic dance music festival in the world", "festivals", "music", "world champion"});
        facts.add(new String[]{"French Fries are actually from Belgium", "food", "inventions", ""});
        facts.add(new String[]{"Herman Van Rompy is the first full-time president of the European Council", "politics", "", ""});
        facts.add(new String[]{"Belgians are the fourth most money saving people in the world", "economy", "social", ""});
        facts.add(new String[]{"The Belgian highway system is the only man-made structure visible from the moon at night", "technology", "world champions", ""});
        facts.add(new String[]{"Andreas Vesalius, the founder of modern human anatomy, is from Belgium", "celebrities", "education", "history"});
        facts.add(new String[]{"Napoleon was defeated in Waterloo, Belgium", "celebrities", "history", "politicians"});
        facts.add(new String[]
        {
            "The first natural color picture in National Geographic was of a flower garden in Gent, Belgium in 1914", "art", "history", "science"
        });
        facts.add(new String[]{"Rock Werchter is the Best Festival in the World", "festivals", "music", "world champions"});

        //Make the table a bit bigger
        facts.addAll(facts);
        facts.addAll(facts);
        facts.addAll(facts);


        return facts;
    }



    @Test
    public void SampleTest2() throws IOException {

        //Set margins
        float margin = 10;

        List<String[]> facts = getFacts();
        facts.addAll(getFacts());//ensure we have multiple pages

        //A list of bookmarks of all the tables
        List<PDOutlineItem> bookmarks = new ArrayList<PDOutlineItem>();

        //Initialize Document
        PDDocument doc = new PDDocument();
        PDPage page = addNewPage(doc);

        //Initialize table
        float tableWidth = page.getMediaBox().getWidth()-(2*margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        boolean drawContent = true;
        boolean drawLines = true;
        float yStart = yStartNewPage;
        float bottomMargin = 70;
        BaseTable table = new BaseTable(yStart,yStartNewPage,bottomMargin,tableWidth, margin, doc, page, drawLines, drawContent);


        //Create Header row
        Row<PDPage> headerRow = table.createRow(15f);
        Cell<PDPage> cell = headerRow.createCell(100,"Awesome Facts About Belgium");
        cell.setFont(PDType1Font.HELVETICA_BOLD);
        cell.setFillColor(Color.BLACK);cell.setTextColor(Color.WHITE);

        table.setHeader(headerRow);

        //Create 2 column row
        Row<PDPage> row = table.createRow(15f);
        cell = row.createCell(75,"Source:");
        cell.setFont(PDType1Font.HELVETICA);

        cell = row.createCell(25,"http://www.factsofbelgium.com/");
        cell.setFont(PDType1Font.HELVETICA_OBLIQUE);

        //Create Fact header row
        Row<PDPage> factHeaderrow = table.createRow(15f);
        cell = factHeaderrow.createCell((100/3) * 2 ,"Fact");
        cell.setFont(PDType1Font.HELVETICA);
        cell.setFontSize(6);
        cell.setFillColor(Color.LIGHT_GRAY);

        cell = factHeaderrow.createCell((100/3),"Tags");
        cell.setFillColor(Color.LIGHT_GRAY);
        cell.setFont(PDType1Font.HELVETICA_OBLIQUE);cell.setFontSize(6);

        //Add multiple rows with random facts about Belgium
        int bookmarkid = 0;
        for(String[] fact : facts) {

            row = table.createRow(10f);
            cell = row.createCell((100/3)*2 ,fact[0]+ " " + fact[0]+ " " + fact[0]);
            cell.setFont(PDType1Font.HELVETICA);cell.setFontSize(6);

            //Create a bookmark for each record
            PDOutlineItem outlineItem = new PDOutlineItem();
            outlineItem.setTitle((++bookmarkid ) + ") " + fact[0]);
            row.setBookmark( outlineItem);

            for(int i = 1; i< fact.length; i++) {
                cell = row.createCell((100/9) ,fact[i]);
                cell.setFont(PDType1Font.HELVETICA_OBLIQUE);cell.setFontSize(6);

                //Set colors
                if(fact[i].contains("beer"))cell.setFillColor(Color.yellow);
                if(fact[i].contains("champion"))cell.setTextColor(Color.GREEN);

            }
        }
        table.draw();

        //Get all bookmarks of previous table
        bookmarks.addAll(table.getBookmarks());

        //Create document outline
        PDDocumentOutline outline =  new PDDocumentOutline();

        for(PDOutlineItem bm : bookmarks) {
            outline.addLast(bm);
        }

        doc.getDocumentCatalog().setDocumentOutline(outline);

        //Save the document
        File file = new File("target/BoxableSample2.pdf");
        System.out.println("Sample file saved at : " + file.getAbsolutePath());
        Files.createParentDirs(file);
        doc.save(file);
        doc.close();

    }

    private static PDPage addNewPage(PDDocument doc) {
        PDPage page = new PDPage();
        doc.addPage(page);
        return page;
    }
}
