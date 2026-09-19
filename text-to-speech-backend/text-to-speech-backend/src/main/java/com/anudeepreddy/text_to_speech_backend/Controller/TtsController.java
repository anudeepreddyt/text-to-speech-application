package com.anudeepreddy.text_to_speech_backend.Controller;

import com.anudeepreddy.text_to_speech_backend.DTO.TtsRequest;
import com.anudeepreddy.text_to_speech_backend.Service.TtsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TtsController {

    private TtsService ttsService;
    @Autowired
    public TtsController(TtsService ttsService) {
        this.ttsService = ttsService;
    }




    @PostMapping(value = "/tts",produces = "audio/wav")
    public ResponseEntity<byte[]> generateSpeech(@Valid @RequestBody TtsRequest ttsRequest, Authentication authentication){
        String username=authentication.getName();
        byte[] audio=ttsService.generateSpeech(ttsRequest,username);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/wav"))
                .body(audio);

    }
}
