package com.anudeepreddy.text_to_speech_backend.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TtsRequest {


    @NotBlank(message = "text can't be empty")
    @Size(max= 500,message = "Text can't exceed 500 chars")
    private String text;
    @NotBlank(message = "voice can't be empty")
    private String voice;
    @NotBlank(message = "Language can't be empty")
    private String language;
}
