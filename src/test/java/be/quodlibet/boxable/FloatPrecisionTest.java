package be.quodlibet.boxable;

import be.quodlibet.boxable.datatable.DataTable;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FloatPrecisionTest {

    @Test
    public void testReproduction() throws IOException {
        PDDocument mainDocument = new PDDocument();
        PDPage myPage = new PDPage(PDRectangle.A3);
        mainDocument.addPage(myPage);
        // contentStream is created but unused in original code except for close(), 
        // effectively doing nothing here but let's keep it close to original.
        PDPageContentStream contentStream = new PDPageContentStream(mainDocument, myPage);

        List<List> data = new ArrayList<>();
        data.add(List.of("Exploring new realms of  possibility adventurers embark on a journey filled with excitement and uncertainty  eager to discover what lies beyond the horizon"));
        
        float tableWidth = 800f;
        float yStartNewPage = myPage.getMediaBox().getHeight() - (2 * 10);
        float yStart = yStartNewPage;
        final float TABLE_MARGIN = 10;
        
        BaseTable dataTable2 = new BaseTable(yStart, yStartNewPage, 0, tableWidth, TABLE_MARGIN, mainDocument, myPage,
                true, true);
        dataTable2.setLineSpacing(1.3f);
        DataTable t2 = new DataTable(dataTable2, myPage);
        Cell headerCell2 = t2.getHeaderCellTemplate();
        headerCell2.setAlign(HorizontalAlignment.LEFT);
        headerCell2.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA));
        headerCell2.setFontSize(11);

        Cell dataOddCell = t2.getDataCellTemplateOdd();
        dataOddCell.setAlign(HorizontalAlignment.LEFT);
        dataOddCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        dataOddCell.setFontSize(13);

        Cell dataEvenCell = t2.getDataCellTemplateEven();
        dataEvenCell.setAlign(HorizontalAlignment.LEFT);
        dataEvenCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        dataEvenCell.setFontSize(13);
        
        t2.addListToTable(data, DataTable.HASHEADER);
        dataTable2.draw();

        contentStream.close();

        File file = new File("target/FloatPrecisionTest.pdf");
        mainDocument.save(file);
        mainDocument.close();
    }
}
