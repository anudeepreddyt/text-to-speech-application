package com.anudeepreddy.text_to_speech_backend.Controller;

import com.anudeepreddy.text_to_speech_backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")

public class LogoutController {
    @Autowired
    private UserService userService;

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String token){
        return ResponseEntity.ok(userService.logout(token));
    }
}
