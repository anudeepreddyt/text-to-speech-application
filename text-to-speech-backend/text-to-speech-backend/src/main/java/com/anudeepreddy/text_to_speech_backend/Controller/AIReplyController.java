package com.anudeepreddy.text_to_speech_backend.Controller;

import com.anudeepreddy.text_to_speech_backend.DTO.GenerateReplyAudioDTO;
import com.anudeepreddy.text_to_speech_backend.DTO.GenerateReplyQueDTO;
import com.anudeepreddy.text_to_speech_backend.Integration.GenerateReplyIntegration;
import com.anudeepreddy.text_to_speech_backend.Model.SpeechHistory;
import com.anudeepreddy.text_to_speech_backend.Repository.SpeechRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class AIReplyController {
    @Autowired
    private GenerateReplyIntegration generateReplyIntegration;
    @Autowired
    private SpeechRepository speechRepository;

    @GetMapping("/generateReply/{id}")
    public ResponseEntity<String> generateReply(@PathVariable Integer id, Authentication authentication){
        String username=authentication.getName();
        return ResponseEntity.ok(generateReplyIntegration.generateReply( id,username));
    }

    @GetMapping("/generateAi/{id}/audio")
    public ResponseEntity<byte[]> generateAiAudio(Authentication authentication, @PathVariable Integer id){
        String username=authentication.getName();
        byte[] audio=generateReplyIntegration.generateAiAudio(id,username);
        return ResponseEntity.ok()
                .header("Content-Type", "audio/wav")
                .body(audio);
    }


}
