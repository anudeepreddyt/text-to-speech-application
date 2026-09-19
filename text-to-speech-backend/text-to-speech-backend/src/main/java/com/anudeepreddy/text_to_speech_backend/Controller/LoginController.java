package com.anudeepreddy.text_to_speech_backend.Controller;

import com.anudeepreddy.text_to_speech_backend.DTO.AuthResponseDTO;
import com.anudeepreddy.text_to_speech_backend.DTO.UserLoginDTO;
import com.anudeepreddy.text_to_speech_backend.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class LoginController {

    private UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody UserLoginDTO userLoginDTO){
        return ResponseEntity.ok(userService.login(userLoginDTO));
    }
}
