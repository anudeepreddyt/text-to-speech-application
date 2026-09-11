package com.anudeepreddy.text_to_speech_backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Voice {

    private String gender;
    private String voiceName;
}
