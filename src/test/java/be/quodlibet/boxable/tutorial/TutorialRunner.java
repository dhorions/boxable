package be.quodlibet.boxable.tutorial;

import org.junit.Test;

import java.io.File;

/**
 * Tutorial Runner
 * 
 * This class runs all Boxable tutorials and generates all PDF outputs.
 * Each tutorial demonstrates different features of the Boxable library.
 * 
 * To run all tutorials, execute this class's main method or run the test.
 * All generated PDFs will be saved in the target/tutorials/ directory.
 */
public class TutorialRunner {

    @Test
    public void runAllTutorials() throws Exception {
        System.out.println("========================================");
        System.out.println("  Boxable Tutorial Runner");
        System.out.println("========================================");
        System.out.println();
        
        // Create output directory
        File tutorialsDir = new File("target/tutorials");
        if (!tutorialsDir.exists()) {
            tutorialsDir.mkdirs();
            System.out.println("Created output directory: " + tutorialsDir.getAbsolutePath());
        }
        System.out.println();
        
        // Track results
        int totalTutorials = 12;
        int successful = 0;
        int failed = 0;
        
        System.out.println("Running tutorials...");
        System.out.println();
        
        // Tutorial 01: Basic Table
        try {
            System.out.print("[1/12] Running Tutorial01_BasicTable... ");
            new Tutorial01_BasicTable().createBasicTable();
            successful++;
            System.out.println("✓ SUCCESS");
        } catch (Exception e) {
            failed++;
            System.out.println("✗ FAILED: " + e.getMessage());
        }
        
        // Tutorial 02: HTML Formatting
        try {
            System.out.print("[2/12] Running Tutorial02_HtmlFormatting... ");
            new Tutorial02_HtmlFormatting().demonstrateHtmlFormatting();
            successful++;
            System.out.println("✓ SUCCESS");
        } catch (Exception e) {
            failed++;
            System.out.println("✗ FAILED: " + e.getMessage());
        }
        
        // Tutorial 03: Colors and Transparency
        try {
            System.out.print("[3/12] Running Tutorial03_ColorsAndTransparency... ");
            new Tutorial03_ColorsAndTransparency().demonstrateColorsAndTransparency();
            successful++;
            System.out.println("✓ SUCCESS");
        } catch (Exception e) {
            failed++;
            System.out.println("✗ FAILED: " + e.getMessage());
        }
        
        // Tutorial 04: Alignment
        try {
            System.out.print("[4/12] Running Tutorial04_Alignment... ");
            new Tutorial04_Alignment().demonstrateAlignment();
            successful++;
            System.out.println("✓ SUCCESS");
        } catch (Exception e) {
            failed++;
            System.out.println("✗ FAILED: " + e.getMessage());
        }
        
        // Tutorial 05: Images
        try {
            System.out.print("[5/12] Running Tutorial05_Images... ");
            new Tutorial05_Images().demonstrateImages();
            successful++;
            System.out.println("✓ SUCCESS");
        } catch (Exception e) {
            failed++;
            System.out.println("✗ FAILED: " + e.getMessage());
        }
        
        // Tutorial 06: Borders and Styling
        try {
            System.out.print("[6/12] Running Tutorial06_BordersAndStyling... ");
            new Tutorial06_BordersAndStyling().demonstrateBordersAndStyling();
            successful++;
            System.out.println("✓ SUCCESS");
        } catch (Exception e) {
            failed++;
            System.out.println("✗ FAILED: " + e.getMessage());
        }
        
        // Tutorial 07: Header Rows
        try {
            System.out.print("[7/12] Running Tutorial07_HeaderRows... ");
            new Tutorial07_HeaderRows().demonstrateHeaderRows();
            successful++;
            System.out.println("✓ SUCCESS");
        } catch (Exception e) {
            failed++;
            System.out.println("✗ FAILED: " + e.getMessage());
        }
        
        // Tutorial 08: Data Import
        try {
            System.out.print("[8/12] Running Tutorial08_DataImport... ");
            new Tutorial08_DataImport().demonstrateDataImport();
            successful++;
            System.out.println("✓ SUCCESS");
        } catch (Exception e) {
            failed++;
            System.out.println("✗ FAILED: " + e.getMessage());
        }
        
        // Tutorial 09: Multi-Page Tables
        try {
            System.out.print("[9/12] Running Tutorial09_MultiPageTables... ");
            new Tutorial09_MultiPageTables().demonstrateMultiPageTables();
            successful++;
            System.out.println("✓ SUCCESS");
        } catch (Exception e) {
            failed++;
            System.out.println("✗ FAILED: " + e.getMessage());
        }
        
        // Tutorial 10: Nested Tables
        try {
            System.out.print("[10/12] Running Tutorial10_NestedTables... ");
            new Tutorial10_NestedTables().demonstrateNestedTables();
            successful++;
            System.out.println("✓ SUCCESS");
        } catch (Exception e) {
            failed++;
            System.out.println("✗ FAILED: " + e.getMessage());
        }
        
        // Tutorial 11: Fixed Height Rows
        try {
            System.out.print("[11/12] Running Tutorial11_FixedHeightRows... ");
            new Tutorial11_FixedHeightRows().demonstrateFixedHeightRows();
            successful++;
            System.out.println("✓ SUCCESS");
        } catch (Exception e) {
            failed++;
            System.out.println("✗ FAILED: " + e.getMessage());
        }
        
        // Tutorial 12: Advanced Features
        try {
            System.out.print("[12/12] Running Tutorial12_AdvancedFeatures... ");
            new Tutorial12_AdvancedFeatures().demonstrateAdvancedFeatures();
            successful++;
            System.out.println("✓ SUCCESS");
        } catch (Exception e) {
            failed++;
            System.out.println("✗ FAILED: " + e.getMessage());
        }
        
        // Print summary
        System.out.println();
        System.out.println("========================================");
        System.out.println("  Summary");
        System.out.println("========================================");
        System.out.println("Total tutorials:     " + totalTutorials);
        System.out.println("Successful:          " + successful);
        System.out.println("Failed:              " + failed);
        System.out.println();
        System.out.println("Output directory:    " + tutorialsDir.getAbsolutePath());
        System.out.println();
        
        // List generated files
        System.out.println("Generated PDF files:");
        File[] pdfFiles = tutorialsDir.listFiles((dir, name) -> name.endsWith(".pdf"));
        if (pdfFiles != null && pdfFiles.length > 0) {
            for (File pdfFile : pdfFiles) {
                System.out.println("  - " + pdfFile.getName() + 
                                 " (" + String.format("%.2f", pdfFile.length() / 1024.0) + " KB)");
            }
        } else {
            System.out.println("  (No PDF files found)");
        }
        
        System.out.println();
        System.out.println("========================================");
        System.out.println("  Tutorial runner completed!");
        System.out.println("========================================");
    }
    
    /**
     * Main method to run all tutorials from command line
     */
    public static void main(String[] args) {
        try {
            TutorialRunner runner = new TutorialRunner();
            runner.runAllTutorials();
        } catch (Exception e) {
            System.err.println("Error running tutorials: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
