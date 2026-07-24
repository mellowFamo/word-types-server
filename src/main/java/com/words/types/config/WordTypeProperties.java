package com.words.types.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.words.types.enums.WordType;

@ConfigurationProperties(prefix = "word-types")
public class WordTypeProperties {

  private Map<WordType, String> colours = new HashMap<>();

  public Map<WordType, String> getColours() {
      return colours;
  }

  public void setColours(Map<WordType, String> colours) {
      this.colours = colours;
  }
}
