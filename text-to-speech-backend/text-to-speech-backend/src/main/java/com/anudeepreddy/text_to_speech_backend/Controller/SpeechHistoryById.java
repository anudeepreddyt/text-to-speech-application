package com.anudeepreddy.text_to_speech_backend.Controller;

import com.anudeepreddy.text_to_speech_backend.Model.SpeechHistory;
import com.anudeepreddy.text_to_speech_backend.Repository.SpeechRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
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
