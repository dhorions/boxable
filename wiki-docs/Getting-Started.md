# Getting Started with Boxable

This guide will help you get started with Boxable and create your first PDF table.

## Installation

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.github.dhorions</groupId>
    <artifactId>boxable</artifactId>
    <version>1.8.2-RC1</version>
</dependency>
```

### Gradle

Add to your `build.gradle`:

```gradle
implementation 'com.github.dhorions:boxable:1.8.2-RC1'
```

## Your First Table

Let's create a simple PDF with a table:

```java
import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Row;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.IOException;

public class FirstTable {
    public static void main(String[] args) throws IOException {
        // Step 1: Create a PDF document
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        
        // Step 2: Define table parameters
        float margin = 50;
        float yStart = page.getMediaBox().getHeight() - margin;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = yStart;
        float bottomMargin = 70;
        
        // Step 3: Create the table
        BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, 
                                        tableWidth, margin, document, page, 
                                        true, true);
        
        // Step 4: Create header row
        Row<PDPage> headerRow = table.createRow(20f);
        headerRow.createCell(50, "Name");
        headerRow.createCell(50, "Age");
        
        // Step 5: Create data rows
        Row<PDPage> row1 = table.createRow(15f);
        row1.createCell(50, "John Doe");
        row1.createCell(50, "30");
        
        Row<PDPage> row2 = table.createRow(15f);
        row2.createCell(50, "Jane Smith");
        row2.createCell(50, "25");
        
        // Step 6: Draw the table
        table.draw();
        
        // Step 7: Save and close
        document.save("first-table.pdf");
        document.close();
        
        System.out.println("PDF created successfully!");
    }
}
```

## Key Concepts

### BaseTable

`BaseTable` is the main class for creating tables. It requires:
- `yStart` - Starting Y position on the page
- `yStartNewPage` - Y position for new pages (usually same as yStart)
- `bottomMargin` - Margin at the bottom of the page
- `tableWidth` - Width of the table
- `margin` - Left margin
- `document` - PDDocument instance
- `page` - Current PDPage
- `drawLines` - Whether to draw cell borders (true/false)
- `drawContent` - Whether to draw cell content (true/false)

### Row

`Row` represents a table row. Create rows with:
```java
Row<PDPage> row = table.createRow(heightInPoints);
```

### Cell

`Cell` represents a table cell. Create cells with:
```java
Cell<PDPage> cell = row.createCell(widthPercentage, "Content");
```

The width is specified as a percentage of the table width.

## Running the Tutorials

The library includes 12 comprehensive tutorials. Run them all:

```bash
mvn test -Dtest=TutorialRunner
```

Or run individually:

```bash
mvn test -Dtest=Tutorial01_BasicTable
mvn test -Dtest=Tutorial02_HtmlFormatting
# ... and so on
```

## Next Steps

- Explore the [Basic Table Tutorial](Tutorial-01-Basic-Table)
- Learn about [HTML Formatting](Tutorial-02-HTML-Formatting)
- Check out [Colors and Transparency](Tutorial-03-Colors-And-Transparency)
- View the complete [API Reference](API-Reference)

## Common Patterns

### Adding a Header Row

```java
Row<PDPage> headerRow = table.createRow(20f);
// ... create cells ...
table.addHeaderRow(headerRow);
```

Headers automatically repeat on each new page.

### Setting Cell Colors

```java
Cell<PDPage> cell = row.createCell(50, "Content");
cell.setFillColor(Color.BLUE);
cell.setTextColor(Color.WHITE);
```

### Importing CSV Data

```java
String csvData = "Name;Age\\nJohn;30\\nJane;25";
DataTable dataTable = new DataTable(baseTable, page);
dataTable.addCsvToTable(csvData, DataTable.HASHEADER, ';');
```

## Troubleshooting

### Table Not Visible

Make sure you call `table.draw()` before saving the document.

### Text Overflow

If text is too large for a cell:
- Increase row height
- Use fixed-height rows with auto-fit
- Wrap text with `<br/>` tags

### Multiple Pages

Boxable automatically creates new pages when content exceeds the page height. Header rows are automatically repeated.

## Further Reading

- [All Tutorials](Tutorial-01-Basic-Table)
- [HTML Tags Reference](HTML-Tags-Reference)
- [FAQ](FAQ)
