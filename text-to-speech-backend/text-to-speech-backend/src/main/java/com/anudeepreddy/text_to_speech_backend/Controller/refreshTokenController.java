package com.anudeepreddy.text_to_speech_backend.Controller;

import com.anudeepreddy.text_to_speech_backend.DTO.AuthTokenDTO;
import com.anudeepreddy.text_to_speech_backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class refreshTokenController {

    private final UserService userService;

    @Autowired
    public refreshTokenController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<AuthTokenDTO> refreshToken(@RequestParam String token){
        return ResponseEntity.ok(userService.refresh(token));
    }
}
