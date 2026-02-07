# Frequently Asked Questions (FAQ)

## General Questions

### What is Boxable?

Boxable is a Java library that simplifies the creation of tables in PDF documents. It's built on top of Apache PDFBox and provides a high-level API for creating professional tables with rich formatting.

### What version of Java is required?

Boxable requires Java 8 or higher. The library is compiled with Java 17 but is compatible with Java 8+.

### Is Boxable production-ready?

Yes! Boxable is actively maintained and used in production applications. Version 1.8.2-RC1 is the current release candidate.

## Installation and Setup

### How do I add Boxable to my project?

**Maven:**
```xml
<dependency>
    <groupId>com.github.dhorions</groupId>
    <artifactId>boxable</artifactId>
    <version>1.8.2-RC1</version>
</dependency>
```

**Gradle:**
```gradle
implementation 'com.github.dhorions:boxable:1.8.2-RC1'
```

### What dependencies does Boxable have?

Boxable depends on:
- Apache PDFBox 3.0.6
- Apache Commons CSV (for CSV import)
- JSoup (for HTML parsing)

These are automatically included when you add Boxable to your project.

## Table Creation

### How do I create a simple table?

See the [Getting Started](Getting-Started) guide or [Tutorial 01](Tutorial-01-Basic-Table) for a complete example.

### Why isn't my table showing up in the PDF?

Make sure you call `table.draw()` before saving the document:

```java
table.draw();  // Don't forget this!
document.save("output.pdf");
```

### How do I control table width?

The table width is specified when creating the BaseTable:

```java
float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, 
                                tableWidth, margin, document, page, 
                                true, true);
```

### Can I create tables without borders?

Yes! Set `drawLines` to `false` when creating the table:

```java
BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, 
                                tableWidth, margin, document, page, 
                                false, true);  // false = no borders
```

You can also set `setOuterBorderStyle()` for custom border control.

## Content and Formatting

### How do I add colors to cells?

Use `setFillColor()` for background and `setTextColor()` for text:

```java
Cell<PDPage> cell = row.createCell(50, "Content");
cell.setFillColor(Color.BLUE);
cell.setTextColor(Color.WHITE);
```

### Can I use HTML formatting in cells?

Yes! Boxable supports many HTML tags including `<b>`, `<i>`, `<u>`, `<s>`, `<br/>`, `<sup>`, `<sub>`, lists, and more. See [HTML Tags Reference](HTML-Tags-Reference) for details.

### How do I add images to cells?

Use `createImageCell()`:

```java
BufferedImage bufferedImage = ImageIO.read(new File("image.png"));
Image image = new Image(bufferedImage);
ImageCell<PDPage> cell = row.createImageCell(50, image);
```

See [Tutorial 05: Images](Tutorial-05-Images) for examples.

### How do I rotate text?

Use `setTextRotated(true)`:

```java
Cell<PDPage> cell = row.createCell(10, "VERTICAL");
cell.setTextRotated(true);
```

### Can I change font size?

Yes:

```java
cell.setFontSize(14f);
```

## Multi-Page Tables

### Do tables automatically span multiple pages?

Yes! When a table reaches the bottom margin, Boxable automatically creates a new page and continues the table.

### How do I make headers repeat on each page?

Use `addHeaderRow()`:

```java
Row<PDPage> headerRow = table.createRow(20f);
// ... create header cells ...
table.addHeaderRow(headerRow);
```

Headers added this way automatically repeat on each new page.

### Can I have multiple header rows?

Yes! Call `addHeaderRow()` multiple times:

```java
table.addHeaderRow(headerRow1);
table.addHeaderRow(headerRow2);
table.addHeaderRow(headerRow3);
```

See [Tutorial 07: Header Rows](Tutorial-07-Header-Rows).

## Data Import

### How do I import data from CSV?

Use the `DataTable` class:

