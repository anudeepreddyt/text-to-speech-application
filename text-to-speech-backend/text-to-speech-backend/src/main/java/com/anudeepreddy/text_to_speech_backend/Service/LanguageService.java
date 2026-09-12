package com.anudeepreddy.text_to_speech_backend.Service;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageService {

    public List<String> getLanguages(){
        return List.of(
                "English",
                "Hindi",
                "Telugu",
                "Tamil",
                "Kannada",
                "Malayalam",
                "Marathi",
                "Gujarati",
                "Bengali",
                "Punjabi",
                "Spanish",
                "French",
                "German",
                "Japanese",
                "Korean"
        );
    }
}
