package be.quodlibet.boxable.tutorial;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.datatable.DataTable;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.Test;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tutorial 08: Data Import
 * 
 * This tutorial demonstrates:
 * - Importing data from CSV strings
 * - Importing data from Java Lists
 * - Column width customization
 * - Data formatting
 * - Header handling
 */
public class Tutorial08_DataImport {

    @Test
    public void demonstrateDataImport() throws IOException {
        // Initialize PDF Document
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        page.setMediaBox(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth())); // Landscape
        document.addPage(page);

        // Define table dimensions
        float margin = 50;
        float yStart = page.getMediaBox().getHeight() - margin;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - margin;
        float bottomMargin = 70;

        // Title
        BaseTable titleTable = new BaseTable(yStart, yStartNewPage, bottomMargin, 
                                            tableWidth, margin, document, page, 
                                            true, true);
        
        Row<PDPage> titleRow = titleTable.createRow(30f);
        Cell<PDPage> titleCell = titleRow.createCell(100, "Tutorial 08: Data Import");
        titleCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        titleCell.setFontSize(16f);
        titleCell.setFillColor(new Color(52, 152, 219));
        titleCell.setTextColor(Color.WHITE);
        titleCell.setAlign(HorizontalAlignment.CENTER);
        
        Row<PDPage> descRow = titleTable.createRow(20f);
        Cell<PDPage> descCell = descRow.createCell(100, 
            "Learn how to import data from CSV and Lists into tables with customization options.");
        descCell.setFillColor(new Color(236, 240, 241));
        descCell.setAlign(HorizontalAlignment.CENTER);
        
        yStart = titleTable.draw() - 30;
        if (titleTable.getCurrentPage() != page) {
            page = titleTable.getCurrentPage();
        }

        // Section 1: Import from List (Basic)
        addSectionHeader(document, page, "Section 1: Import from List (Basic)", 
                        yStart, yStartNewPage, bottomMargin, tableWidth, margin);
        yStart -= 48;
        
        // Create sample data list
        List<List> listData = new ArrayList<>();
        listData.add(new ArrayList<>(Arrays.asList("Product", "Category", "Price", "Stock", "Status")));
        listData.add(new ArrayList<>(Arrays.asList("Laptop", "Electronics", "$999.99", "15", "In Stock")));
        listData.add(new ArrayList<>(Arrays.asList("Mouse", "Accessories", "$19.99", "120", "In Stock")));
        listData.add(new ArrayList<>(Arrays.asList("Keyboard", "Accessories", "$49.99", "85", "In Stock")));
        listData.add(new ArrayList<>(Arrays.asList("Monitor", "Electronics", "$299.99", "42", "In Stock")));
        listData.add(new ArrayList<>(Arrays.asList("USB Cable", "Accessories", "$9.99", "200", "In Stock")));
        
        BaseTable listTable = new BaseTable(yStart, yStartNewPage, bottomMargin, 
                                           tableWidth, margin, document, page, 
                                           true, true);
        DataTable dt1 = new DataTable(listTable, page);
        dt1.addListToTable(listData, DataTable.HASHEADER);
        
        yStart = listTable.draw() - 30;
        if (listTable.getCurrentPage() != page) {
            page = listTable.getCurrentPage();
        }

        // Section 2: Import from CSV
        addSectionHeader(document, page, "Section 2: Import from CSV", 
                        yStart, yStartNewPage, bottomMargin, tableWidth, margin);
        yStart -= 48;
        
        // Create sample CSV data
        String csvData = "Name;Age;City;Country;Occupation\n" +
                        "John Smith;28;New York;USA;Engineer\n" +
                        "Jane Doe;32;London;UK;Designer\n" +
                        "Bob Johnson;45;Toronto;Canada;Manager\n" +
                        "Alice Williams;25;Sydney;Australia;Developer\n" +
                        "Charlie Brown;38;Berlin;Germany;Analyst";
        
        BaseTable csvTable = new BaseTable(yStart, yStartNewPage, bottomMargin, 
                                          tableWidth, margin, document, page, 
                                          true, true);
        DataTable dt2 = new DataTable(csvTable, page);
        dt2.addCsvToTable(csvData, DataTable.HASHEADER, ';');
        
        yStart = csvTable.draw() - 30;
        if (csvTable.getCurrentPage() != page) {
            page = csvTable.getCurrentPage();
        }

        // Section 3: Custom Column Widths
        addSectionHeader(document, page, "Section 3: Custom Column Widths", 
                        yStart, yStartNewPage, bottomMargin, tableWidth, margin);
        yStart -= 48;
        
        // CSV with custom column widths
        String csvData2 = "Description;Code;Value;Notes\n" +
                         "This is a very long description that needs more space;ABC;100;Short\n" +
                         "Another long description with details;DEF;200;Note\n" +
                         "More descriptive text here;GHI;300;Info";
        
        BaseTable customWidthTable = new BaseTable(yStart, yStartNewPage, bottomMargin, 
                                                   tableWidth, margin, document, page, 
                                                   true, true);
        // Custom column width ratios: Description (3x), Code (1x), Value (1x), Notes (1x)
        DataTable dt3 = new DataTable(customWidthTable, page, Arrays.asList(3f, 1f, 1f, 1f));
        dt3.addCsvToTable(csvData2, DataTable.HASHEADER, ';');
        
