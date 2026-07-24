package com.words.types.entity;

import com.words.types.enums.WordType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "words")
public class Word {

  public Word() {
  }

  public Word(String word, WordType type) {
    this.word = word;
    this.type = type.toString();
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 50)
  private String word;

  @Column(length = 12)
  private String type;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getWord() {
    return word;
  }

  public void setWord(String word) {
    this.word = word;
  }

  public WordType getType() {
    return WordType.getByName(type);
  }

  public String getTypeAsString() {
    return type;
  }

  public void setType(WordType type) {
    this.type = type.toString();
  }
}
