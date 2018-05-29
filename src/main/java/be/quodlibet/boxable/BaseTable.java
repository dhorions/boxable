package be.quodlibet.boxable;

import java.io.IOException;
import java.util.Collection;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

import be.quodlibet.boxable.page.DefaultPageProvider;


/**
 * Created by dgautier on 3/18/2015.
 * Deprecated: this class now acts as a temporary adapter for the new Table/TableDrawer system
 */
@Deprecated
public class BaseTable {
	public Table table;
	public TableDrawer<PDPage> tableDrawer;

	public BaseTable(float yStart, float yStartNewPage, float bottomMargin, float width, float 
			margin, PDDocument document, PDPage currentPage, boolean drawLines, boolean drawContent) throws IOException {
		table = new Table.Builder()
				.yStart(yStart)
				.yStartNewPage(yStartNewPage)
				.pageTopMargin(0)
				.pageBottomMargin(bottomMargin)
				.width(width)
				.margin(margin)
				.drawLines(drawLines)
				.drawContent(drawContent)
				.build();
		tableDrawer = new TableDrawer<PDPage>(table, document, currentPage, new DefaultPageProvider(document, currentPage.getMediaBox()));
		//document
	}
	public BaseTable(float yStart, float yStartNewPage, float pageTopMargin, float bottomMargin, float width, 
			float margin, PDDocument document, PDPage currentPage, boolean drawLines, boolean drawContent) throws IOException {
		table = new Table.Builder()
				.yStart(yStart)
				.yStartNewPage(yStartNewPage)
				.pageTopMargin(pageTopMargin)
				.pageBottomMargin(bottomMargin)
				.width(width)
				.margin(margin)
				.drawLines(drawLines)
				.drawContent(drawContent)
				.build();
		tableDrawer = new TableDrawer<PDPage>(table, document, currentPage, new DefaultPageProvider(document, currentPage.getMediaBox()));
	}
	
	public void addHeaderRow(Row row) {
		table.addHeaderRow(row);
	}
	
	public Row createRow(float f) {
		return table.createRow(f);
	}
	
	public void draw() {
		try {
			tableDrawer.draw();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Collection<? extends PDOutlineItem> getBookmarks() {
		return tableDrawer.getBookmarks();
	}
	public void removeAllBorders(boolean b) {
		tableDrawer.removeAllBorders(b);
	}
	public void setLineSpacing(float f) {
		tableDrawer.setLineSpacing(f);
	}
}
