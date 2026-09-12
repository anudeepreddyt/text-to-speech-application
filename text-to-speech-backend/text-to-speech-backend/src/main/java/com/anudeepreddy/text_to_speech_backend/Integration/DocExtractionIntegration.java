package com.anudeepreddy.text_to_speech_backend.Integration;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class DocExtractionIntegration {

    public String extractText(MultipartFile file){

        if(file == null || file.isEmpty()){
            throw  new IllegalArgumentException("file is empty");
        }

        String fileName=file.getOriginalFilename();

        if(file==null){
            throw new IllegalArgumentException("Invalid file name");
        }

        String lowerCaseFileName=fileName.toLowerCase();

        try{

            if(lowerCaseFileName.endsWith(".pdf")){

                return extactPdfData(file);
            }
            else if(lowerCaseFileName.endsWith(".docx")){
                    return extactDocxData(file);
            }
            throw new IllegalArgumentException("only pdf and docx format is supported");

        }
        catch (Exception e) {
            throw new RuntimeException("Failed to extract data from file");
        }

    }

    public String extactPdfData(MultipartFile file)throws IOException{

            byte[] fileBytes = file.getBytes();
            try (PDDocument document = Loader.loadPDF(fileBytes)) {

                PDFTextStripper stripper = new PDFTextStripper();

                return stripper.getText(document).trim();
            }


    }

    public String extactDocxData(MultipartFile file) throws IOException{
        try (XWPFDocument document =
                     new XWPFDocument(file.getInputStream())) {

            StringBuilder text = new StringBuilder();

            for (XWPFParagraph paragraph :
                    document.getParagraphs()) {

                text.append(paragraph.getText());
                text.append("\n");
            }

            return text.toString().trim();
        }
    }
}
