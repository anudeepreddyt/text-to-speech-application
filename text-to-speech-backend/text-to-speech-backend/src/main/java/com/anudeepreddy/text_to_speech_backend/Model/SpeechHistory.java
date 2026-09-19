package com.anudeepreddy.text_to_speech_backend.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpeechHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String text;
    private String voice;
    @Lob
    private byte[] audioFormat;
    private LocalDateTime createdAt;
    private String language;

    @ManyToOne
    @JoinColumn(name = "userid",nullable = false)
    private UsersModel usersModel;
}
