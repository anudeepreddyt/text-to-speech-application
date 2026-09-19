package com.anudeepreddy.text_to_speech_backend.Service;

import com.anudeepreddy.text_to_speech_backend.Model.RefreshToken;
import com.anudeepreddy.text_to_speech_backend.Model.UsersModel;
import com.anudeepreddy.text_to_speech_backend.Repository.RefreshTokenRepository;
import com.anudeepreddy.text_to_speech_backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    private final long refresh_token_duration=24*60*60*1000;

    public RefreshToken createRefreshTokenService(String username){
        UsersModel user=userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("User not found!!!"));

        RefreshToken refreshToken=refreshTokenRepository.findByUsersModel(user).orElse(new RefreshToken());

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refresh_token_duration));

        refreshToken.setUsersModel(user);

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken refreshToken){
        if(refreshToken.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Token Expired.Please login again!!!");
        }
        return refreshToken;
    }

    public RefreshToken findToken(String token){
        return refreshTokenRepository.findByToken(token).orElseThrow(()->new RuntimeException("No token found!!!"));
    }
}
