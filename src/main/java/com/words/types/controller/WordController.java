package com.words.types.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.words.types.entity.Word;
import com.words.types.repository.WordRepository;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("${app.api-prefix}/words")
public class WordController {

  private static final Logger log = LoggerFactory.getLogger(WordController.class);

  private final WordRepository wordRepository;

  public WordController(WordRepository wordRepository) {
    this.wordRepository = wordRepository;
  }

  @GetMapping
  public List<Word> getAllWords() {
    log.info("GET /words called");
    return wordRepository.findAll(Sort.by(Sort.Direction.ASC, Word::getId));
  }

  @PutMapping("/{id}")
  public Word updateWord(@PathVariable Long id, @RequestBody Word updatedWord) {
      log.info("PUT /words/{} called", id);
      Word existingWord = wordRepository.findById(id)
              .orElseThrow(() -> new RuntimeException("Word not found with id: " + id));
      existingWord.setWord(updatedWord.getWord());
      existingWord.setType(updatedWord.getType());
      return wordRepository.save(existingWord);
  }

  @DeleteMapping("/{id}")
  public void deleteWord(@PathVariable Long id) {
    log.info("DELETE /words/{} called", id);
    wordRepository.deleteById(id);
  }
}
