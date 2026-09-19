# Speak — text-to-speech frontend

A small React (Vite) frontend for your `text-to-speech-backend` Spring Boot
API. Plain CSS, no UI framework, one dependency (`lucide-react` for icons).

## Run it

```bash
npm install
npm run dev
```

Opens at `http://localhost:5173`. It expects the backend at
`http://localhost:8080/api` (Spring Boot's default port + your
`@RequestMapping("/api")`). To point it elsewhere, create a `.env` file:

```
VITE_API_BASE_URL=http://localhost:8080/api
```

## What's included

- Register / log in (JWT), with automatic silent refresh via `/refreshToken`
  when the access token expires, and logout
- **Speak**: type text (500-char limit, matching your `@Size` validation),
  pick a language and voice, generate and play/download the audio
- **From a document**: upload a PDF or `.docx`, pick language + voice, get
  audio back
- **History**: lists everything you've generated, with inline playback
- **AI reply** (Speak, From a document, and each History item): "Generate
  AI reply" calls `/generateReply/{id}`, then "Play as speech" calls
  `/generateAi/{id}/audio`. This only appears when an `id` is actually
  available — see the patch below, which is required for this to show up
  at all.

## One thing to fix first: CORS

I read through every controller/DTO/service in your zip to match field
names exactly, and confirmed there's no CORS configuration anywhere in the
project. The browser will block every request from `localhost:5173` to
`localhost:8080` until you add one. Put this next to your other config
classes, e.g. `Configuration/CorsConfig.java`:

```java
package com.anudeepreddy.text_to_speech_backend.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
```

## Required backend patch: the AI-reply feature needs an `id`

The frontend now has UI for the AI-reply endpoints:

- `GET /api/generateReply/{id}`
- `GET /api/generateAi/{id}/audio`

(`GET /api/AiHistory/{id}` and `GET /api/AiHistory/{id}/audio` just
re-read what's already saved — the frontend doesn't call these yet, since
the "generate" endpoints above already do the generate-and-save in one
step within a session.)

All of these need the numeric `id` of a `SpeechHistory` row, and right now
nothing in the API ever sends that `id` to a client:

- `POST /tts` and `POST /document` save a `SpeechHistory` row internally but
  only ever return the raw audio bytes.
- `GET /speechHistory` returns `SpeechHistoryDTO`, which only has `text` and
  `audioformat`.

**The frontend is written defensively around this**: it looks for an
`X-Speech-Id` response header on `/tts` and `/document`, and an `id` field
on each `/speechHistory` item. If either is missing, it just hides the "AI
reply" button for that clip — nothing breaks, the feature just won't
appear until you make these two changes:

1. In `TtsController.java` and `DocumentTextController.java`, have
   `TtsService.generateSpeech(...)` return the saved `SpeechHistory` (or at
   least its `id`) instead of only `byte[]`, and set it on the response:

   ```java
   return ResponseEntity.ok()
           .contentType(MediaType.parseMediaType("audio/wav"))
           .header("X-Speech-Id", String.valueOf(history.getId()))
           .body(audio);
   ```

2. Add `private Integer id;` to `SpeechHistoryDTO.java`, and pass
   `history.getId()` into the `new SpeechHistoryDTO(...)` call inside
   `TtsService.speechHistory()`.

Once either change is live, restart the backend and refresh the frontend —
the "AI reply" button will start appearing automatically, no frontend
change needed.

## A smaller note: default error messages

There's no `@ControllerAdvice`/exception handler in this project, so
failures (bad login, duplicate username, etc.) fall back to Spring Boot's
default error response, which hides the actual exception message unless you
set `server.error.include-message=always` in `application.properties`.
Without it, the frontend shows a generic fallback message instead of your
backend's specific one (e.g. "Username already exists"). Not a blocker —
just explains why error text can look generic until you add that line (or a
proper `@ControllerAdvice`, which would be the more robust fix).
