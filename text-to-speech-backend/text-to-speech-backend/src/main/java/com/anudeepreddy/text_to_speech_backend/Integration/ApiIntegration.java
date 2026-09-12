package com.anudeepreddy.text_to_speech_backend.Integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Map;

@Component
public class ApiIntegration {

    private WebClient webClient;
    private final ObjectMapper objectMapper;
    private TranslationIntegration translationIntegration;


    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Autowired
    public ApiIntegration(ObjectMapper objectMapper, WebClient.Builder webClientBuilder,TranslationIntegration translationIntegration) {
        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder.build();
        this.translationIntegration=translationIntegration;

    }

    public byte[] generateSpeech(String text, String voice,String language) {

        String changed_text= translationIntegration.translate( text,  language);


        if (changed_text == null || changed_text.isBlank()) {
            throw new RuntimeException("Translation returned empty text");
        }

        Map<String, Object> requestBody = Map.of(
                "model", "gemini-3.1-flash-tts-preview",
                "input", changed_text,
                "response_format", Map.of(
                        "type", "audio"
                ),
                "generation_config", Map.of(
                        "speech_config", new Object[]{
                                Map.of(
                                        "voice", voice,
                                        "language",getLanguageCode(language)
                                )
                        }
                )
        );

        System.out.println("REQUEST TO GEMINI:");
        System.out.println(requestBody);

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

        return addWavHeader(pcmAudio, 24000, 1, 16);

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
            case "english" -> "en-IN";
            case "hindi" -> "hi-IN";
            case "gujarati" -> "gu-IN";
            case "marathi" -> "mr-IN";
            case "spanish" -> "es-ES";
            case "french" -> "fr-FR";
            case "german" -> "de-DE";
            case "telugu" -> "te-IN";

            default -> throw new IllegalArgumentException(
                    "Unsupported language: " + language
            );
        };
    }

}
