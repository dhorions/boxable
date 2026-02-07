# API Reference

This reference covers the main classes and methods in Boxable.

## Core Classes

### BaseTable

The main class for creating tables.

#### Constructor

```java
BaseTable(float yStart, 
         float yStartNewPage, 
         float bottomMargin, 
         float width, 
         float margin, 
         PDDocument document, 
         PDPage page, 
         boolean drawLines, 
         boolean drawContent)
```

**Parameters:**
- `yStart` - Starting Y position on the current page
- `yStartNewPage` - Y position for new pages (usually same as yStart)
- `bottomMargin` - Bottom margin (when to create new page)
- `width` - Table width in points
- `margin` - Left margin in points
- `document` - PDDocument instance
- `page` - Current PDPage
- `drawLines` - Whether to draw cell borders
- `drawContent` - Whether to draw cell content

#### Key Methods

```java
// Create a new row
Row<PDPage> createRow(float height)

// Add a header row (repeats on each page)
void addHeaderRow(Row<PDPage> row)

// Draw the table
float draw()  // Returns final Y position

// Get current page (may change after draw)
PDPage getCurrentPage()

// Set outer border style
void setOuterBorderStyle(LineStyle style)
```

### Row

Represents a table row.

#### Key Methods

```java
// Create a text cell
Cell<PDPage> createCell(float widthPercent, String text)

// Create an image cell
ImageCell<PDPage> createImageCell(float widthPercent, Image image)

// Create a table cell (nested table via HTML)
Cell<PDPage> createTableCell(float widthPercent, String htmlTable, 
                              PDDocument doc, PDPage page, float yStart, 
                              float bottomMargin, float margin)

// Set row height
void setHeight(float height)

// Set fixed height (auto-shrink text)
void setFixedHeight(boolean fixed)

// Get all cells in row
List<Cell<PDPage>> getCells()
```

### Cell

Represents a table cell.

#### Styling Methods

```java
// Colors
void setFillColor(Color color)
void setTextColor(Color color)

// Text properties
void setFont(PDFont font)
void setFontSize(float size)
void setTextRotated(boolean rotated)

// Alignment
void setAlign(HorizontalAlignment align)
void setValign(VerticalAlignment align)

// Borders
void setBorderStyle(LineStyle style)
void setTopBorderStyle(LineStyle style)
void setBottomBorderStyle(LineStyle style)
void setLeftBorderStyle(LineStyle style)
void setRightBorderStyle(LineStyle style)

// Padding
void setTopPadding(float padding)
void setBottomPadding(float padding)
void setLeftPadding(float padding)
void setRightPadding(float padding)

// Content
String getText()
void setText(String text)
```

### ImageCell

Extends Cell for image content.

```java
// Get the image
Image getImage()

// Inherited from Cell: all styling methods
```

### DataTable

Helper class for importing data.

#### Constructor

```java
DataTable(BaseTable table, PDPage page)
DataTable(BaseTable table, PDPage page, List<Float> colWidths)
```

#### Key Methods

```java
// Import from CSV
void addCsvToTable(String csvData, boolean hasHeader, char delimiter)

// Import from List
void addListToTable(List<List> data, boolean hasHeader)

// Style templates
Cell getHeaderCellTemplate()
Cell getDataCellTemplateEven()
Cell getDataCellTemplateOdd()
List<Cell> getDataCellTemplateEvenList()
List<Cell> getDataCellTemplateOddList()
Cell getFirstColumnCellTemplate()
Cell getLastColumnCellTemplate()
```

### LineStyle

Represents border styling.

#### Constructor

```java
LineStyle(Color color, float width)
```

**Example:**
```java
LineStyle redBorder = new LineStyle(Color.RED, 2f);
cell.setBorderStyle(redBorder);
```

### Image

Wrapper for images in cells.

#### Constructor

```java
Image(BufferedImage image)
```

