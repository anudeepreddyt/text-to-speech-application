package com.anudeepreddy.text_to_speech_backend.Integration;

import com.anudeepreddy.text_to_speech_backend.DTO.GenerateReplyQueDTO;
import com.anudeepreddy.text_to_speech_backend.Model.SpeechHistory;
import com.anudeepreddy.text_to_speech_backend.Model.UsersModel;
import com.anudeepreddy.text_to_speech_backend.Repository.SpeechRepository;
import com.anudeepreddy.text_to_speech_backend.Repository.UserRepository;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Map;

@Component
public class GenerateReplyIntegration {

    private final UserRepository userRepository;
    private final SpeechRepository speechRepository;
    private WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.url}")
    private String geminiInteractionUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;


    public GenerateReplyIntegration(ObjectMapper objectMapper, WebClient.Builder webClientBuilder, UserRepository userRepository, SpeechRepository speechRepository) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.speechRepository = speechRepository;
    }

    public String generateReply(Integer id,String username) {

        SpeechHistory history=speechRepository.findById(id).orElseThrow(()-> new RuntimeException("Unable to process request"));
        String prompt= """
                
                You are a helpful conversational AI assistant.
                
                Your task is to generate a natural conversational reply to the user's message.
                
                Rules:
                - Understand the meaning and intent of the user's message.
                - Do NOT translate the user's message directly.
                - Generate a new response as if you are having a conversation with the user.
                - If the user asks a question, answer the question.
                - If the user introduces themselves, acknowledge their introduction naturally.
                - If the user makes a statement, respond appropriately to that statement.
                - Keep the response natural, friendly, and concise.
                - Do not repeat the user's message unless necessary.
                - Do not mention that you are an AI unless necessary.
                - Do not make up facts.
                - Return only the response that should be shown to the user.
                - The response MUST NOT exceed 255 characters.
                - If necessary, shorten the response to stay within 255 characters.
                - The response must be written in %s.
                
                User message:
                %s
                """.formatted(history.getText(),history.getLanguage());

        System.out.println(history.getText()+"   "+history.getLanguage());

        Map<String, Object> requestBody = Map.of(
                "model", "gemini-3.8-flash",
                "input", prompt
        );

        String response = webClient.post()
                .uri(geminiInteractionUrl)
                .header("x-goog-api-key", geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        clientResponse -> clientResponse
                                .bodyToMono(String.class)
                                .map(body -> new RuntimeException(
                                        "Gemini Translation API Error: " + body
                                ))
                )
                .bodyToMono(String.class)
                .block();

        JsonNode root = objectMapper.readTree(response);

        JsonNode steps = root.path("steps");

        if (steps.isArray()) {

            for (JsonNode step : steps) {

                JsonNode content = step.path("content");

                if (content.isArray()) {

                    for (JsonNode item : content) {

                        if ("text".equals(item.path("type").asText())) {

                            String replyText =
                                    item.path("text").asText();

                            if (!replyText.isBlank()) {
                                history.setAiReply(replyText);
                                speechRepository.save(history);
                                return replyText.trim();
                            }
                        }
                    }
                }
            }
        }

        throw new RuntimeException(
                "Translation text not found in Gemini response"
        );
    }

    public byte[] generateAiAudio(Integer id, String username) {
        SpeechHistory history=speechRepository.findById(id).orElseThrow(()->new RuntimeException("Unable to process request"));
        String text=history.getAiReply();
        Map<String, Object> requestBody = Map.of(
                "model", "gemini-3.1-flash-tts-preview",
                "input", text,
                "response_format", Map.of(
                        "type", "audio"
                ),
                "generation_config", Map.of(
                        "speech_config", new Object[]{
                                Map.of(
                                        "voice", history.getVoice(),
                                        "language",getLanguageCode(history.getLanguage())
                                )
                        }
                )
        );

        String response = webClient.post()
                .uri(geminiApiUrl)
                .header("x-goog-api-key", geminiApiKey)
                .header("Content-Type", "application/json")
                .header("Api-Revision", "2026-05-20")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        clientResponse -> clientResponse
                                .bodyToMono(String.class)
                                .map(body -> new RuntimeException(
                                        "Gemini API Error: " + body
                                ))
                )
                .bodyToMono(String.class)
                .block();

        JsonNode root = objectMapper.readTree(response);

        String base64Audio = "";

        JsonNode steps = root.path("steps");

        if (steps.isArray()) {

            for (JsonNode step : steps) {

                JsonNode content = step.path("content");

                if (content.isArray()) {

                    for (JsonNode item : content) {

                        if ("audio".equals(item.path("type").asText())) {

                            base64Audio = item.path("data").asText();

                            break;
                        }
                    }
                }

                if (!base64Audio.isBlank()) {
                    break;
                }
            }
        }

        if (base64Audio.isBlank()) {
            throw new RuntimeException("Audio data not found in Gemini response");
        }

        byte[] pcmAudio = Base64.getDecoder().decode(base64Audio);

        byte[] audio= addWavHeader(pcmAudio, 24000, 1, 16);
        history.setAiAudio(audio);
        speechRepository.save(history);
        return audio;

    }

    private byte[] addWavHeader(
            byte[] pcmData,
            int sampleRate,
            int channels,
            int bitsPerSample) {

        int byteRate = sampleRate * channels * bitsPerSample / 8;
        int blockAlign = channels * bitsPerSample / 8;

        int dataSize = pcmData.length;
        int fileSize = 36 + dataSize;

        byte[] wav = new byte[44 + dataSize];

        // RIFF
        wav[0] = 'R';
        wav[1] = 'I';
        wav[2] = 'F';
        wav[3] = 'F';

        writeIntLE(wav, 4, fileSize);

        // WAVE
        wav[8] = 'W';
        wav[9] = 'A';
        wav[10] = 'V';
        wav[11] = 'E';

        // fmt
        wav[12] = 'f';
        wav[13] = 'm';
        wav[14] = 't';
        wav[15] = ' ';

        writeIntLE(wav, 16, 16); // PCM header size

        writeShortLE(wav, 20, (short) 1); // PCM format

        writeShortLE(wav, 22, (short) channels);

        writeIntLE(wav, 24, sampleRate);

        writeIntLE(wav, 28, byteRate);

        writeShortLE(wav, 32, (short) blockAlign);

        writeShortLE(wav, 34, (short) bitsPerSample);

        // data
        wav[36] = 'd';
        wav[37] = 'a';
        wav[38] = 't';
        wav[39] = 'a';

        writeIntLE(wav, 40, dataSize);

        // PCM audio data
        System.arraycopy(
                pcmData,
                0,
                wav,
                44,
                pcmData.length
        );

        return wav;
    }
    private void writeIntLE(byte[] buffer, int offset, int value) {

        buffer[offset] = (byte) (value & 0xff);
        buffer[offset + 1] = (byte) ((value >> 8) & 0xff);
        buffer[offset + 2] = (byte) ((value >> 16) & 0xff);
        buffer[offset + 3] = (byte) ((value >> 24) & 0xff);
    }
    private void writeShortLE(byte[] buffer, int offset, short value) {

        buffer[offset] = (byte) (value & 0xff);
        buffer[offset + 1] = (byte) ((value >> 8) & 0xff);
    }



    public String getLanguageCode(String language){
        return switch (language.toLowerCase()){
            case "english" -> "en";
            case "hindi" -> "hi";
            case "telugu" -> "te";
            case "tamil" -> "ta";
            case "kannada" -> "kn";
            case "malayalam" -> "ml";
            case "marathi" -> "mr";
            case "gujarati" -> "gu";
            case "bengali" -> "bn";
            case "punjabi" -> "pa";
            case "spanish" -> "es";
            case "french" -> "fr";
            case "german" -> "de";
            case "japanese" -> "ja";
            case "korean" -> "ko";

            default -> throw new IllegalArgumentException(
                    "Unsupported language: " + language
            );
        };
    }
}
