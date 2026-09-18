package com.anudeepreddy.text_to_speech_backend.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {

    @NotBlank(message = "Username can't be blank")
    private String username;
    @NotBlank(message = "password can't be blank")
    private String password;
}
