package com.anudeepreddy.text_to_speech_backend.Service;

import com.anudeepreddy.text_to_speech_backend.DTO.Voice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoiceService {

    public List<Voice> getVoices(){
        return List.of(
                new Voice("Male","Puck"),
                new Voice("Female","Kore")
        );
    }
}
