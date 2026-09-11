package com.anudeepreddy.text_to_speech_backend.Repository;

import com.anudeepreddy.text_to_speech_backend.Model.SpeechHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpeechRepository extends JpaRepository<SpeechHistory,Integer> {
}