**Example:**
```java
BufferedImage buffImg = ImageIO.read(new File("image.png"));
Image image = new Image(buffImg);
ImageCell<PDPage> cell = row.createImageCell(50, image);
```

## Enums

### HorizontalAlignment

```java
HorizontalAlignment.LEFT
HorizontalAlignment.CENTER
HorizontalAlignment.RIGHT
```

### VerticalAlignment

```java
VerticalAlignment.TOP
VerticalAlignment.MIDDLE
VerticalAlignment.BOTTOM
```

## Constants

### DataTable

```java
DataTable.HASHEADER  // = true (data has header row)
DataTable.NOHEADER   // = false (no header row)
```

## Common Patterns

### Creating a Basic Table

```java
PDDocument doc = new PDDocument();
PDPage page = new PDPage();
doc.addPage(page);

float margin = 50;
float yStart = page.getMediaBox().getHeight() - margin;
float tableWidth = page.getMediaBox().getWidth() - (2 * margin);

BaseTable table = new BaseTable(yStart, yStart, 70, tableWidth, 
                                margin, doc, page, true, true);

Row<PDPage> row = table.createRow(20f);
Cell<PDPage> cell = row.createCell(100, "Hello World");

table.draw();
doc.save("output.pdf");
doc.close();
```

### Styled Header

```java
Row<PDPage> header = table.createRow(20f);
Cell<PDPage> headerCell = header.createCell(100, "Header");
headerCell.setFillColor(new Color(52, 152, 219));
headerCell.setTextColor(Color.WHITE);
headerCell.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD));
headerCell.setAlign(HorizontalAlignment.CENTER);
table.addHeaderRow(header);
```

### Alternating Row Colors

```java
for (int i = 0; i < data.length; i++) {
    Row<PDPage> row = table.createRow(15f);
    Cell<PDPage> cell = row.createCell(100, data[i]);
    
    if (i % 2 == 0) {
        cell.setFillColor(new Color(236, 240, 241));
    } else {
        cell.setFillColor(Color.WHITE);
    }
}
```

### Custom Borders

```java
// Thick red border
cell.setBorderStyle(new LineStyle(Color.RED, 3f));

// No borders
cell.setBorderStyle(null);

// Bottom border only
cell.setTopBorderStyle(null);
cell.setBottomBorderStyle(new LineStyle(Color.BLACK, 2f));
cell.setLeftBorderStyle(null);
cell.setRightBorderStyle(null);

// Outer border only on table
table.setOuterBorderStyle(new LineStyle(Color.BLACK, 2f));
```

### Transparent Colors

```java
// Alpha channel: 0 = fully transparent, 255 = fully opaque
Color transparentRed = new Color(255, 0, 0, 128);  // 50% transparent
cell.setFillColor(transparentRed);
```

### HTML Formatting

```java
String html = "<b>Bold</b> and <i>italic</i><br/>" +
              "H<sub>2</sub>O and E=mc<sup>2</sup>";
Cell<PDPage> cell = row.createCell(100, html);
```

### Multi-Column Cells

```java
Row<PDPage> row = table.createRow(20f);
Cell<PDPage> wideCell = row.createCell(70, "Wide cell");
Cell<PDPage> narrowCell1 = row.createCell(15, "Narrow 1");
Cell<PDPage> narrowCell2 = row.createCell(15, "Narrow 2");
```

## Best Practices

1. **Always call `draw()`** before saving the document
2. **Store return value of `draw()`** if creating multiple tables
3. **Check `getCurrentPage()`** after `draw()` for page transitions
4. **Use percentage widths** that sum to 100
5. **Add header rows before data rows**
6. **Set `drawContent` and `drawLines`** according to your needs
7. **Use templates in DataTable** for consistent styling
8. **Close PDDocument** after saving

## See Also

- [Getting Started Guide](Getting-Started)
- [All Tutorials](Tutorial-01-Basic-Table)
- [HTML Tags Reference](HTML-Tags-Reference)
- [FAQ](FAQ)
