package com.anudeepreddy.text_to_speech_backend.Repository;

import com.anudeepreddy.text_to_speech_backend.Model.UsersModel;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UsersModel,Integer> {
    boolean existsByUsername(String username);

    Optional<UsersModel> findByUsername(String username);
}
