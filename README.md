[Boxable](http://dhorions.github.io/boxable/) - A Java library to build tables in PDF documents.

[![Java CI with Maven](https://github.com/dhorions/boxable/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/dhorions/boxable/actions/workflows/maven.yml)
[![Donate](https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=5UL3NVLA852MN&source=url)

Boxable is a library that can be used to easily create tables in PDF documents. It uses the [PDFBox](https://pdfbox.apache.org/) PDF library under the hood.

# Features

- Build tables in PDF documents
- Convert CSV data into tables in PDF documents
- Convert Lists into tables in PDF documents

#### Boxable supports the following table features
- HTML tags in cell content (not all! `<p>,<i>,<b>,<br>,<ul>,<ol>,<li>,<u>,<s>,<h1>-<h6>,<sup>,<sub>`)
- Horizontal & Vertical Alignment of the text
- Images inside cells and outside table (image scale is also supported)
- Basic set of rendering attributes for lines (borders)
- Rotated text (by 90 degrees)
- Writing text outside tables
- Inner tables using HTML `<table>`

# Maven
```xml
<dependency>
    <groupId>com.github.dhorions</groupId>
    <artifactId>boxable</artifactId>
    <version>1.8.2-RC1</version>
</dependency>
```
For other build systems, check the [Maven Central Repository](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22boxable%22).


# Tutorial

Comprehensive tutorials are now available in the test package! Each tutorial demonstrates specific features with well-documented, runnable examples:

## Running the Tutorials

You can run all tutorials at once using the Tutorial Runner:

```bash
mvn test -Dtest=TutorialRunner
```

Or run individual tutorials:

```bash
mvn test -Dtest=Tutorial01_BasicTable
```

All generated PDFs will be saved in the `target/tutorials/` directory.

## Available Tutorials

1. **Tutorial01_BasicTable** - Simple table creation, headers, and cell styling
2. **Tutorial02_HtmlFormatting** - All supported HTML tags including `<sup>` and `<sub>`
3. **Tutorial03_ColorsAndTransparency** - Cell colors, text colors, and alpha channel
4. **Tutorial04_Alignment** - Horizontal and vertical text alignment
5. **Tutorial05_Images** - Images in cells with scaling and padding control
6. **Tutorial06_BordersAndStyling** - Border styles, colors, widths, and selective borders
7. **Tutorial07_HeaderRows** - Single and multiple header rows with page repetition
8. **Tutorial08_DataImport** - Import data from CSV and Java Lists
9. **Tutorial09_MultiPageTables** - Large tables spanning multiple pages
10. **Tutorial10_NestedTables** - Tables within cells using HTML `<table>` tags
11. **Tutorial11_FixedHeightRows** - Fixed-height rows with auto-fit text
12. **Tutorial12_AdvancedFeatures** - Rotated text, line spacing, and colspan

## Tutorial Source Code

All tutorial source code is available in `src/test/java/be/quodlibet/boxable/tutorial/`.

Each tutorial is self-contained and includes:
- Detailed JavaDoc comments explaining the demonstrated features
- Well-structured, readable code
- Generated PDF output for visual reference

## Wiki Documentation

Detailed documentation is being created and will be accessible at https://github.com/dhorions/boxable/wiki.
If you want to help, please let us know  [here](https://github.com/dhorions/boxable/issues/41).

# Usage examples

## Create a PDF from a CSV file 

```java
String data = readData("https://s3.amazonaws.com/misc.quodlibet.be/Boxable/Eurostat_Immigration_Applications.csv");
BaseTable pdfTable = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true,true);
DataTable t = new DataTable(pdfTable, page);
t.addCsvToTable(data, DataTable.HASHEADER, ';');
pdfTable.draw();
```
Output : [CSVExamplePortrait.pdf](https://s3.amazonaws.com/misc.quodlibet.be/Boxable/CSVexamplePortrait.pdf)

## Create a PDF from a List

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

## Build tables in PDF documents

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

Special Thanks to these awesome contributors : 
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
- [lambart](https://github.com/lambart)

## License 

Copyright 2026 [Quodlibet.be](https://www.quodlibet.be)

**Source Code** : 
Licensed under the Apache License, Version 2.0 (the "License");you may not use this file except in compliance with the License.You may obtain a copy of the License at  http://www.apache.org/licenses/LICENSE-2.0

**Bundled Fonts** : 
Liberation Sans fonts are licensed under the SIL Open Font License 1.1. See `src/main/resources/fonts/LICENSE` for full details.

By using this library, you agree to comply with both licenses.



Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
