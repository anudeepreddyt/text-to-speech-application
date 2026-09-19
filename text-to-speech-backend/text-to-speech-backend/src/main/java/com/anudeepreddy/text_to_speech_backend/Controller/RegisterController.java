package com.anudeepreddy.text_to_speech_backend.Controller;

import com.anudeepreddy.text_to_speech_backend.DTO.UserLoginDTO;
import com.anudeepreddy.text_to_speech_backend.Model.UsersModel;
import com.anudeepreddy.text_to_speech_backend.Service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class RegisterController {
    private UserService userService;

    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UsersModel usersModel){
        return ResponseEntity.ok(userService.register(usersModel));
    }
}
