package com.words.types.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.words.types.config.WordTypeProperties;
import com.words.types.dto.WordTypeDto;
import com.words.types.enums.WordType;

@Service
public class WordTypeService {

  private final WordTypeProperties properties;

  public WordTypeService(WordTypeProperties properties) {
    this.properties = properties;
  }

  public List<WordTypeDto> getAll() {
    return Arrays.stream(WordType.values())
        .map(type -> new WordTypeDto(
          type.toString(), properties.getColours().get(type)))
        .toList();
  }
}