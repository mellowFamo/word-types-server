package com.words.types.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.words.types.dto.SentenceRequestDto;
import com.words.types.service.SentenceService;

@RestController
@RequestMapping("${app.api-prefix}/sentence")
public class SentenceController {

  private static final Logger log = LoggerFactory.getLogger(SentenceController.class);
  private final SentenceService sentenceService;

  public SentenceController(SentenceService sentenceService) {
    this.sentenceService = sentenceService;
  }

  @PostMapping
  public void submitSentence(@RequestBody SentenceRequestDto request) {
    String sentence = request == null ? null : request.getSentence();
    log.info("POST /sentence called with sentence: {}", sentence);
    sentenceService.processSentence(request);
  }
}
