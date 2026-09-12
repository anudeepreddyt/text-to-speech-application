package com.anudeepreddy.text_to_speech_backend.Controller;

import com.anudeepreddy.text_to_speech_backend.DTO.TtsRequest;
import com.anudeepreddy.text_to_speech_backend.Integration.DocExtractionIntegration;
import com.anudeepreddy.text_to_speech_backend.Service.TtsService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class DocumentTextController {

    private final DocExtractionIntegration docExtractionIntegration;
    private final TtsService ttsService;

    public DocumentTextController(DocExtractionIntegration docExtractionIntegration,TtsService ttsService) {
        this.docExtractionIntegration = docExtractionIntegration;
        this.ttsService = ttsService;
    }

    @PostMapping(value = "/document",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> generateSpeechFromDoc(
            @RequestParam("file") MultipartFile file,
            @RequestParam("voice") String voice,
            @RequestParam("language") String language
            ){

        String extractText= docExtractionIntegration.extractText(file);

        if(extractText==null || extractText.isBlank()){
            throw new RuntimeException("No readle text found from the document");
        }

        TtsRequest ttsRequest=new TtsRequest(
                extractText,
                voice,
                language
        );

        byte[] audio=ttsService.generateSpeech(ttsRequest);

        return ResponseEntity.ok().contentType(MediaType.parseMediaType("audio/wav")).body(audio);
    }


}