        yStart = customWidthTable.draw() - 30;
        if (customWidthTable.getCurrentPage() != page) {
            page = customWidthTable.getCurrentPage();
        }

        // Section 4: Styled Data Import
        addSectionHeader(document, page, "Section 4: Styled Data Import", 
                        yStart, yStartNewPage, bottomMargin, tableWidth, margin);
        yStart -= 48;
        
        // Create styled table with custom headers
        BaseTable styledTable = new BaseTable(yStart, yStartNewPage, bottomMargin, 
                                             tableWidth, margin, document, page, 
                                             true, true);
        
        // Add custom header rows before importing data
        Row<PDPage> customHeader1 = styledTable.createRow(20f);
        Cell<PDPage> header1Cell = customHeader1.createCell(100, "Monthly Sales Report - Q4 2024");
        header1Cell.setFillColor(new Color(41, 128, 185));
        header1Cell.setTextColor(Color.WHITE);
        header1Cell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        header1Cell.setAlign(HorizontalAlignment.CENTER);
        styledTable.addHeaderRow(customHeader1);
        
        Row<PDPage> customHeader2 = styledTable.createRow(15f);
        Cell<PDPage> header2Cell = customHeader2.createCell(100, "Sales figures in USD");
        header2Cell.setFillColor(new Color(52, 152, 219));
        header2Cell.setTextColor(Color.WHITE);
        header2Cell.setAlign(HorizontalAlignment.CENTER);
        styledTable.addHeaderRow(customHeader2);
        
        // Now import data
        String salesCsv = "Month;Revenue;Expenses;Profit;Growth\n" +
                         "October;$125,000;$45,000;$80,000;+12%\n" +
                         "November;$145,000;$48,000;$97,000;+16%\n" +
                         "December;$180,000;$52,000;$128,000;+24%";
        
        DataTable dt4 = new DataTable(styledTable, page);
        // Customize header cell styling
        dt4.getHeaderCellTemplate().setFillColor(new Color(52, 73, 94));
        dt4.getHeaderCellTemplate().setTextColor(Color.WHITE);
        
        // Customize data cell styling for alternating rows
        for (Cell<PDPage> cell : dt4.getDataCellTemplateEvenList()) {
            cell.setFillColor(Color.WHITE);
        }
        for (Cell<PDPage> cell : dt4.getDataCellTemplateOddList()) {
            cell.setFillColor(new Color(236, 240, 241));
        }
        
        dt4.addCsvToTable(salesCsv, DataTable.HASHEADER, ';');
        
        yStart = styledTable.draw() - 30;
        if (styledTable.getCurrentPage() != page) {
            page = styledTable.getCurrentPage();
        }

        // Section 5: Large Dataset Import
        addSectionHeader(document, page, "Section 5: Large Dataset Import", 
                        yStart, yStartNewPage, bottomMargin, tableWidth, margin);
        yStart -= 48;
        
        // Generate large dataset
        List<List> largeData = new ArrayList<>();
        largeData.add(new ArrayList<>(Arrays.asList("ID", "Name", "Department", "Email", "Phone")));
        
        for (int i = 1; i <= 30; i++) {
            largeData.add(new ArrayList<>(Arrays.asList(
                String.valueOf(i),
                "Employee " + i,
                "Dept " + ((i % 5) + 1),
                "employee" + i + "@company.com",
                "555-" + String.format("%04d", i)
            )));
        }
        
        BaseTable largeTable = new BaseTable(yStart, yStartNewPage, bottomMargin, 
                                            tableWidth, margin, document, page, 
                                            true, true);
        DataTable dt5 = new DataTable(largeTable, page);
        
        // Style the large table
        dt5.getHeaderCellTemplate().setFillColor(new Color(155, 89, 182));
        dt5.getHeaderCellTemplate().setTextColor(Color.WHITE);
        for (Cell<PDPage> cell : dt5.getDataCellTemplateEvenList()) {
            cell.setFillColor(Color.WHITE);
        }
        for (Cell<PDPage> cell : dt5.getDataCellTemplateOddList()) {
            cell.setFillColor(new Color(250, 242, 255));
        }
        
        dt5.addListToTable(largeData, DataTable.HASHEADER);
        
        largeTable.draw();

        // Save the document
        File file = new File("target/tutorials/Tutorial08_DataImport.pdf");
        System.out.println("Tutorial 08 PDF saved at: " + file.getAbsolutePath());
        file.getParentFile().mkdirs();
        document.save(file);
        document.close();
    }

    /**
     * Helper method to add a section header
     */
    private void addSectionHeader(PDDocument document, PDPage page, String title, 
                                  float yStart, float yStartNewPage, float bottomMargin, 
                                  float tableWidth, float margin) throws IOException {
        BaseTable headerTable = new BaseTable(yStart, yStartNewPage, bottomMargin, 
                                             tableWidth, margin, document, page, 
                                             true, true);
        Row<PDPage> sectionRow = headerTable.createRow(18f);
        Cell<PDPage> sectionCell = sectionRow.createCell(100, title);
        sectionCell.setFillColor(new Color(44, 62, 80));
        sectionCell.setTextColor(Color.WHITE);
        sectionCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
        sectionCell.setAlign(HorizontalAlignment.CENTER);
        headerTable.draw();
    }
}
