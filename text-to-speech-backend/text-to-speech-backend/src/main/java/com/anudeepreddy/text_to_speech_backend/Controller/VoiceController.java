package com.anudeepreddy.text_to_speech_backend.Controller;

import com.anudeepreddy.text_to_speech_backend.DTO.Voice;
import com.anudeepreddy.text_to_speech_backend.Service.VoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class VoiceController {

    private VoiceService voiceService;
    @Autowired
    public VoiceController(VoiceService voiceService) {
        this.voiceService = voiceService;
    }



          @GetMapping("/voices")
        public ResponseEntity<List<Voice>> getVoices(){
              return ResponseEntity.ok(voiceService.getVoices());
          }
}
