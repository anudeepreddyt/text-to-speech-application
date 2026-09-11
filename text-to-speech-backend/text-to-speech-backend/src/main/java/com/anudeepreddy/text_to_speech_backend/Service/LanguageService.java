package com.anudeepreddy.text_to_speech_backend.Service;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageService {

    public List<String> getLanguages(){
        return List.of(
                "English",
                "Hindi",
                "Gujarati",
                "Marathi",
                "Spanish",
                "French",
                "German"
        );
    }
}
