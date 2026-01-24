[Boxable](http://dhorions.github.io/boxable/) - A java library to build tables in PDF documents.
=======


[![Java CI with Maven](https://github.com/dhorions/boxable/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/dhorions/boxable/actions/workflows/maven.yml)
[![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=5UL3NVLA852MN&source=url)

Boxable is a library that can be used to easily create tables in pdf documents.  It uses the [PDFBox](https://pdfbox.apache.org/) PDF library under the hood.

# Features

- Build tables in pdf documents
- Convert csv data into tables in pdf documents
- Convert Lists into tables in pdf documents

#### Boxable supports next tables features
- HTML tags in cell content (not all! `<p>,<i>,<b>,<br>,<ul>,<ol>,<li>,<u>,<s>,<h1>-<h6>`)
- Horizontal & Vertical Alignment of the text
- Images inside cells and outside table (image scale is also supported)
- basic set of rendering attributes for lines (borders)
- rotated text (by 90 degrees)
- writing text outside tables
- Inner tables using html `<table>`

# Maven
```xml
<dependency>
    <groupId>com.github.dhorions</groupId>
    <artifactId>boxable</artifactId>
    <version>1.8.1</version>
</dependency>
```
For other build systems, check the [Maven Central Repository](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22boxable%22).


# Tutorial

A tutorial is being created and will be accessible at https://github.com/dhorions/boxable/wiki.
If you want to help, please let us know  [here](https://github.com/dhorions/boxable/issues/41).

# Usage examples

## Create a pdf from a csv file 

```java
String data = readData("https://s3.amazonaws.com/misc.quodlibet.be/Boxable/Eurostat_Immigration_Applications.csv");
BaseTable pdfTable = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true,true);
DataTable t = new DataTable(pdfTable, page);
t.addCsvToTable(data, DataTable.HASHEADER, ';');
pdfTable.draw();
```
Output : [CSVExamplePortrait.pdf](https://s3.amazonaws.com/misc.quodlibet.be/Boxable/CSVexamplePortrait.pdf)

## Create a pdf from a List

```java
List<List> data = new ArrayList();
data.add(new ArrayList<>(
               Arrays.asList("Column One", "Column Two", "Column Three", "Column Four", "Column Five")));
for (int i = 1; i <= 100; i++) {
  data.add(new ArrayList<>(
      Arrays.asList("Row " + i + " Col One", "Row " + i + " Col Two", "Row " + i + " Col Three", "Row " + i + " Col Four", "Row " + i + " Col Five")));
}
BaseTable dataTable = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);
DataTable t = new DataTable(dataTable, page);
t.addListToTable(data, DataTable.HASHEADER);
dataTable.draw();
```
Output : [ListExampleLandscape.pdf](https://s3.amazonaws.com/misc.quodlibet.be/Boxable/ListExampleLandscape.pdf)

## Build tables in pdf documents

```java
BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true,
				drawContent);
//Create Header row
Row<PDPage> headerRow = table.createRow(15f);
Cell<PDPage> cell = headerRow.createCell(100, "Awesome Facts About Belgium");
cell.setFont(PDType1Font.HELVETICA_BOLD);
cell.setFillColor(Color.BLACK);
table.addHeaderRow(headerRow);
List<String[]> facts = getFacts();
for (String[] fact : facts) {
			Row<PDPage> row = table.createRow(10f);
			cell = row.createCell((100 / 3.0f) * 2, fact[0] );
			for (int i = 1; i < fact.length; i++) {
			   cell = row.createCell((100 / 9f), fact[i]);
			}
}
table.draw();
```

## Fixed height rows (auto-fit text)

```java
BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);

// Fixed-height header row (text shrinks to fit)
Row<PDPage> headerRow = table.createRow(12f);
headerRow.setFixedHeight(true);
Cell<PDPage> headerCell = headerRow.createCell(100, "Fixed header with longer text");
headerCell.setFontSize(14f);
table.addHeaderRow(headerRow);

// Fixed-height data row (text shrinks to fit)
Row<PDPage> fixedRow = table.createRow(12f);
fixedRow.setFixedHeight(true);
fixedRow.createCell(30, "Fixed row");
fixedRow.createCell(70, "Some value that should be reduced to fit in 12pt height");

// Flexible row (height grows to fit)
Row<PDPage> flexibleRow = table.createRow(12f);
flexibleRow.createCell(30, "Flexible row");
flexibleRow.createCell(70, "Some value that should keep its font size and expand the row height");

table.draw();
```

## Inner Tables

Boxable supports rendering nested tables within a cell using standard HTML `<table>` syntax.

### Basic Usage
Pass an HTML string containing a `<table>` to a `TableCell`.  
*Note: You must use `createTableCell` (not `createCell`) and provide the document context.*

```java
String html = "<table>" +
              "<tr><td>Left</td><td>Right</td></tr>" +
              "</table>";
// Create a cell that renders the inner table
row.createTableCell(100, html, doc, page, yStart, 10, 10);
```

### Style Inheritance
Inner tables automatically inherit font and color settings from their parent container cell:
```java
Cell<PDPage> cell = row.createTableCell(100, htmlTable, doc, page, yStart, pageTopMargin, pageBottomMargin);
cell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD)); // Inner table text will be bold
cell.setTextColor(Color.BLUE); // Inner table text will be blue
```

### Global Configuration
You can configure borders and padding for the entire inner table via `TableCell` methods:
```java
TableCell<PDPage> tableCell = (TableCell<PDPage>) cell;
tableCell.setInnerTableDrawLines(true); // Draw borders (default true)
tableCell.setInnerTableBorders(true, true, true, true); // Outer borders
tableCell.setInnerTableInnerBorders(true, true); // Inner grid lines
tableCell.setInnerTableBorderStyle(new LineStyle(Color.GRAY, 0.5f)); // Custom border style
```

### HTML Attribute Styling
Individual cells within the inner table can be styled using standard HTML attributes.
Supported attributes:
- `style="color: #RRGGBB|name"` sets text color.
- `style="background-color: #RRGGBB|name"` sets background color.
- `style="border-color: #RRGGBB|name"` sets border color for that cell.
- `style="font-family: FontName"` sets font (e.g., 'Courier-Bold', 'Times-Italic').
- `bgcolor="#RRGGBB|name"` sets background color (legacy attribute).

**Example:**
```html
<table>
  <tr>
    <td style="color: red; background-color: yellow;">Red on Yellow</td>
    <td style="border-color: blue;">Blue Border</td>
  </tr>
  <tr>
    <td style="font-family: Courier-Bold;">Courier Bold Text</td>
    <td bgcolor="lightgray">Light Gray Bg</td>
  </tr>
</table>
```

For a comprehensive list of supported named colors and syntax (hex, rgb, rgba), see the implementation in `ColorUtils` and tests in [InnerTableHTMLStylingTest.java](src/test/java/be/quodlibet/boxable/InnerTableHTMLStylingTest.java).

Special Thanks to these awesome contributers : 
- [@joaemel](https://github.com/joaemel)
- [@johnmanko](https://github.com/johnmanko)
- [@Vobarian](https://github.com/vobarian)
- [@Giboow](https://github.com/giboow)
- [@Ogmios-Voice](https://github.com/ogmios-voice)
- [@zaqpiotr](https://github.com/zaqpiotr)
- [Frulenzo](https://github.com/Frulenzo)
- [dgautier](https://github.com/dgautier)
- [ZeerDonker](https://github.com/ZeerDonker)
- [dobluth](https://github.com/dobluth)
- [schmitzhermes](https://github.com/schmitzhermes)

=======

Copyright [2026](Quodlibet.be)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
