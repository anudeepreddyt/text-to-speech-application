package com.anudeepreddy.text_to_speech_backend.Service;

import com.anudeepreddy.text_to_speech_backend.DTO.SpeechHistoryDTO;
import com.anudeepreddy.text_to_speech_backend.Integration.ApiIntegration;
import com.anudeepreddy.text_to_speech_backend.DTO.TtsRequest;
import com.anudeepreddy.text_to_speech_backend.Model.SpeechHistory;
import com.anudeepreddy.text_to_speech_backend.Model.UsersModel;
import com.anudeepreddy.text_to_speech_backend.Repository.SpeechRepository;
import com.anudeepreddy.text_to_speech_backend.Repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class TtsService {

    private ApiIntegration apiIntegration;
    private SpeechRepository speechRepository;

    @Autowired
    public TtsService(ApiIntegration apiIntegration,SpeechRepository speechRepository) {
        this.apiIntegration = apiIntegration;
        this.speechRepository = speechRepository;
    }

    @Autowired
    private UserRepository userRepository;

    public byte[] generateSpeech(TtsRequest ttsRequest,String username){
        byte[] audio= apiIntegration.generateSpeech(ttsRequest.getText(),ttsRequest.getVoice(),ttsRequest.getLanguage());
        UsersModel usersModel=userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("Unable to process request user might not exist"));
        SpeechHistory history=new SpeechHistory();

        history.setText(ttsRequest.getText());
        history.setLanguage(ttsRequest.getLanguage());
        history.setVoice(ttsRequest.getVoice());
        history.setAudioFormat(audio);
        history.setCreatedAt(LocalDateTime.now());
        history.setUsersModel(usersModel);

        speechRepository.save(history);


        return audio;
    }

    @Transactional(readOnly = true)
    public List<SpeechHistoryDTO> speechHistory(String username) {
        UsersModel usersModel=userRepository.findByUsername(username).orElseThrow(()-> new RuntimeException("username not found"));
        List<SpeechHistory> histories=speechRepository.findByUsersModel(usersModel);

        return histories.stream().map(history->
                new SpeechHistoryDTO(
                        history.getText(),
                        addWavHeader(history.getAudioFormat(),24000, 1, 16))

                ).toList();
    }

    private byte[] addWavHeader(
            byte[] pcmData,
            int sampleRate,
            int channels,
            int bitsPerSample) {

        int byteRate = sampleRate * channels * bitsPerSample / 8;
        int blockAlign = channels * bitsPerSample / 8;

        int dataSize = pcmData.length;
        int fileSize = 36 + dataSize;

        byte[] wav = new byte[44 + dataSize];

        // RIFF
        wav[0] = 'R';
        wav[1] = 'I';
        wav[2] = 'F';
        wav[3] = 'F';

        writeIntLE(wav, 4, fileSize);

        // WAVE
        wav[8] = 'W';
        wav[9] = 'A';
        wav[10] = 'V';
        wav[11] = 'E';

        // fmt
        wav[12] = 'f';
        wav[13] = 'm';
        wav[14] = 't';
        wav[15] = ' ';

        writeIntLE(wav, 16, 16); // PCM header size

        writeShortLE(wav, 20, (short) 1); // PCM format

        writeShortLE(wav, 22, (short) channels);

        writeIntLE(wav, 24, sampleRate);

        writeIntLE(wav, 28, byteRate);

        writeShortLE(wav, 32, (short) blockAlign);

        writeShortLE(wav, 34, (short) bitsPerSample);

        // data
        wav[36] = 'd';
        wav[37] = 'a';
        wav[38] = 't';
        wav[39] = 'a';

        writeIntLE(wav, 40, dataSize);

        // PCM audio data
        System.arraycopy(
                pcmData,
                0,
                wav,
                44,
                pcmData.length
        );

        return wav;
    }
    private void writeIntLE(byte[] buffer, int offset, int value) {

        buffer[offset] = (byte) (value & 0xff);
        buffer[offset + 1] = (byte) ((value >> 8) & 0xff);
        buffer[offset + 2] = (byte) ((value >> 16) & 0xff);
        buffer[offset + 3] = (byte) ((value >> 24) & 0xff);
    }
    private void writeShortLE(byte[] buffer, int offset, short value) {

        buffer[offset] = (byte) (value & 0xff);
        buffer[offset + 1] = (byte) ((value >> 8) & 0xff);
    }
}
