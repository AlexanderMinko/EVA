package com.english.eva.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.english.eva.entity.Word;
import com.english.eva.model.SearchParams;
import com.english.eva.repository.WordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class WordService {

  private final WordRepository wordRepository;

  public Word save(Word word) {
    var saved = wordRepository.save(word);
    log.info("Word successfully has been saved. Id=[{}], word=[{}], transcript=[{}], frequency=[{}], topic=[{}]",
        saved.getId(), saved.getText(), saved.getTranscript(), saved.getFrequency(), saved.getTopic());
    return saved;
  }

  public List<Word> getAll() {
    var words = wordRepository.findAll(Sort.by(Sort.Direction.DESC, "dateCreated"));
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

  public List<Word> getByWordIds(Set<Long> ids) {
    return wordRepository.findByIdIn(ids);
  }

  public void delete(Long id) {
    wordRepository.deleteById(id);
    log.info("Word with id [{}] successfully has been deleted", id);
  }

  public List<Word> search(SearchParams params) {
    var result = wordRepository.findBySearchParams(params);
    log.info("Founded [{}] search results", result.size());
    return result;
  }

  public boolean isWordExistsByText(String text) {
    return wordRepository.existsByText(text);
  }

  public void saveReserve() {
    var mapper = new ObjectMapper();
    var data = wordRepository.findAll();
    var path = Paths.get(System.getProperty("user.home") + "/English/words.json");
    try {
      Files.write(path, mapper.writeValueAsBytes(data));
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new RuntimeException(e);
    }
    log.info("Words have been saved to JSON. Path: [{}]", path);
  }

//  @Data
//  static class WordDto implements Serializable {
//    private Long id;
//    private String text;
//    private String transcript;
//    private Integer frequency;
//    private String topic;
//    private Date dateCreated;
//    private Date lastModified;
//    private List<Meaning> meanings;
//
//    public WordDto(Word word) {
//      this.id = word.getId();
//      this.text = word.getText();
//      this.transcript = word.getTranscript();
//      this.frequency = word.getFrequency();
//      this.topic = word.getTopic();
//      this.dateCreated = word.getDateCreated();
//      this.lastModified = word.getLastModified();
//      this.meanings = word.getMeaning();
//    }
//  }
}
