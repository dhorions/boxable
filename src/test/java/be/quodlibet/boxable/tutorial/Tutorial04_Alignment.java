package be.quodlibet.boxable.tutorial;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.VerticalAlignment;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.Test;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

/**
 * Tutorial 04: Cell Alignment
 * 
 * This tutorial demonstrates:
 * - Horizontal alignment (LEFT, CENTER, RIGHT)
 * - Vertical alignment (TOP, MIDDLE, BOTTOM)
 * - Combined horizontal and vertical alignments
 * - Practical alignment examples
 */
public class Tutorial04_Alignment {

    @Test
    public void demonstrateAlignment() throws IOException {
        // Initialize PDF Document
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        // Define table dimensions
        float margin = 50;
        float yStart = page.getMediaBox().getHeight() - margin;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - margin;
        float bottomMargin = 70;

        // Create the table
        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, 
                                        tableWidth, margin, document, page, 
                                        true, true);

        // Title Row
        Row<PDPage> titleRow = table.createRow(30f);
        Cell<PDPage> titleCell = titleRow.createCell(100, "Tutorial 04: Cell Alignment");
        titleCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        titleCell.setFontSize(16f);
        titleCell.setFillColor(new Color(230, 126, 34));  // Orange background
        titleCell.setTextColor(Color.WHITE);
        titleCell.setAlign(HorizontalAlignment.CENTER);

        // Description
        Row<PDPage> descRow = table.createRow(20f);
        Cell<PDPage> descCell = descRow.createCell(100, 
            "Demonstrate horizontal and vertical text alignment options in cells.");
        descCell.setFillColor(new Color(236, 240, 241));
        descCell.setAlign(HorizontalAlignment.CENTER);

        // Section 1: Horizontal Alignment
        addSectionHeader(table, "Horizontal Alignment");
        
        Row<PDPage> hAlignHeader = table.createRow(15f);
        hAlignHeader.createCell(25, "Alignment");
        hAlignHeader.createCell(75, "Example");
        styleHeaderRow(hAlignHeader);

        // LEFT alignment
        Row<PDPage> leftRow = table.createRow(15f);
        leftRow.createCell(25, "LEFT");
        Cell<PDPage> leftCell = leftRow.createCell(75, "This text is aligned to the left");
        leftCell.setAlign(HorizontalAlignment.LEFT);
        leftCell.setFillColor(new Color(174, 214, 241));

        // CENTER alignment
        Row<PDPage> centerRow = table.createRow(15f);
        centerRow.createCell(25, "CENTER");
        Cell<PDPage> centerCell = centerRow.createCell(75, "This text is centered");
        centerCell.setAlign(HorizontalAlignment.CENTER);
        centerCell.setFillColor(new Color(174, 214, 241));

        // RIGHT alignment
        Row<PDPage> rightRow = table.createRow(15f);
        rightRow.createCell(25, "RIGHT");
        Cell<PDPage> rightCell = rightRow.createCell(75, "This text is aligned to the right");
        rightCell.setAlign(HorizontalAlignment.RIGHT);
        rightCell.setFillColor(new Color(174, 214, 241));

        // Section 2: Vertical Alignment
        addSectionHeader(table, "Vertical Alignment (Tall Cells)");
        
        Row<PDPage> vAlignHeader = table.createRow(15f);
        vAlignHeader.createCell(25, "Alignment");
        vAlignHeader.createCell(75, "Example (40pt tall cells)");
        styleHeaderRow(vAlignHeader);

        // TOP alignment
        Row<PDPage> topRow = table.createRow(40f);
        topRow.createCell(25, "TOP");
        Cell<PDPage> topCell = topRow.createCell(75, "Text at top");
        topCell.setValign(VerticalAlignment.TOP);
        topCell.setFillColor(new Color(212, 230, 241));

        // MIDDLE alignment
        Row<PDPage> middleRow = table.createRow(40f);
        middleRow.createCell(25, "MIDDLE");
        Cell<PDPage> middleCell = middleRow.createCell(75, "Text in middle");
        middleCell.setValign(VerticalAlignment.MIDDLE);
        middleCell.setFillColor(new Color(212, 230, 241));

        // BOTTOM alignment
        Row<PDPage> bottomRow = table.createRow(40f);
        bottomRow.createCell(25, "BOTTOM");
        Cell<PDPage> bottomCell = bottomRow.createCell(75, "Text at bottom");
        bottomCell.setValign(VerticalAlignment.BOTTOM);
        bottomCell.setFillColor(new Color(212, 230, 241));

        // Section 3: Combined Alignment
        addSectionHeader(table, "Combined Horizontal and Vertical Alignment");
        
        Row<PDPage> comboHeader = table.createRow(15f);
        comboHeader.createCell(33.33f, "Top Left");
        comboHeader.createCell(33.33f, "Top Center");
        comboHeader.createCell(33.34f, "Top Right");
        styleHeaderRow(comboHeader);

        // Top row combinations
        Row<PDPage> topComboRow = table.createRow(35f);
        Cell<PDPage> topLeft = topComboRow.createCell(33.33f, "TOP<br/>LEFT");
        topLeft.setAlign(HorizontalAlignment.LEFT);
        topLeft.setValign(VerticalAlignment.TOP);
        topLeft.setFillColor(new Color(230, 245, 255));
        
        Cell<PDPage> topCenter = topComboRow.createCell(33.33f, "TOP<br/>CENTER");
        topCenter.setAlign(HorizontalAlignment.CENTER);
        topCenter.setValign(VerticalAlignment.TOP);
        topCenter.setFillColor(new Color(230, 245, 255));
        
        Cell<PDPage> topRight = topComboRow.createCell(33.34f, "TOP<br/>RIGHT");
        topRight.setAlign(HorizontalAlignment.RIGHT);
        topRight.setValign(VerticalAlignment.TOP);
        topRight.setFillColor(new Color(230, 245, 255));

