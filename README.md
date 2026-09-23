
# 🎙️ Text-to-Speech Application

A full-stack AI-powered **Text-to-Speech Application** that converts written text and documents into natural-sounding speech using the **Google Gemini Text-to-Speech API**.

The application provides a user-friendly React interface, a secure Spring Boot backend, JWT-based authentication, document text extraction, and audio generation.

🔗 **GitHub Repository:**  
https://github.com/anudeepreddyt/text-to-speech-application

---

## 🚀 Live Demo

https://text-to-speech-application-frontend.onrender.com

> **Note:** The backend may take some time to start when hosted on a free-tier cloud service.

---

## 📌 Project Overview

The Text-to-Speech Application allows users to convert text into speech by selecting a language and voice.

Users can:

- Register and log in securely.
- Convert written text into speech.
- Upload PDF and DOCX documents.
- Extract text from uploaded documents.
- Generate speech from extracted document content.
- Select different voices and supported languages.
- Play generated audio in the browser.
- Access protected backend APIs using JWT authentication.

The backend communicates with the Google Gemini API to generate speech audio and returns the audio to the React frontend.

---

## ✨ Features

### 🔐 User Authentication

- User registration.
- User login.
- JWT-based authentication.
- Access token and refresh token support.
- Secure password hashing using BCrypt.
- Logout functionality.
- Protected API endpoints.
- Automatic access-token refresh from the frontend.

### 🗣️ Text-to-Speech Conversion

- Convert user-provided text into speech.
- Select the preferred language.
- Select the required voice.
- Generate audio using the Gemini Text-to-Speech API.
- Return generated audio in WAV format.
- Play generated audio directly in the browser.

### 📄 Document-to-Speech Conversion

- Upload PDF documents.
- Upload DOCX documents.
- Extract text from uploaded files.
- Convert extracted text into speech.
- Return the generated audio to the frontend.

### 🌐 Full-Stack Integration

- React frontend.
- Spring Boot REST APIs.
- PostgreSQL database.
- JWT authentication.
- CORS configuration.
- Gemini API integration.
- Frontend and backend deployment using Render.

---

## 🛠️ Technology Stack

### Frontend

- React
- Vite
- JavaScript
- HTML5
- CSS3
- Fetch API
- Browser Audio API

### Backend

- Java 25
- Spring Boot
- Spring Security
- Spring Data JPA
- Spring Web
- Spring WebFlux
- Bean Validation
- Lombok
- Maven

### Database

- PostgreSQL

### AI Integration

- Google Gemini API
- Gemini Text-to-Speech Model
- Gemini Text Generation Model

### Authentication and Security

- JWT
- Access Tokens
- Refresh Tokens
- BCrypt Password Encoder
- Spring Security
- CORS

### Deployment and Tools

- Docker
- Docker Compose
- Render
- Git
- GitHub
- Postman
- Swagger/OpenAPI

---

## 🏗️ Application Architecture

```text
                   ┌──────────────────────────┐
                   │       React Frontend     │
                   │      Vite + JavaScript   │
                   └────────────┬─────────────┘
                                │
                                │ HTTP REST API
                                │ JWT Bearer Token
                                ▼
                   ┌──────────────────────────┐
                   │     Spring Boot API      │
                   │       Backend             │
                   ├──────────────────────────┤
                   │ Controllers               │
                   │ Services                  │
                   │ JWT Authentication       │
                   │ Document Extraction      │
                   │ Gemini Integration       │
                   └───────┬──────────┬────────┘
                           │          │
              ┌────────────┘          └──────────────┐
              ▼                                      ▼
   ┌─────────────────────┐             ┌─────────────────────┐
   │    PostgreSQL       │             │    Google Gemini    │
   │      Database       │             │        API          │
   └─────────────────────┘             └─────────────────────┘
```

---

## 📂 Project Structure

### Backend

```text
text-to-speech-backend
│
├── src
│   └── main
│       ├── java
│       │   └── com.anudeepreddy.text_to_speech_backend
│       │       │
│       │       ├── Configuration
│       │       │   └── SecurityConfiguration.java
│       │       │
│       │       ├── Controller
│       │       │   ├── RegisterController.java
│       │       │   ├── LogoutController.java
│       │       │   ├── DocumentTextController.java
│       │       │   └── ...
│       │       │
│       │       ├── DTO
│       │       │
│       │       ├── Filter
│       │       │   └── JWTFilter.java
│       │       │
│       │       ├── Integration
│       │       │   └── DocExtractionIntegration.java
│       │       │
│       │       ├── Model
│       │       │
│       │       ├── Repository
│       │       │
│       │       └── Service
│       │
│       └── resources
│           └── application.properties
│
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── .dockerignore
```

