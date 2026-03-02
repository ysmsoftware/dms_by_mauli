package com.dms.app.config;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import java.io.File;
import java.io.IOException;

public class AddBarcodeToPDF {

    public static void addBarcodeToPdf(String pdfPath, String pdfName, String barcodePath) {
        try {
            // Load the existing PDF
            File file = new File(pdfPath+pdfName); //+".pdf" 
            //System.out.println(pdfPath+pdfName);
            PDDocument document = PDDocument.load(file);

            // Load the barcode image
            File barcodeFile = new File(barcodePath);
            PDImageXObject barcodeImage = PDImageXObject.createFromFileByContent(barcodeFile, document);

            // Iterate over each page
            for (PDPage page : document.getPages()) {
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {
                    // Place the barcode image at the bottom (adjust coordinates as necessary)
                    float x = page.getMediaBox().getLowerLeftX() + (page.getMediaBox().getWidth() - barcodeImage.getWidth()) / 2;
                    float y = page.getMediaBox().getLowerLeftY();
                    contentStream.drawImage(barcodeImage, x, y, barcodeImage.getWidth(), barcodeImage.getHeight());
                }
            }

            // Save the modified PDF
            document.save(pdfPath+pdfName);
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
