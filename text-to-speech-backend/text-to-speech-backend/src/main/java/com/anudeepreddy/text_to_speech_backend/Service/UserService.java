package com.anudeepreddy.text_to_speech_backend.Service;

import com.anudeepreddy.text_to_speech_backend.DTO.AuthResponseDTO;
import com.anudeepreddy.text_to_speech_backend.DTO.AuthTokenDTO;
import com.anudeepreddy.text_to_speech_backend.DTO.UserLoginDTO;
import com.anudeepreddy.text_to_speech_backend.Model.RefreshToken;
import com.anudeepreddy.text_to_speech_backend.Model.UsersModel;
import com.anudeepreddy.text_to_speech_backend.Repository.RefreshTokenRepository;
import com.anudeepreddy.text_to_speech_backend.Repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private JWTService jwtService;

    @Autowired
    public UserService(UserRepository userRepository, AuthenticationManager authenticationManager,JWTService jwtService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService=jwtService;
    }

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

   @Autowired
   private PasswordEncoder passwordEncoder;

    public String register(@Valid UsersModel usersModel) {

        if(userRepository.existsByUsername(usersModel.getUsername())){
            throw new RuntimeException("Username already exists");
        }
        usersModel.setPassword(passwordEncoder.encode(usersModel.getPassword()));
        userRepository.save(usersModel);
        return "User registered successfully...";
    }

    public AuthResponseDTO login(UserLoginDTO userLoginDTO) {

        Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(),userLoginDTO.getPassword()));
        if(authentication.isAuthenticated()){
            String authToken=jwtService.generateToken(userLoginDTO.getUsername());
            RefreshToken refreshToken=refreshTokenService.createRefreshTokenService(userLoginDTO.getUsername());
            return new AuthResponseDTO(
                    authToken,
                    refreshToken.getToken()
            );
        }
        else{
            throw new RuntimeException("User not found");
        }
    }

    public AuthTokenDTO refresh(String requestDTO) {
        RefreshToken refreshToken=refreshTokenRepository.findByToken(requestDTO).orElseThrow(()-> new RuntimeException("Cannot generate new token retry!!!!"));
        refreshTokenService.verifyExpiration(refreshToken);
        UsersModel user=refreshToken.getUsersModel();
        String authToken=jwtService.generateToken(user.getUsername());
        return new AuthTokenDTO(authToken);
    }

    public String logout(String logoutDTO) {
        RefreshToken refreshToken=refreshTokenRepository.findByToken(logoutDTO).orElseThrow(()->new RuntimeException("Unable to logout!!!!"));
        refreshTokenRepository.delete(refreshToken);
        return "Logout Successfully....";
    }

}
