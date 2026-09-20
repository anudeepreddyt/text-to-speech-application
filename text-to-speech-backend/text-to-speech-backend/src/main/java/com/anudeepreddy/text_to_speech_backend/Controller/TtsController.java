package com.anudeepreddy.text_to_speech_backend.Controller;

import com.anudeepreddy.text_to_speech_backend.DTO.TtsRequest;
import com.anudeepreddy.text_to_speech_backend.Model.SpeechHistory;
import com.anudeepreddy.text_to_speech_backend.Service.TtsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
        SpeechHistory history=ttsService.generateSpeech(ttsRequest,username);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/wav"))
                .header("X-Speech-Id", String.valueOf(history.getId()))
                .body(history.getAudioFormat());

    }
}
