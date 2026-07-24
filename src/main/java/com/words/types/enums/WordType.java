package com.words.types.enums;

public enum WordType {
  NOUN,
  VERB,
  ADJECTIVE,
  ADVERB,
  PRONOUN,
  PREPOSITION,
  INTERJECTION,
  CONJUNCTION,
  DETERMINER;

  public static WordType getByName(String name) {
    return WordType.valueOf(name.toUpperCase());
  }

  @Override
  public String toString() {
    String lower = name().toLowerCase();
    return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
  }
}
