# HTML Tags Reference

Boxable supports a subset of HTML tags for rich text formatting in table cells. This reference covers all supported tags with examples.

## Text Formatting Tags

### Bold: `<b>`

Makes text bold.

**Example:**
```html
"This is <b>bold text</b> in a cell"
```

**Output:** This is **bold text** in a cell

### Italic: `<i>`

Makes text italic.

**Example:**
```html
"This is <i>italic text</i> in a cell"
```

**Output:** This is *italic text* in a cell

### Underline: `<u>`

Underlines text.

**Example:**
```html
"This is <u>underlined text</u> in a cell"
```

**Output:** This is <u>underlined text</u> in a cell

### Strikethrough: `<s>`

Strikes through text.

**Example:**
```html
"This is <s>strikethrough text</s> in a cell"
```

**Output:** This is ~~strikethrough text~~ in a cell

## Heading Tags

### Headings: `<h1>` through `<h6>`

Creates headings of different sizes.

**Example:**
```html
"<h1>Largest Heading</h1>"
"<h2>Second Level</h2>"
"<h3>Third Level</h3>"
"<h6>Smallest Heading</h6>"
```

**Note:** Headings have different font sizes with h1 being the largest.

## Superscript and Subscript (New in v1.8.2)

### Superscript: `<sup>`

Raises text above the baseline (useful for exponents, footnotes).

**Example:**
```html
"E=mc<sup>2</sup>"
"x<sup>2</sup> + y<sup>2</sup> = z<sup>2</sup>"
```

**Output:** E=mc² and x² + y² = z²

**Common Uses:**
- Mathematical exponents
- Ordinal indicators (1ˢᵗ, 2ⁿᵈ)
- Footnote references

### Subscript: `<sub>`

Lowers text below the baseline (useful for chemical formulas).

**Example:**
```html
"H<sub>2</sub>O"
"CO<sub>2</sub>"
```

**Output:** H₂O and CO₂

**Common Uses:**
- Chemical formulas
- Mathematical subscripts
- Array indices

### Combined Super/Subscript

You can combine both:

**Example:**
```html
"A<sup>n</sup><sub>i</sub>"
```

## Line Breaks and Paragraphs

### Line Break: `<br/>` or `<br>`

Inserts a line break.

**Example:**
```html
"First line<br/>Second line<br/>Third line"
```

**Output:**
```
First line
Second line
Third line
```

### Paragraph: `<p>`

Creates a paragraph with spacing.

**Example:**
```html
"<p>First paragraph</p><p>Second paragraph</p>"
```

## Lists

### Unordered List: `<ul>` and `<li>`

Creates a bulleted list.

**Example:**
```html
"<ul>
  <li>First item</li>
  <li>Second item</li>
  <li>Third item</li>
</ul>"
```

**Output:**
- First item
- Second item
- Third item

### Ordered List: `<ol>` and `<li>`

Creates a numbered list.

**Example:**
```html
"<ol>
  <li>First step</li>
  <li>Second step</li>
  <li>Third step</li>
</ol>"
```

**Output:**
1. First step
2. Second step
3. Third step

## Tables (Nested)

### Nested Table: `<table>`, `<tr>`, `<td>`

Creates a table within a cell.

**Example:**
```html
"<table>
  <tr><td>Name</td><td>Value</td></tr>
  <tr><td>Item 1</td><td>100</td></tr>
  <tr><td>Item 2</td><td>200</td></tr>
</table>"
```

### Colspan in Nested Tables

**Example:**
```html
"<table>
  <tr><td colspan='2'>Spanning Header</td></tr>
  <tr><td>Col 1</td><td>Col 2</td></tr>
</table>"
```

## Nesting Tags

Tags can be nested for combined effects:

**Example:**
```html
"<b><i>Bold and Italic</i></b>"
"<u><b>Bold Underlined</b></u>"
"<b>Bold with <i>italic</i> inside</b>"
"Underlined with <u><sup>superscript</sup></u>"
```

**Complex Example:**
```html
"<b>Product:</b> Widget X<br/>
<i>Formula:</i> H<sub>2</sub>O + CO<sub>2</sub><br/>
<u>Price:</u> $99.99<sup>*</sup>"
```

## Best Practices

### DO:
- ✓ Close all tags properly
- ✓ Use proper nesting (inner tags close before outer tags)
- ✓ Use semantic tags for meaning (`<b>` for emphasis, `<sup>` for exponents)
- ✓ Test complex HTML in the tutorial examples first

### DON'T:
- ✗ Use unsupported HTML tags (they will be ignored)
- ✗ Use CSS styles (not supported)
- ✗ Nest lists inside lists (not fully supported)
- ✗ Use special Unicode characters not in the default font

## Tag Support Matrix

| Tag | Supported | Notes |
|-----|-----------|-------|
| `<b>` | ✓ | Bold text |
| `<i>` | ✓ | Italic text |
| `<u>` | ✓ | Underlined text |
| `<s>` | ✓ | Strikethrough |
| `<h1>`-`<h6>` | ✓ | Different heading sizes |
| `<sup>` | ✓ | Superscript (v1.8.2+) |
| `<sub>` | ✓ | Subscript (v1.8.2+) |
| `<br/>` | ✓ | Line break |
| `<p>` | ✓ | Paragraph |
| `<ul>`, `<ol>`, `<li>` | ✓ | Lists |
| `<table>`, `<tr>`, `<td>` | ✓ | Nested tables |
| `colspan` | ✓ | Column spanning (in nested tables) |
| `<span>` | ✗ | Not supported |
| `<div>` | ✗ | Not supported |
| CSS | ✗ | Not supported |

## Examples by Use Case

### Scientific Formulas
```html
"Water: H<sub>2</sub>O<br/>
Carbon Dioxide: CO<sub>2</sub><br/>
Einstein's Equation: E=mc<sup>2</sup>"
```

### Product Information
```html
"<b>Widget Pro<sup>TM</sup></b><br/>
<i>The professional choice</i><br/>
<u>Only $99.99</u>"
```

### Instructions
```html
"<ol>
  <li>Connect to power</li>
  <li>Press <b>Start</b></li>
  <li>Wait for <i>Ready</i> indicator</li>
</ol>"
```

### Data Table
```html
"<table>
  <tr><td><b>Metric</b></td><td><b>Value</b></td></tr>
  <tr><td>CPU</td><td>2.4 GHz</td></tr>
  <tr><td>RAM</td><td>16 GB</td></tr>
</table>"
```

## See Also

- [Tutorial 02: HTML Formatting](Tutorial-02-HTML-Formatting) - Live examples
- [Tutorial 10: Nested Tables](Tutorial-10-Nested-Tables) - Nested table examples
- [API Reference](API-Reference) - Cell class documentation