### Frontend

```text
text-to-speech-frontend
│
├── src
│   ├── components
│   ├── pages
│   ├── services
│   │   └── api.js
│   ├── App.jsx
│   └── main.jsx
│
├── public
├── package.json
├── vite.config.js
└── .env
```

> The exact frontend folder names may vary depending on the current project structure.

---

## 🔌 Backend APIs

### Authentication APIs

| Method | Endpoint | Description | Authentication |
|---|---|---|---|
| POST | `/api/register` | Register a new user | Public |
| POST | `/api/login` | Authenticate a user | Public |
| POST | `/api/refreshToken` | Generate a new access token | Public |
| POST | `/api/logout` | Invalidate a refresh token | Public |

### Text-to-Speech APIs

| Method | Endpoint | Description | Authentication |
|---|---|---|---|
| GET | `/api/health` | Check backend status | Public |
| GET | `/api/voices` | Retrieve available voices | Protected |
| GET | `/api/languages` | Retrieve supported languages | Protected |
| POST | `/api/tts` | Convert text into speech | Protected |
| POST | `/api/document` | Convert a PDF or DOCX document into speech | Protected |

### Text-to-Speech Request

```json
{
  "text": "Hello, welcome to my application.",
  "voice": "Puck",
  "language": "English"
}
```

### Text-to-Speech Response

The API returns generated audio in WAV format.

```http
Content-Type: audio/wav
```

The generated speech ID is returned through the response header when supported:

```http
X-Speech-Id: 1
```

---

## 🔐 Authentication Flow

The application uses JWT-based authentication.

```text
1. User registers an account
             │
             ▼
2. User logs in using username and password
             │
             ▼
3. Backend validates credentials
             │
             ▼
4. Backend generates:
   - Access Token
   - Refresh Token
             │
             ▼
5. Frontend stores the tokens
             │
             ▼
6. Frontend sends the access token
   in the Authorization header
             │
             ▼
7. JWT Filter validates the token
             │
             ▼
8. User accesses protected APIs
```

### Authorization Header

```http
Authorization: Bearer <access_token>
```

The frontend attempts to refresh the access token when a protected request returns a `401 Unauthorized` response and a refresh token is available.

---

## 🤖 Gemini API Integration

The backend integrates with the Google Gemini API for speech generation.

### Text-to-Speech Model

```text
gemini-3.1-flash-tts-preview
```

### Text Generation Model

```text
gemini-3.8-flash
```

The backend sends a request to the Gemini Interactions API, processes the returned audio data, and converts the PCM audio into a WAV file.

### Audio Processing

The generated audio is handled using:

- PCM audio data.
- 24 kHz sample rate.
- Mono audio.
- 16-bit audio.
- WAV header generation.

> Model availability and API requirements may change. Refer to the official Google Gemini API documentation for current model and API details.

---

## 📄 Document Processing

The application supports text extraction from:

- PDF files.
- DOCX files.

### PDF Processing

PDF text is extracted using:

```text
Apache PDFBox
```

### DOCX Processing

DOCX text is extracted using:

```text
Apache POI
```

### Document Processing Flow

```text
User uploads a PDF or DOCX file
             │
             ▼
Backend receives the multipart file
             │
             ▼
File type is identified
             │
             ▼
Text is extracted from the document
             │
             ▼
Extracted text is sent to Gemini API
             │
             ▼
Speech audio is generated
             │
             ▼
WAV audio is returned to the frontend
```

---

## ⚙️ Local Installation and Setup

### Prerequisites

Install the following tools:

- Java 25
- Maven
- Node.js and npm
- PostgreSQL
- Git
- A Google Gemini API key

---

### 1. Clone the Repository

```bash
git clone https://github.com/anudeepreddyt/text-to-speech-application.git
```

```bash
cd text-to-speech-application
```

---

### 2. Configure PostgreSQL

Create a PostgreSQL database:

```sql
CREATE DATABASE tts_application;
```

Update the database configuration in the backend `application.properties` file.

Example:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tts_application
spring.datasource.username=postgres
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

### 3. Configure Gemini API

Add your Gemini API configuration using environment variables.

