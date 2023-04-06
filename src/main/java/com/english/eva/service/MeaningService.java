package com.english.eva.service;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.english.eva.entity.LearningStatus;
import com.english.eva.entity.Meaning;
import com.english.eva.entity.MeaningSource;
import com.english.eva.entity.PartOfSpeech;
import com.english.eva.entity.ProficiencyLevel;
import com.english.eva.entity.Word;
import com.english.eva.repository.MeaningRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MeaningService {

  private final MeaningRepository meaningRepository;

  public void save(Meaning meaning) {
    var saved = meaningRepository.save(meaning);
    log.info("Meaning successfully has been saved. Id=[{}], source=[{}], part of speech=[{}], proficiency level=[{}]," +
            " description=[{}], examples=[{}]",
        saved.getId(), saved.getMeaningSource().getLabel(), saved.getPartOfSpeech().getLabel(),
        saved.getProficiencyLevel(), saved.getDescription(), saved.getExamples());
    saveReserve();
  }

  public void updateLearningStatus(Long id, LearningStatus learningStatus) {
    meaningRepository.updateLearningStatus(id, learningStatus);
    log.info("Learning status has been successfully updated for meaning with id [{}]. New learning status is [{}]",
        id, learningStatus);
    saveReserve();
  }

  public Meaning getMeaning(Long id) {
    var meaning = meaningRepository.findById(id).orElseThrow();
    log.info("Found meaning. ID=[{}], examples={}", meaning.getId(), meaning.getExamples());
    return meaning;
  }

  public void saveReserve() {
    var mapper = new ObjectMapper();
    var data = meaningRepository.findAll().stream().map(MeaningDto::new).toList();
    var path = Paths.get(System.getProperty("user.home") + "/English/meanings.json");
    try {
      Files.write(path, mapper.writeValueAsBytes(data));
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new RuntimeException(e);
    }
    log.info("Meanings have been saved to JSON. Path: [{}]", path);
  }

  @Data
  class MeaningDto implements Serializable {
    private Long id;
    private String target;
    private PartOfSpeech partOfSpeech;
    private ProficiencyLevel proficiencyLevel;
    private MeaningSource meaningSource;
    private LearningStatus learningStatus;
    private String description;
    private List<String> examples;
    private String also;
    private Date dateCreated;
    private Date lastModified;
    private Long wordId;

    public MeaningDto(Meaning meaning) {
      this.id = meaning.getId();
      this.target = meaning.getTarget();
      this.partOfSpeech = meaning.getPartOfSpeech();
      this.proficiencyLevel = meaning.getProficiencyLevel();
      this.meaningSource = meaning.getMeaningSource();
      this.learningStatus = meaning.getLearningStatus();
      this.description = meaning.getDescription();
      this.examples = meaning.getExamples();
      this.also = meaning.getAlso();
      this.dateCreated = meaning.getDateCreated();
      this.lastModified = meaning.getLastModified();
      this.wordId = meaning.getWord().getId();
    }
  }
}
