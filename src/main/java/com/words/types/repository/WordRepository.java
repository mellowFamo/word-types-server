package com.words.types.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.words.types.entity.Word;

public interface WordRepository extends JpaRepository<Word, Long> {
}
