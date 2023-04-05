package com.english.eva.service;

import java.util.List;

import com.english.eva.entity.LearningStatus;
import com.english.eva.entity.Meaning;
import com.english.eva.entity.MeaningSource;
import com.english.eva.repository.MeaningRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  }

  public void updateLearningStatus(Long id, LearningStatus learningStatus) {
    meaningRepository.updateLearningStatus(id, learningStatus);
    log.info("Learning status has been successfully updated for meaning with id [{}]. New learning status is [{}]",
        id, learningStatus);
  }

  public Meaning getMeaning(Long id) {
    var meaning = meaningRepository.findById(id).orElseThrow();
    log.info("Found meaning. ID=[{}], examples={}", meaning.getId(), meaning.getExamples());
    return meaning;
  }
}
