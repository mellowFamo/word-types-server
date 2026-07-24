# Word Types Server

Word Types Server is a Spring Boot REST API that classifies words in a sentence into English word types (noun, verb, adjective, etc.) and stores the results in PostgreSQL.

It supports two classification modes:

- Rule-based fallback classifier (always available).
- OpenRouter AI classifier (optional, enabled by configuration).

## Tech Stack

- Java 25
- Spring Boot 4.1.0
- Spring Web + Spring Data JPA
- PostgreSQL
- Maven

## Features

- Submit a sentence and persist each token with its word type.
- Fetch all stored words.
- Update or delete a stored word.
- Fetch all available word types with configured color values.
- Optional AI-based classification via OpenRouter with automatic fallback to rule-based classification on error.

## Project Structure

- `src/main/java/com/words/types/controller`: REST controllers
- `src/main/java/com/words/types/service`: business logic and classifier integration
- `src/main/java/com/words/types/entity`: JPA entities
- `src/main/java/com/words/types/repository`: data access
- `src/main/java/com/words/types/config`: configuration properties and CORS setup
- `src/main/resources/application.properties`: app configuration
- `src/main/resources/db/migration/V1__create_and_normalize_words_table.sql`: SQL schema init script

## Prerequisites

- JDK 25
- PostgreSQL (running and reachable)
- Maven (or use included Maven wrapper)

## Configuration

The app imports environment values from `.env` using:

- `spring.config.import=optional:file:.env[.properties]`

### 1) Create `.env`

Use the template:

```bash
cp .env.template .env
```

Then edit values as needed.

### 2) Required variables

```dotenv
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/your_database
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
```

### 3) Common optional variables

```dotenv
APP_API_PREFIX=/api/v1
APP_CORS_ALLOWED_ORIGINS=http://localhost:5173

APP_OPENROUTER_ENABLED=false
APP_OPENROUTER_API_KEY=
APP_OPENROUTER_MODEL=openrouter/auto
APP_OPENROUTER_BASE_URL=https://openrouter.ai/api/v1
APP_OPENROUTER_SITE_URL=
```

Notes:

- If OpenRouter is disabled or the API key is missing, the service uses local rule-based classification.
- CORS applies to all routes under `APP_API_PREFIX`.

## Database Behavior

On startup, Spring SQL init runs:

- `src/main/resources/db/migration/V1__create_and_normalize_words_table.sql`

This script:

- Creates `words` table if it does not exist.
- Ensures expected columns are present.
- Resets sequence position to max existing `id`.

JPA is configured with:

- `spring.jpa.hibernate.ddl-auto=validate`

So entity-to-schema mismatches fail fast instead of mutating schema.

## Running the Service

Using Maven wrapper:

```bash
./mvnw spring-boot:run
```

Build a jar:

```bash
./mvnw clean package
java -jar target/types_service-0.0.1-SNAPSHOT.jar
```

Default base URL:

- `http://localhost:8080`

Default API prefix:

- `/api/v1`

## API Reference

### 1) Submit sentence

- Method: `POST`
- Path: `/api/v1/sentence`
- Body:

```json
{
  "sentence": "The quick brown fox jumps quickly"
}
```

Response:

- `200 OK`
- Empty body

Behavior:

- Splits sentence using non-letter delimiters.
- Normalizes tokens to lowercase.
- Attempts AI classification if enabled.
- Falls back to local classifier if AI is unavailable/fails.
- Persists each token to `words` table.

Example:

```bash
curl -X POST http://localhost:8080/api/v1/sentence \
  -H "Content-Type: application/json" \
  -d '{"sentence":"The quick brown fox jumps quickly"}'
```

### 2) List words

- Method: `GET`
- Path: `/api/v1/words`

Response example:

```json
[
  {
    "id": 1,
    "word": "the",
    "type": "Determiner"
  },
  {
    "id": 2,
    "word": "fox",
    "type": "Noun"
  }
]
```

Example:

```bash
curl http://localhost:8080/api/v1/words
```

### 3) Update a word

- Method: `PUT`
- Path: `/api/v1/words/{id}`
- Body example:

```json
{
  "word": "quickly",
  "type": "Adverb"
}
```

Returns updated `Word` object.

Example:

```bash
curl -X PUT http://localhost:8080/api/v1/words/1 \
  -H "Content-Type: application/json" \
  -d '{"word":"quickly","type":"Adverb"}'
```

### 4) Delete a word

- Method: `DELETE`
- Path: `/api/v1/words/{id}`

Response:

- `200 OK`
- Empty body

Example:

```bash
curl -X DELETE http://localhost:8080/api/v1/words/1
```

### 5) List word types

- Method: `GET`
- Path: `/api/v1/word-types`

Response example:

```json
[
  { "name": "Noun", "colour": "#FF5733" },
  { "name": "Verb", "colour": "#008D00" }
]
```

Example:

```bash
curl http://localhost:8080/api/v1/word-types
```

## Word Types

Supported categories:

- Noun
- Verb
- Adjective
- Adverb
- Pronoun
- Preposition
- Interjection
- Conjunction
- Determiner

Color mappings are configured in `application.properties` under `word-types.colours.*`.

## Troubleshooting

- App fails at startup with DB errors:
  - Verify PostgreSQL is running.
  - Confirm datasource URL/user/password.
  - Ensure DB user has permissions for schema/table changes in the SQL init script.
- AI classification not being used:
  - Set `APP_OPENROUTER_ENABLED=true`.
  - Provide `APP_OPENROUTER_API_KEY`.
  - Check logs for OpenRouter call failures and fallback notices.
- CORS issues from frontend:
  - Set `APP_CORS_ALLOWED_ORIGINS` to the frontend origin (for example `http://localhost:5173`).
