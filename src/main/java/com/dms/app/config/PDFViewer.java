package com.dms.app.config;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PDFViewer extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String pdfFilePath;

    public PDFViewer(String pdfFilePath) {
        this.pdfFilePath = pdfFilePath;
        initialize();
    }

    private void initialize() {
        setTitle("PDF Viewer");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            PDDocument document = PDDocument.load(new File(pdfFilePath));
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            int pageCount = document.getNumberOfPages();
            BufferedImage[] images = new BufferedImage[pageCount];

            for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
                images[pageIndex] = pdfRenderer.renderImageWithDPI(pageIndex, 300, ImageType.RGB);
            }

            document.close();

            JTabbedPane tabbedPane = new JTabbedPane();

            for (int i = 0; i < images.length; i++) {
                Image image = images[i];
                JLabel label = new JLabel(new ImageIcon(image));
                tabbedPane.addTab("Page " + (i + 1), label);
            }

            getContentPane().add(tabbedPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String pdfFilePath = "D://PICTURES/My docs.pdf";
            new PDFViewer(pdfFilePath).setVisible(true);
        });
    }
}

