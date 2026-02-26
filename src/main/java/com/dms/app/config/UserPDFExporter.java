package com.dms.app.config;

import java.io.IOException;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import com.dms.app.model.User;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class UserPDFExporter {	
   
     
    public void export(HttpServletResponse response, User user) throws DocumentException, IOException {
       
    	//String serverPath = "G:/STSExam/Sample Test Workspace/stsexam/src/main/resources/static/dist/img/";
    	//String serverFontPath = "G:/STSExam/Sample Test Workspace/stsexam/src/main/resources/static/dist/fonts/";
    	
    	String serverPath = "C:/STSE/resources/img/";
    	String serverFontPath = "C:/STSE/resources/fonts/";
    	
    	Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
         
        document.open();
		      
		String bgImageFilePath = serverPath+"certificate.png";
		
		PdfContentByte canvas = writer.getDirectContentUnder();
	    Image image = Image.getInstance(bgImageFilePath);
	    image.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
	    image.setAbsolutePosition(0, 0);	        
	    canvas.addImage(image);
	    
	    String barcode = serverPath+"barcode.gif";	     
	    Image image212 = Image.getInstance(barcode);
	    image212.scaleToFit(125, 30);
	    image212.setAbsolutePosition(85, 750);	    
	    canvas.addImage(image212);

	    String logoImageFilePath = serverPath+"stse-logo.png";	     
	    Image image1 = Image.getInstance(logoImageFilePath);
	    image1.scaleToFit(237, 230);
	    image1.setAbsolutePosition(175, 664);	    
	    canvas.addImage(image1);
	    	    
	    FontFactory.register(serverFontPath+"Montserrat-Bold_0.ttf","certificate_font");        
	  	Font font = FontFactory.getFont("certificate_font");
	  	font.setSize(12); 		
	  	font.setColor(new BaseColor(3,34, 64)); // #011d6b
	  	font.setStyle(Font.BOLD);
	  	
	  	String certImageFilePath = serverPath+"cert.png";	     
	    Image image2 = Image.getInstance(certImageFilePath);
	    image2.scaleToFit(207, 200);
	    image2.setAbsolutePosition(190, 610);	    
	    canvas.addImage(image2);
	    
		String appreImageFilePath = serverPath+"appre.png";	     
	    Image image3 = Image.getInstance(appreImageFilePath);
	    image3.scaleToFit(160, 155);
	    image3.setAbsolutePosition(220, 555);	    
	    canvas.addImage(image3);
	    
		int rank = 441;		  	
	  		    		
	    FontFactory.register(serverFontPath+"tt0140m.ttf","title_font");        
	  	Font font1 = FontFactory.getFont("title_font");
	  	font1.setSize(22); 		
	  	font1.setColor(new BaseColor(177,135, 27)); // #011d6b 
	  	font1.setStyle(Font.BOLD);
	  		    
		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new  Phrase(user.getName(), font1), 300, 510, 0); 
				 		
		String lineImageFilePath1 = serverPath+"line.png";	     
		Image image411 = Image.getInstance(lineImageFilePath1);
		image411.scaleToFit(400, 400);
		image411.setAbsolutePosition(100, 490);	    
		canvas.addImage(image411);
		

	  	 FontFactory.register(serverFontPath+"Montserrat-Bold_0.ttf","certificate_font");        
		  	Font font11 = FontFactory.getFont("certificate_font");
		  	font11.setSize(10); 		
		  	font11.setColor(new BaseColor(3,34, 64)); // #011d6b
		  	font11.setStyle(Font.BOLD);
	  		 		
		//String address = user.getAddress();
		//int addressLength = address.length();
		//System.out.println("address : "+addressLength);
		
		//if(!address.equals("None")) {			
		//	if(addressLength <= 65) {
				ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new  Phrase("Nashik", font), 300, 470, 0);
		/*	}else {
				ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new  Phrase(address.substring(0,65), font11), 300, 479, 0);
				ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new  Phrase(address.substring(66,addressLength-1), font11), 300, 466, 0);
			}	*/
			
		/*
		 * } else if(address.equals("None")) { ColumnText.showTextAligned(canvas,
		 * Element.ALIGN_CENTER, new Phrase("Nashik", font), 300, 470, 0); }
		 */
		
		String lineImageFilePath2 = serverPath+"line.png";	     
		Image image412 = Image.getInstance(lineImageFilePath2);
		image412.scaleToFit(400, 400);
		image412.setAbsolutePosition(100, 455);	    
		canvas.addImage(image412);
		
		String contImageFilePath = serverPath+"conte2.png";	     
		Image image4 = Image.getInstance(contImageFilePath);
		image4.scaleToFit(400, 400);
		image4.setAbsolutePosition(100, 330);	    
		canvas.addImage(image4);
		
		FontFactory.register(serverFontPath+"tt0140m.ttf","grade_font");        
	  	Font font2 = FontFactory.getFont("grade_font");
	  	font2.setSize(24); 		
	  	font2.setColor(new BaseColor(252, 3, 173)); 
	  	font2.setStyle(Font.BOLD);
		
		//ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new  Phrase("EXCELLENT", font2), 300, 300, 0);
		
		/*
		 * String gradeImageFilePath = serverPath+"excellent.png"; Image image8 =
		 * Image.getInstance(gradeImageFilePath); image8.scaleToFit(150, 150);
		 * image8.setAbsolutePosition(225, 300); canvas.addImage(image8);
		 */
		 	
		/*
		 * String gradeImageFilePath = serverPath+"good.png"; Image image8 =
		 * Image.getInstance(gradeImageFilePath); image8.scaleToFit(80, 80);
		 * image8.setAbsolutePosition(250, 300); canvas.addImage(image8);
		 */
				
	//	String gradeImageFilePath = serverPath+"satisfactory.png"; Image image8 =
	//	Image.getInstance(gradeImageFilePath); image8.scaleToFit(170, 170);
	//	image8.setAbsolutePosition(225, 300); canvas.addImage(image8);
		
		    FontFactory.register(serverFontPath+"tt0144m_.ttf","title_font1");        
		  	Font font12 = FontFactory.getFont("title_font1");
		  	font12.setSize(22); 		
		  	font12.setColor(new BaseColor(212, 27, 133)); // #011d6b 
		  	font12.setStyle(Font.BOLD);
		  		    
			//ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new  Phrase("SATISFACTORY", font12), 300, 300, 0); 
		  
		  	String txt = "";
		  	if(rank <= 43) txt = "EXCELLENT";
		  	else if(rank >= 44 && rank <= 129) txt = "VERY GOOD";
		  	else if(rank >= 130 && rank <= 361) txt = "GOOD";
		  	else if(rank >= 362 && rank <= 440) txt = "SATISFACTORY";
		  	else txt = "NOT ATTAINED (FAIL)";
		  	
		     ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new  Phrase(txt, font12), 300, 300, 0); 
		 
		/*
		 * PdfContentByte pcb = writer.getDirectContent(); // Creating a table float []
		 * pointColumnWidths = {200F, 95F, 110F}; PdfPTable table = new
		 * PdfPTable(pointColumnWidths); table.setTotalWidth(405F);
		 * 
		 * 
		 * // Adding cells to the table PdfPCell p1 = new PdfPCell(new
		 * Phrase("Particular", font)); p1.setPadding(5); table.addCell(p1);
		 * 
		 * PdfPCell p2 = new PdfPCell(new Phrase("Max. Marks", font)); p2.setPadding(5);
		 * p2.setHorizontalAlignment(1); table.addCell(p2);
		 * 
		 * PdfPCell p3 = new PdfPCell(new Phrase("Marks Obtained", font));
		 * p3.setPadding(5); p3.setHorizontalAlignment(1); table.addCell(p3);
		 * 
		 * PdfPCell p4 = new PdfPCell(new Phrase("Samrudhhi Talent Search Exam", font));
		 * p4.setPadding(5); table.addCell(new PdfPCell(p4));
		 * 
		 * PdfPCell p5 = new PdfPCell(new PdfPCell(new Phrase("100", font)));
		 * p5.setPadding(5); p5.setHorizontalAlignment(1); table.addCell(new
		 * PdfPCell(p5));
		 * 
		 * String marks = "-"; if(studentExam != null) { marks =
		 * String.valueOf(studentExam.getTotalMarks()); }
		 * 
		 * PdfPCell p6 = new PdfPCell(new PdfPCell(new Phrase(marks, font)));
		 * p6.setPadding(5); p6.setHorizontalAlignment(1); table.addCell(new
		 * PdfPCell(p6));
		 * 
		 * table.writeSelectedRows(0, -1, 100, 270, pcb);
		 */
	    // Adding Table to document        
	    //document.add(table);                  
				
		String symbolImageFilePath = serverPath+"symbol.png";	     
		Image image7 = Image.getInstance(symbolImageFilePath);
		image7.scaleToFit(90, 90);
		image7.setAbsolutePosition(260,190);	    
		canvas.addImage(image7);	
		
		
		String signImageFilePath1 = serverPath+"birari_sir_sign.png";	     
		Image image671 = Image.getInstance(signImageFilePath1);
		image671.scaleToFit(130, 80);
		image671.setAbsolutePosition(87,120);	    
		canvas.addImage(image671);
		
		String signImageFilePath2 = serverPath+"pathan_sir_sign.png";	     
		Image image672 = Image.getInstance(signImageFilePath2);
		image672.scaleToFit(130,80);
		image672.setAbsolutePosition(235,120);	    
		canvas.addImage(image672);
		
		String signImageFilePath3 = serverPath+"avhad_sir_sign.png";	     
		Image image6 = Image.getInstance(signImageFilePath3);
		image6.scaleToFit(130, 80);
		image6.setAbsolutePosition(383,120);	    
		canvas.addImage(image6);	
				
		String footerImageFilePath = serverPath+"footer.png";	     
		Image image5 = Image.getInstance(footerImageFilePath);
		image5.scaleToFit(435, 400);
		image5.setAbsolutePosition(80,75);	    
		canvas.addImage(image5);
				
		String certNumber = rank < 10?"0000"+rank:rank <100?"000"+rank:"00"+rank;
		
		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new  Phrase("Certificate No. "+certNumber, font), 445, 755, 0);
		
		ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new  Phrase("Year : 2022", font), 475, 735, 0); 
		/*
		 * Paragraph p = new Paragraph("List of Users", font);
		 * p.setAlignment(Paragraph.ALIGN_CENTER);
		 * 
		 * document.add(p);
		 * 
		 * PdfPTable table = new PdfPTable(5); table.setWidthPercentage(100f);
		 * table.setWidths(new float[] {1.5f, 3.5f, 3.0f, 3.0f, 1.5f});
		 * table.setSpacingBefore(10);
		 * 
		 * writeTableHeader(table); writeTableData(table);
		 * 
		 * document.add(table);
		 */
         
        document.close();
         
    }
    
   
}
