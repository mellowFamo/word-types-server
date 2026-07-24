package com.words.types.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.words.types.config.OpenRouterProperties;
import com.words.types.enums.WordType;

@Service
public class OpenRouterWordClassifier {

  private static final Logger log = LoggerFactory.getLogger(OpenRouterWordClassifier.class);

  private static final String SYSTEM_PROMPT =
      "You are an English grammar classifier. Return only valid JSON with no markdown. "
          + "Classify each word into exactly one of: NOUN, VERB, ADJECTIVE, ADVERB, PRONOUN, PREPOSITION, INTERJECTION, CONJUNCTION, DETERMINER.";

  private final RestClient restClient;
  private final ObjectMapper objectMapper;
  private final OpenRouterProperties properties;

  public OpenRouterWordClassifier(ObjectMapper objectMapper, OpenRouterProperties properties) {
    this.objectMapper = objectMapper;
    this.properties = properties;
    this.restClient = RestClient.builder().baseUrl(properties.getBaseUrl()).build();
  }

  public Optional<List<WordType>> classifySentence(String sentence, List<String> normalizedWords) {
    if (!properties.isEnabled() || !StringUtils.hasText(properties.getApiKey()) || normalizedWords.isEmpty()) {
      return Optional.empty();
    }

    log.info("Classifying sentence with OpenRouter: {}", sentence);
    String userPrompt = buildUserPrompt(sentence, normalizedWords);

    try {
      ChatCompletionRequest request = new ChatCompletionRequest(
          properties.getModel(),
          List.of(
              new ChatMessage("system", SYSTEM_PROMPT),
              new ChatMessage("user", userPrompt)),
          0);

      RestClient.RequestBodySpec bodySpec = restClient.post()
          .uri("/chat/completions")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON);

      if (StringUtils.hasText(properties.getSiteUrl())) {
        bodySpec.header("HTTP-Referer", properties.getSiteUrl());
      }
      if (StringUtils.hasText(properties.getAppName())) {
        bodySpec.header("X-Title", properties.getAppName());
      }

      ChatCompletionResponse response = bodySpec
          .body(request)
          .retrieve()
          .body(ChatCompletionResponse.class);

      if (response == null || response.choices() == null || response.choices().isEmpty()) {
        return Optional.empty();
      }

      ChatMessage message = response.choices().getFirst().message();
      if (message == null || !StringUtils.hasText(message.content())) {
        return Optional.empty();
      }

      List<WordType> parsedTypes = parseTypesFromContent(message.content(), normalizedWords.size());
      if (parsedTypes == null || parsedTypes.size() != normalizedWords.size()) {
        return Optional.empty();
      }

      return Optional.of(parsedTypes);
    } catch (Exception ex) {
      log.warn("OpenRouter classification failed, using fallback rules. reason={}", ex.getMessage());
      return Optional.empty();
    }
  }

  private String buildUserPrompt(String sentence, List<String> normalizedWords) {
    return "Original sentence: " + sentence + "\n"
        + "Words in order (classify in this same order): " + normalizedWords + "\n"
        + "Return JSON only in this exact shape: "
        + "{\"types\":[\"NOUN\",\"VERB\",...]} and keep array length exactly " + normalizedWords.size() + ".";
  }

  private List<WordType> parseTypesFromContent(String content, int expectedCount) throws Exception {
    JsonNode root = objectMapper.readTree(content);
    JsonNode typesNode = root.path("types");
    if (!typesNode.isArray() || typesNode.size() != expectedCount) {
      return null;
    }

    List<WordType> types = new java.util.ArrayList<>(expectedCount);
    for (JsonNode typeNode : typesNode) {
      String raw = typeNode.asText(null);
      if (!StringUtils.hasText(raw)) {
        return null;
      }
      types.add(WordType.valueOf(raw.trim().toUpperCase()));
    }
    return types;
  }

  private record ChatCompletionRequest(String model, List<ChatMessage> messages, int temperature) {
  }

  private record ChatCompletionResponse(List<ChatChoice> choices) {
  }

  private record ChatChoice(ChatMessage message) {
  }

  private record ChatMessage(String role, String content) {
  }
}