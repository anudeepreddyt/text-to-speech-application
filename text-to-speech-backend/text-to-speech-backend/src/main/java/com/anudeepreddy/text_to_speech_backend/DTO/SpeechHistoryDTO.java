package com.anudeepreddy.text_to_speech_backend.DTO;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpeechHistoryDTO {

    private String text;
    private byte[] audioformat;
}