        // Middle row combinations
        Row<PDPage> middleComboRow = table.createRow(35f);
        Cell<PDPage> middleLeft = middleComboRow.createCell(33.33f, "MIDDLE<br/>LEFT");
        middleLeft.setAlign(HorizontalAlignment.LEFT);
        middleLeft.setValign(VerticalAlignment.MIDDLE);
        middleLeft.setFillColor(new Color(255, 243, 224));
        
        Cell<PDPage> middleCenter = middleComboRow.createCell(33.33f, "MIDDLE<br/>CENTER");
        middleCenter.setAlign(HorizontalAlignment.CENTER);
        middleCenter.setValign(VerticalAlignment.MIDDLE);
        middleCenter.setFillColor(new Color(255, 243, 224));
        
        Cell<PDPage> middleRight = middleComboRow.createCell(33.34f, "MIDDLE<br/>RIGHT");
        middleRight.setAlign(HorizontalAlignment.RIGHT);
        middleRight.setValign(VerticalAlignment.MIDDLE);
        middleRight.setFillColor(new Color(255, 243, 224));

        // Bottom row combinations
        Row<PDPage> bottomComboRow = table.createRow(35f);
        Cell<PDPage> bottomLeft = bottomComboRow.createCell(33.33f, "BOTTOM<br/>LEFT");
        bottomLeft.setAlign(HorizontalAlignment.LEFT);
        bottomLeft.setValign(VerticalAlignment.BOTTOM);
        bottomLeft.setFillColor(new Color(255, 235, 238));
        
        Cell<PDPage> bottomCenter = bottomComboRow.createCell(33.33f, "BOTTOM<br/>CENTER");
        bottomCenter.setAlign(HorizontalAlignment.CENTER);
        bottomCenter.setValign(VerticalAlignment.BOTTOM);
        bottomCenter.setFillColor(new Color(255, 235, 238));
        
        Cell<PDPage> bottomRight = bottomComboRow.createCell(33.34f, "BOTTOM<br/>RIGHT");
        bottomRight.setAlign(HorizontalAlignment.RIGHT);
        bottomRight.setValign(VerticalAlignment.BOTTOM);
        bottomRight.setFillColor(new Color(255, 235, 238));

        // Section 4: Practical Example
        addSectionHeader(table, "Practical Example: Invoice Table");
        
        Row<PDPage> invoiceHeader = table.createRow(15f);
        Cell<PDPage> itemHeader = invoiceHeader.createCell(40, "Item");
        Cell<PDPage> qtyHeader = invoiceHeader.createCell(20, "Qty");
        Cell<PDPage> priceHeader = invoiceHeader.createCell(20, "Price");
        Cell<PDPage> totalHeader = invoiceHeader.createCell(20, "Total");
        styleHeaderRow(invoiceHeader);
        
        // Center align numeric headers
        qtyHeader.setAlign(HorizontalAlignment.CENTER);
        priceHeader.setAlign(HorizontalAlignment.RIGHT);
        totalHeader.setAlign(HorizontalAlignment.RIGHT);

        // Invoice data rows
        String[][] invoiceData = {
            {"Premium Widget", "5", "$10.00", "$50.00"},
            {"Standard Widget", "10", "$5.00", "$50.00"},
            {"Economy Widget", "20", "$2.50", "$50.00"}
        };

        for (String[] row : invoiceData) {
            Row<PDPage> dataRow = table.createRow(15f);
            Cell<PDPage> itemCell = dataRow.createCell(40, row[0]);
            Cell<PDPage> qtyCell = dataRow.createCell(20, row[1]);
            Cell<PDPage> priceCell = dataRow.createCell(20, row[2]);
            Cell<PDPage> totalCell = dataRow.createCell(20, row[3]);
            
            // Apply appropriate alignment
            itemCell.setAlign(HorizontalAlignment.LEFT);
            qtyCell.setAlign(HorizontalAlignment.CENTER);
            priceCell.setAlign(HorizontalAlignment.RIGHT);
            totalCell.setAlign(HorizontalAlignment.RIGHT);
        }

        // Total row
        Row<PDPage> totalRow = table.createRow(15f);
        Cell<PDPage> totalLabel = totalRow.createCell(80, "TOTAL:");
        Cell<PDPage> totalAmount = totalRow.createCell(20, "$150.00");
        totalLabel.setAlign(HorizontalAlignment.RIGHT);
        totalLabel.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        totalAmount.setAlign(HorizontalAlignment.RIGHT);
        totalAmount.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        totalAmount.setFillColor(new Color(46, 204, 113));
        totalAmount.setTextColor(Color.WHITE);

        // Draw the table
        table.draw();

        // Save the document
        File file = new File("target/tutorials/Tutorial04_Alignment.pdf");
        System.out.println("Tutorial 04 PDF saved at: " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        document.save(file);
        document.close();
    }

    /**
     * Helper method to add a section header
     */
    private void addSectionHeader(BaseTable table, String title) throws IOException {
        Row<PDPage> sectionRow = table.createRow(18f);
        Cell<PDPage> sectionCell = sectionRow.createCell(100, title);
        sectionCell.setFillColor(new Color(44, 62, 80));
        sectionCell.setTextColor(Color.WHITE);
        sectionCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        sectionCell.setAlign(HorizontalAlignment.CENTER);
    }

    /**
     * Helper method to style header rows
     */
    private void styleHeaderRow(Row<PDPage> row) {
        Color headerColor = new Color(52, 73, 94);
        for (Cell<PDPage> cell : row.getCells()) {
            cell.setFillColor(headerColor);
            cell.setTextColor(Color.WHITE);
            cell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        }
    }
}
