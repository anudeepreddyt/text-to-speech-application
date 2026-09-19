package com.anudeepreddy.text_to_speech_backend.Controller;

import com.anudeepreddy.text_to_speech_backend.Service.LanguageService;
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
public class LanguageController {
    private LanguageService languageService;
    @Autowired
    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    
    @GetMapping("/languages")
    public ResponseEntity<List<String>> getLanguages(){
        return ResponseEntity.ok(languageService.getLanguages());
    }

}
