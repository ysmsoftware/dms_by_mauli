package com.dms.app.config;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;

public class BarCodeImage {

	public static BufferedImage generateCode128BarcodeImage(String barcodeText) throws Exception {
	    Barcode barcode = BarcodeFactory.createCode128(barcodeText); //createEAN13(barcodeText);
	    //barcode.setFont(BARCODE_TEXT_FONT);
	    return BarcodeImageHandler.getImage(barcode);
	}
	
	public static boolean getBarCodeImage(String barcodeText, String folderPath) throws IOException, Exception {	    
		// Write the Buffered Image into an output file
		return ImageIO.write(generateCode128BarcodeImage(barcodeText), "png", new File(folderPath+barcodeText+".png")); 		   
	}
}
