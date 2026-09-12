package com.anudeepreddy.text_to_speech_backend.Integration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Component
public class TranslationIntegration {

    @Value("${gemini.api.key}")
    private String geminiApiKey;
    @Value("${gemini.api.url}")
    private String geminiInteractionUrl;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public TranslationIntegration(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public String translate(String text, String language){
        String prompt = """
                Translate the following text into %s.

                Rules:
                - Return only the translated text.
                - Do not explain anything.
                - Do not add quotation marks.
                - Preserve the original meaning.
                - Do not summarize the text.

                Text:
                %s
                """.formatted(language, text);

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

        System.out.println("========== TRANSLATION RESPONSE ==========");
        System.out.println(response);
        System.out.println("==========================================");



        JsonNode root = objectMapper.readTree(response);

        JsonNode steps = root.path("steps");

        if (steps.isArray()) {

            for (JsonNode step : steps) {

                JsonNode content = step.path("content");

                if (content.isArray()) {

                    for (JsonNode item : content) {

                        if ("text".equals(item.path("type").asText())) {

                            String translatedText =
                                    item.path("text").asText();

                            if (!translatedText.isBlank()) {
                                return translatedText.trim();
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
}