```properties
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/interactions
gemini.api.key=${GEMINI_API_KEY}
```

Set the environment variable:

### Windows PowerShell

```powershell
$env:GEMINI_API_KEY="your_actual_api_key"
```

### Linux/macOS

```bash
export GEMINI_API_KEY="your_actual_api_key"
```

> Never commit API keys, passwords, JWT secrets, or `.env` files to GitHub.

---

### 4. Run the Backend

Navigate to the backend directory:

```bash
cd text-to-speech-backend
```

Run the application using Maven:

```bash
mvn spring-boot:run
```

The backend will run on:

```text
http://localhost:8080
```

---

### 5. Configure the Frontend

Navigate to the frontend directory:

```bash
cd text-to-speech-frontend
```

Install dependencies:

```bash
npm install
```

Create a `.env` file:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

Start the frontend:

```bash
npm run dev
```

The frontend will be available at:

```text
http://localhost:5173
```

---

## 🐳 Running with Docker

The project can be containerized using Docker and Docker Compose.

### Build the Containers

```bash
docker compose build
```

### Start the Application

```bash
docker compose up -d
```

### Check Running Containers

```bash
docker ps
```

### View Backend Logs

```bash
docker compose logs -f backend
```

### Rebuild and Start

```bash
docker compose up -d --build
```

### Stop the Application

```bash
docker compose down
```

### Docker Services

| Service | Port |
|---|---|
| Backend | 8080 |
| PostgreSQL | 5432 |

Inside Docker Compose, the backend connects to PostgreSQL using the service name:

```text
postgres
```

It should not use `localhost` as the database hostname from inside the backend container.

---

## 🌍 Deployment

The application can be deployed using Render or another cloud hosting platform.

### Frontend Deployment

Configure the frontend environment variable:

```env
VITE_API_BASE_URL=https://text-to-speech-application-backend.onrender.com/api
```

### Backend Deployment

Configure the following environment variables on the hosting platform:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://<database-host>:5432/<database-name>
SPRING_DATASOURCE_USERNAME=<database-username>
SPRING_DATASOURCE_PASSWORD=<database-password>
GEMINI_API_URL=https://generativelanguage.googleapis.com/v1beta/interactions
GEMINI_API_KEY=<your-gemini-api-key>
```

Also configure:

- JWT secret.
- JWT expiration.
- CORS allowed frontend origin.
- PostgreSQL connection details.

---

## 🛡️ Security

The project includes the following security measures:

- BCrypt password hashing.
- JWT access-token authentication.
- Refresh-token support.
- Stateless Spring Security sessions.
- Protected REST endpoints.
- CORS configuration.
- Environment-based secret configuration.
- User authentication before accessing protected functionality.

### Security Recommendations

- Use a strong JWT secret.
- Store secrets as environment variables.
- Do not expose API keys in the frontend.
- Do not commit `.env` files.
- Validate uploaded files.
- Configure production CORS origins carefully.
- Use HTTPS in production.

---

## 🧪 Testing APIs

You can test the backend APIs using:

- Postman.
- Swagger UI.
- Browser developer tools.
- Frontend application.

Swagger UI, when enabled, can be accessed at:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI documentation:

```text
http://localhost:8080/v3/api-docs
```

---

## 🔮 Future Enhancements

Possible future improvements include:

- Speech history management.
- Download generated audio files.
- Audio playback controls.
- AI-generated replies for saved speech content.
- Support for additional document formats.
- More voice customization options.
- Audio file storage optimization.
- Rate limiting for API requests.
- Global exception handling.
- Automated unit and integration tests.
- CI/CD pipeline.
- Improved accessibility.
- Support for additional languages.

---

## 🎯 Learning Outcomes

This project helped me gain practical experience in:

- Java and Spring Boot backend development.
- REST API development.
- Spring Security and JWT authentication.
- Access-token and refresh-token management.
- React frontend development.
- Frontend-backend integration.
- PostgreSQL database integration.
- Third-party AI API integration.
- PDF and DOCX text extraction.
- Multipart file uploads.
- Audio data processing.
- CORS configuration.
- Docker containerization.
- Cloud deployment.
- Debugging authentication and deployment issues.

---

## 👨‍💻 Author

**Anudeep Reddy**

- GitHub: https://github.com/anudeepreddyt

---

## 📄 License

This project is intended for learning, experimentation, and portfolio development.

Add a specific open-source license if you intend to distribute the project under one.