```java
String csvData = "Name;Age\\nJohn;30\\nJane;25";
BaseTable baseTable = new BaseTable(...);
DataTable dataTable = new DataTable(baseTable, page);
dataTable.addCsvToTable(csvData, DataTable.HASHEADER, ';');
baseTable.draw();
```

### Can I import from Java Lists?

Yes:

```java
List<List> data = new ArrayList<>();
data.add(new ArrayList<>(Arrays.asList("Name", "Age")));
data.add(new ArrayList<>(Arrays.asList("John", "30")));

DataTable dataTable = new DataTable(baseTable, page);
dataTable.addListToTable(data, DataTable.HASHEADER);
```

See [Tutorial 08: Data Import](Tutorial-08-Data-Import).

### How do I control column widths when importing?

Pass a list of relative widths:

```java
DataTable dataTable = new DataTable(baseTable, page, 
                                    Arrays.asList(3f, 1f, 1f, 1f));
// First column is 3x wider than others
```

## Performance

### How do I handle very large tables?

Boxable handles large tables efficiently. For best performance:
- Use `drawContent = true` and `drawLines = true` only when needed
- Consider breaking very large tables into multiple tables
- See [Tutorial 09: Multi-Page Tables](Tutorial-09-MultiPage-Tables)

### Does Boxable use a lot of memory?

Boxable is designed to be memory-efficient. It processes rows as they're added rather than storing everything in memory.

## Advanced Features

### Can I create nested tables?

Yes! Use HTML `<table>` tags within cell content:

```java
String nestedTable = "<table>" +
    "<tr><td>Name</td><td>Value</td></tr>" +
    "<tr><td>Item 1</td><td>100</td></tr>" +
    "</table>";
Cell<PDPage> cell = row.createCell(50, nestedTable);
```

See [Tutorial 10: Nested Tables](Tutorial-10-Nested-Tables).

### What are fixed-height rows?

Fixed-height rows automatically shrink text to fit a specified height:

```java
Row<PDPage> row = table.createRow(12f);
row.setFixedHeight(true);  // Text will shrink to fit 12pt height
```

See [Tutorial 11: Fixed Height Rows](Tutorial-11-Fixed-Height-Rows).

### Can cells span multiple columns?

Yes, in nested tables using `colspan`:

```html
"<table>
  <tr><td colspan='3'>Spans 3 columns</td></tr>
  <tr><td>Col1</td><td>Col2</td><td>Col3</td></tr>
</table>"
```

## Troubleshooting

### My text is cut off

Possible solutions:
- Increase row height
- Use fixed-height rows with auto-shrink
- Add line breaks with `<br/>` tags
- Reduce font size

### Colors aren't showing

Make sure you set both `drawLines` and `drawContent` to `true`:

```java
BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, 
                                tableWidth, margin, document, page, 
                                true, true);  // both true
```

### Characters are showing as boxes

The default font (Helvetica) doesn't support all Unicode characters. Use:
- Standard ASCII characters
- HTML entity references
- Or load a custom font that supports your characters

### Table positioning is wrong

Make sure your `yStart` calculation accounts for margins:

```java
float yStart = page.getMediaBox().getHeight() - margin;
```

## Getting Help

### Where can I find more examples?

Run the built-in tutorials:

```bash
mvn test -Dtest=TutorialRunner
```

This generates 12 PDF examples covering all features.

### Where do I report bugs?

Report issues on GitHub: https://github.com/dhorions/boxable/issues

### How can I contribute?

Contributions are welcome! See issue [#41](https://github.com/dhorions/boxable/issues/41) for documentation needs.

### Is there commercial support?

Boxable is open source. For commercial support inquiries, contact the maintainers through GitHub.

## See Also

- [Getting Started](Getting-Started)
- [All Tutorials](Tutorial-01-Basic-Table)
- [API Reference](API-Reference)
- [HTML Tags Reference](HTML-Tags-Reference)
