package com.anudeepreddy.text_to_speech_backend.Repository;

import com.anudeepreddy.text_to_speech_backend.Model.RefreshToken;
import com.anudeepreddy.text_to_speech_backend.Model.UsersModel;
import io.jsonwebtoken.security.Jwks;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUsersModel(UsersModel user);
}
