# Home

Welcome to the Boxable library documentation!

## What is Boxable?

Boxable is a high-level Java library built on top of Apache PDFBox that makes it easy to create tables in PDF documents. It provides a simple API for generating professional-looking tables with rich formatting options.

## Key Features

- **Easy table creation** - Simple API for building tables programmatically
- **Rich HTML formatting** - Support for HTML tags including `<b>`, `<i>`, `<u>`, `<s>`, `<h1>`-`<h6>`, `<sup>`, `<sub>`, `<ul>`, `<ol>`, `<li>`, and more
- **Data import** - Import data from CSV files or Java Lists
- **Multi-page support** - Tables automatically span multiple pages with repeating headers
- **Advanced styling** - Cell colors, borders, alignment, transparency, images, and more
- **Nested tables** - Create complex layouts with tables inside cells
- **Fixed-height rows** - Auto-fit text in constrained spaces
- **Rotated text** - 90-degree rotation for vertical labels

## Quick Start

### Maven Dependency

```xml
<dependency>
    <groupId>com.github.dhorions</groupId>
    <artifactId>boxable</artifactId>
    <version>1.8.2-RC1</version>
</dependency>
```

### Simple Example

```java
// Initialize PDF document
PDDocument document = new PDDocument();
PDPage page = new PDPage();
document.addPage(page);

// Create table
float margin = 50;
float yStart = page.getMediaBox().getHeight() - margin;
float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
float bottomMargin = 70;

BaseTable table = new BaseTable(yStart, yStart, bottomMargin, 
                                tableWidth, margin, document, page, 
                                true, true);

// Add rows
Row<PDPage> headerRow = table.createRow(20f);
headerRow.createCell(50, "Name");
headerRow.createCell(50, "Value");

Row<PDPage> dataRow = table.createRow(15f);
dataRow.createCell(50, "Item 1");
dataRow.createCell(50, "Value 1");

// Draw and save
table.draw();
document.save("output.pdf");
document.close();
```

## Documentation Structure

- **[Getting Started](Getting-Started)** - Installation and first steps
- **[Tutorials](Tutorial-01-Basic-Table)** - 12 comprehensive tutorials with examples
- **[API Reference](API-Reference)** - Detailed API documentation
- **[HTML Tags Reference](HTML-Tags-Reference)** - Supported HTML tags
- **[FAQ](FAQ)** - Frequently asked questions

## Runnable Tutorials

The library includes 12 runnable tutorials that demonstrate all features:

1. [Basic Table](Tutorial-01-Basic-Table) - Simple table creation
2. [HTML Formatting](Tutorial-02-HTML-Formatting) - Text formatting with HTML
3. [Colors and Transparency](Tutorial-03-Colors-And-Transparency) - Color management
4. [Alignment](Tutorial-04-Alignment) - Text alignment options
5. [Images](Tutorial-05-Images) - Images in cells
6. [Borders and Styling](Tutorial-06-Borders-And-Styling) - Border customization
7. [Header Rows](Tutorial-07-Header-Rows) - Repeating headers
8. [Data Import](Tutorial-08-Data-Import) - CSV and List imports
9. [Multi-Page Tables](Tutorial-09-MultiPage-Tables) - Page spanning
10. [Nested Tables](Tutorial-10-Nested-Tables) - Tables within cells
11. [Fixed Height Rows](Tutorial-11-Fixed-Height-Rows) - Auto-fit text
12. [Advanced Features](Tutorial-12-Advanced-Features) - Rotated text, colspan

Run all tutorials:
```bash
mvn test -Dtest=TutorialRunner
```

## Community and Support

- **GitHub Repository**: https://github.com/dhorions/boxable
- **Issues**: https://github.com/dhorions/boxable/issues
- **Contributing**: Contributions are welcome! See issue [#41](https://github.com/dhorions/boxable/issues/41)

## License

Licensed under the Apache License, Version 2.0. See the LICENSE file for details.
