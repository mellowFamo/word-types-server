package com.words.types.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.words.types.dto.WordTypeDto;
import com.words.types.service.WordTypeService;

@RestController
@RequestMapping("${app.api-prefix}/word-types")
public class WordTypeController {

  private static final Logger log = LoggerFactory.getLogger(WordTypeController.class);

  private final WordTypeService service;

  public WordTypeController(WordTypeService service) {
    this.service = service;
  }

  @GetMapping
  public List<WordTypeDto> getAllTypes() {
    log.info("GET /word-types called");
    return service.getAll();
  }
}