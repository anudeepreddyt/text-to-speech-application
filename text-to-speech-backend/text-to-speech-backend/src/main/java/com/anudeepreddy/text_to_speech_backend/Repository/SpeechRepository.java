package com.anudeepreddy.text_to_speech_backend.Repository;

import com.anudeepreddy.text_to_speech_backend.Model.SpeechHistory;
import com.anudeepreddy.text_to_speech_backend.Model.UsersModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeechRepository extends JpaRepository<SpeechHistory,Integer> {
    List<SpeechHistory> findByUsersModel(UsersModel usersModel);
}
