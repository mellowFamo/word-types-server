package com.words.types.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.words.types.dto.SentenceRequestDto;
import com.words.types.entity.Word;
import com.words.types.enums.WordType;
import com.words.types.repository.WordRepository;

@Service
public class SentenceService {

  private static final Set<String> DETERMINERS = Set.of("a", "an", "the", "this", "that", "these", "those", "my", "your", "his", "her", "its", "our", "their", "some", "any", "each", "every", "either", "neither");
  private static final Set<String> PRONOUNS = Set.of("i", "you", "he", "she", "it", "we", "they", "me", "him", "her", "us", "them", "myself", "yourself", "himself", "herself", "itself", "ourselves", "themselves", "who", "whom", "whose", "which");
  private static final Set<String> PREPOSITIONS = Set.of("in", "on", "at", "by", "for", "with", "from", "to", "into", "onto", "of", "about", "over", "under", "between", "through", "across", "after", "before", "without", "within");
  private static final Set<String> CONJUNCTIONS = Set.of("and", "but", "or", "nor", "so", "yet", "because", "although", "while", "if", "unless", "since", "whereas");
  private static final Set<String> INTERJECTIONS = Set.of("wow", "oops", "hey", "oh", "ah", "ouch", "hurray", "bravo", "alas", "yo");

  private final WordRepository wordRepository;
  private final OpenRouterWordClassifier openRouterWordClassifier;

  public SentenceService(WordRepository wordRepository, OpenRouterWordClassifier openRouterWordClassifier) {
    this.wordRepository = wordRepository;
    this.openRouterWordClassifier = openRouterWordClassifier;
  }

  @Transactional
  public void processSentence(SentenceRequestDto request) {
    String sentence = request == null ? null : request.getSentence();
    if (sentence == null || sentence.isBlank()) {
      return;
    }

    List<Word> wordsToSave = new ArrayList<>();
    List<String> normalizedWords = new ArrayList<>();
    String[] tokens = sentence.split("[^A-Za-z]+");

    for (String token : tokens) {
      if (token == null || token.isBlank()) {
        continue;
      }

      String normalizedWord = token.toLowerCase(Locale.ROOT);
      normalizedWords.add(normalizedWord);
    }

    Optional<List<WordType>> aiTypes = openRouterWordClassifier.classifySentence(sentence, normalizedWords);

    List<WordType> aiTypeList = aiTypes.orElse(null);
    for (int i = 0; i < normalizedWords.size(); i++) {
      String normalizedWord = normalizedWords.get(i);
      WordType type = aiTypeList != null ? aiTypeList.get(i) : classifyWord(normalizedWord);
      wordsToSave.add(new Word(normalizedWord, type));
    }

    if (!wordsToSave.isEmpty()) {
      wordRepository.saveAll(wordsToSave);
    }
  }

  private WordType classifyWord(String word) {
    if (DETERMINERS.contains(word)) {
      return WordType.DETERMINER;
    }
    if (PRONOUNS.contains(word)) {
      return WordType.PRONOUN;
    }
    if (PREPOSITIONS.contains(word)) {
      return WordType.PREPOSITION;
    }
    if (CONJUNCTIONS.contains(word)) {
      return WordType.CONJUNCTION;
    }
    if (INTERJECTIONS.contains(word)) {
      return WordType.INTERJECTION;
    }

    if (word.endsWith("ly")) {
      return WordType.ADVERB;
    }
    if (word.endsWith("ing") || word.endsWith("ed") || word.endsWith("ize") || word.endsWith("ise")) {
      return WordType.VERB;
    }
    if (word.endsWith("ous") || word.endsWith("ful") || word.endsWith("less") || word.endsWith("able") || word.endsWith("ible") || word.endsWith("ive") || word.endsWith("al") || word.endsWith("ic")) {
      return WordType.ADJECTIVE;
    }

    return WordType.NOUN;
  }
}
