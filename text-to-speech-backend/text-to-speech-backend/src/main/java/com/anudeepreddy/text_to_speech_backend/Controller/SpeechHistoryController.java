package com.anudeepreddy.text_to_speech_backend.Controller;

import com.anudeepreddy.text_to_speech_backend.DTO.SpeechHistoryDTO;
import com.anudeepreddy.text_to_speech_backend.Service.TtsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "https://text-to-speech-application-frontend.onrender.com")
public class SpeechHistoryController {

    private final TtsService ttsService;

    public SpeechHistoryController(TtsService ttsService) {
        this.ttsService = ttsService;
    }

    @GetMapping("/speechHistory")
    public ResponseEntity<List<SpeechHistoryDTO>> speechHistory(Authentication authentication){
        String username=authentication.getName();
        return ResponseEntity.ok(ttsService.speechHistory(username));
    }
}
