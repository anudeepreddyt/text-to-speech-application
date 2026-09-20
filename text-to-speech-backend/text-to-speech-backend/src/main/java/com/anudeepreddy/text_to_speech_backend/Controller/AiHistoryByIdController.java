package com.anudeepreddy.text_to_speech_backend.Controller;

import com.anudeepreddy.text_to_speech_backend.Model.SpeechHistory;
import com.anudeepreddy.text_to_speech_backend.Repository.SpeechRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "https://text-to-speech-application-frontend.onrender.com")
public class AiHistoryByIdController {

    private final SpeechRepository speechRepository;

    public AiHistoryByIdController(SpeechRepository speechRepository) {
        this.speechRepository = speechRepository;
    }

    @GetMapping("/AiHistory/{id}/audio")
    public ResponseEntity<byte[]> getAudio(@PathVariable Integer id) {

        SpeechHistory history = speechRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Audio not found"));

        return ResponseEntity.ok()
                .header("Content-Type", "audio/wav")
                .body(history.getAiAudio());
    }

    @GetMapping("/AiHistory/{id}")
    public ResponseEntity<String> getAiReply(@PathVariable Integer id) {

        SpeechHistory history = speechRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Audio not found"));

        return ResponseEntity.ok(history.getAiReply());
    }
}
