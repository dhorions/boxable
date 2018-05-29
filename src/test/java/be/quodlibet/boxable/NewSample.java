package be.quodlibet.boxable;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.Test;

import be.quodlibet.boxable.page.DefaultPageProvider;

public class NewSample {
	/**
	 * <p>
	 * Test for a  table using the following features : 
	 * <ul>
	 * <li> text wrapping inside a cell </li>
	 * </ul>
	 * </p>
	 *
	 * @throws IOException
	 */
	@Test
	public void SampleTest() throws IOException {

		// Set margins
		float margin = 10;

		// Initialize Document
		PDDocument doc = new PDDocument();
		PDPage page = new PDPage();
		doc.addPage(page);

		// Initialize table
		float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
		float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
		boolean drawContent = true;
		boolean drawLines = true;
		float yStart = yStartNewPage;
		float bottomMargin = 70;
		//Table table = new Table(yStart, yStartNewPage, bottomMargin, tableWidth, margin, drawLines, drawContent);
		Table table = new Table.Builder()
				.yStart(yStart)
				.yStartNewPage(yStartNewPage)
				.pageBottomMargin(bottomMargin)
				.width(tableWidth)
				.margin(margin)
				.drawLines(drawLines)
				.drawContent(drawContent)
				.build();

		// Create Header row
		Row row = table.createRow(15f);
		Cell cell = row.createCell((100 / 3f),
				"Hellooooooooooo",
				HorizontalAlignment.get("center"), VerticalAlignment.get("top"));
		cell.setFontSize(6);

		Cell cell2 = row.createCell((100 / 3f), "<i>Here is text in italic</i>",
				HorizontalAlignment.get("center"), VerticalAlignment.get("middle"));
		cell2.setFontSize(6);

		Cell cell3 = row.createCell((100 / 3f), "<b><i>Here is text in bold and italic</i></b>",
				HorizontalAlignment.get("center"), VerticalAlignment.get("bottom"));
		cell3.setFontSize(6);
		//draw document
		TableDrawer<PDPage> drawer = new TableDrawer<PDPage>(table, doc, page, new DefaultPageProvider(doc, page.getMediaBox()));
		drawer.draw();

		// Save the document
		File file = new File("target/BoxableNewSample.pdf");
		System.out.println("Sample file saved at : " + file.getAbsolutePath());
		createParentDirs(file);
		doc.save(file);
		doc.close();
	}

	/*
	 * Adapted from https://github.com/google/guava/blob/master/guava/src/com/google/common/io/java
	 */
	private static void createParentDirs(File file) throws IOException {
		File parent = file.getCanonicalFile().getParentFile();
		if (parent != null) {
			parent.mkdirs();
			if (!parent.isDirectory()) {
				throw new IOException("Unable to create parent directories of " + file);
			}
		}
	}
}
