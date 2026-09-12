package com.anudeepreddy.text_to_speech_backend.Service;

import com.anudeepreddy.text_to_speech_backend.Integration.ApiIntegration;
import com.anudeepreddy.text_to_speech_backend.DTO.TtsRequest;
import com.anudeepreddy.text_to_speech_backend.Model.SpeechHistory;
import com.anudeepreddy.text_to_speech_backend.Repository.SpeechRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TtsService {

    private ApiIntegration apiIntegration;
    private SpeechRepository speechRepository;

    @Autowired
    public TtsService(ApiIntegration apiIntegration,SpeechRepository speechRepository) {
        this.apiIntegration = apiIntegration;
        this.speechRepository = speechRepository;
    }

    public byte[] generateSpeech(TtsRequest ttsRequest){
        byte[] audio= apiIntegration.generateSpeech(ttsRequest.getText(),ttsRequest.getVoice(),ttsRequest.getLanguage());

        SpeechHistory history=new SpeechHistory();

        history.setText(ttsRequest.getText());
        history.setLanguage(ttsRequest.getLanguage());
        history.setVoice(ttsRequest.getVoice());
        history.setAudioFormat("wav");
        history.setCreatedAt(LocalDateTime.now());

        speechRepository.save(history);


        return audio;
    }
}
