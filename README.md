[Boxable](http://dhorions.github.io/boxable/) - A java library to build tables in PDF documents.
=======
Boxable is a library that can be used to easily create tables in pdf documents.  It uses the [PDFBox](https://pdfbox.apache.org/) PDF library under the hood.

# Changelog
This fork was made as part of a university project. These changes have been implemented:  
* Removed abstract modifier from Table class to allow it to be instantiated directly. This is less confusing than instantiating through BaseTable, and allows the user to employ the \<T extends PDPage> generic functionality without writing their own Table sub-class.
* Split Table\<T> into two classes (as it was huge, and had too many responsibilities):
    * TableDrawer\<T> does all of the drawing and nothing else. Its only public method is draw().
    * Table stores the styling and the Rows.  
This change makes it easier to write the same table to different documents and makes the code easier to understand.
* Added Table.Builder for more user-friendly construction of Table objects.
* TableDrawer\<T> is now the only class which interacts with PageProvider\<T>, so the \<T> parameterisation was removed from the Table, Row, and Cell classes.
* BaseTable now acts as an adapter for sending deprecated method calls to the new objects.

Cleaned up root of main package:
* Moved ImageCell class into Image sub-package.
* Moved HTMLNode and TableCell into new HTML sub-package.
* Deleted deprecated BoxableUtils class.
* Deleted dead classes (AbstractPageTemplate, AbstractTemplatedTable).


Made building from source easier:
* Removed Gradle, left Maven. Couldn't get it to import into Eclipse error-free with both build tools present.
* Added .project, .classpath, .settings files to .gitignore.

Miscellaneous fixes:
* Replaced deprecated PDPageContentStream constructors
* Fixed some outdated header checks
* Removed Guava dependency as it was barely doing anything

# Features

- Build tables in pdf documents
- Convert csv data into tables in pdf documents
- Convert Lists into tables in pdf documents

#### Boxable supports next tables features
- HTML tags in cell content (not all! `<p>,<i>,<b>,<br>,<ul>,<ol>,<li>`)
- Horizontal & Vertical Alignment of the text
- Images inside cells and outside table (image scale is also supported)
- basic set of rendering attributes for lines (borders)
- rotated text (by 90 degrees)
- writing text outside tables

#### What is new in version 1.5?
- several HTML ordered lists bugfixes
- having possibility to make inner tables with TableCell (check `SampleTest10()` JUnit test)
- using FreeSans as default font to avoid multiple charachter decoding problems
- added support for drawing "borderless" table (check `SampleTest11()` for that)
- javadocs bugfix

Check PRs:
[#142](https://github.com/dhorions/boxable/pull/142)
[#144](https://github.com/dhorions/boxable/pull/144)


# Maven
```xml
<dependency>
    <groupId>com.github.dhorions</groupId>
    <artifactId>boxable</artifactId>
    <version>1.5</version>
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

Special Thanks to these awesome contributers : 
- [Frulenzo](https://github.com/Frulenzo)
- [dgautier](https://github.com/dgautier)
- [ZeerDonker](https://github.com/ZeerDonker)
- [dobluth](https://github.com/dobluth)
- [schmitzhermes](https://github.com/schmitzhermes)

=======

Copyright [2018](Quodlibet.be)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
