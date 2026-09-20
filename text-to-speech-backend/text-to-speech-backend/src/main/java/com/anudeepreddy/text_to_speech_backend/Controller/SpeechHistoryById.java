package com.anudeepreddy.text_to_speech_backend.Controller;

import com.anudeepreddy.text_to_speech_backend.Model.SpeechHistory;
import com.anudeepreddy.text_to_speech_backend.Repository.SpeechRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "https://text-to-speech-application-frontend.onrender.com")
public class SpeechHistoryById {

    private final SpeechRepository speechRepository;

    public SpeechHistoryById(SpeechRepository speechRepository) {
        this.speechRepository = speechRepository;
    }

    @GetMapping("/speechHistory/{id}/audio")
    public ResponseEntity<byte[]> getAudio(@PathVariable Integer id) {

        SpeechHistory history = speechRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Audio not found"));

        return ResponseEntity.ok()
                .header("Content-Type", "audio/wav")
                .body(history.getAudioFormat());
    }
}
