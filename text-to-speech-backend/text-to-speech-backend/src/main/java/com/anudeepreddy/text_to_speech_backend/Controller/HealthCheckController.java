package com.anudeepreddy.text_to_speech_backend.Controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class HealthCheckController {

    @GetMapping("/health")
    public ResponseEntity<String> checkHealth(){
        return ResponseEntity.ok("Status Up...");
    }

}
