package festival.gui;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import festival.Event;

public class Pdf {
	private static String FILE = "c:/temp/yourDayPlan.pdf";
	private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
			Font.BOLD);
	private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
			Font.NORMAL, BaseColor.RED);
	private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
			Font.BOLD);
	private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
			Font.BOLD); 
	private static Document document = new Document(); 
	// The owner of the document. 
	private static String userName; //= "DEFAULT_USERNAME"; 
	
	public void addLineUp(ArrayList<Event> lineUp) throws DocumentException, IOException {
		PdfWriter.getInstance(document, new FileOutputStream(FILE));
		document.open(); 
		addMetaData(document); 
		document.add(new Paragraph("Your Day Plan:")); 
		document.add(new Paragraph("\n")); 
		for (int i = 0; i < lineUp.size(); i ++ ) {
			document.add(new Paragraph(lineUp.get(i).toString())); 
		}
		document.close(); 
		Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + FILE); 
	}
	
	public void addOwner(String name) {
		this.userName = name; 
	}
	
	public void addMetaData(Document document) {
		document.addTitle("Line Up of - "+userName);
		document.addCreator(userName);
		document.addAuthor(userName);
		document.addKeywords("Festival Line Up App"); 
		document.addSubject("Line Up V 1.0"); 
	}
	
}
