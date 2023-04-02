package com.english.eva.service;

import java.util.List;
import java.util.Objects;

import com.english.eva.entity.Word;
import com.english.eva.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class WordService {

  private final WordRepository wordRepository;

  public void save(Word word) {
    var saved = wordRepository.save(word);
    log.info("Word successfully has been saved. Id=[{}], word=[{}], transcript=[{}], frequency=[{}], topic=[{}]",
        saved.getId(), saved.getText(), saved.getTranscript(), saved.getFrequency(), saved.getTopic());
  }

  public List<Word> getAll() {
    var words = wordRepository.findAll();
    log.info("Founded {} words", words.size());
    return words;
  }

  public Word getById(Long id) {
    var word = wordRepository.findById(id).orElseGet(() -> {
      log.warn("Word not not found by id [{}]", id);
      return null;
    });
    if (Objects.nonNull(word)) {
      log.info("Founded word [{}] by id [{}]", word.getText(), id);
    }
    return word;
  }
}
