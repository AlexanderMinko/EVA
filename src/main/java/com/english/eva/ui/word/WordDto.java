package com.english.eva.ui.word;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.english.eva.entity.LearningStatus;
import com.english.eva.entity.Meaning;
import com.english.eva.entity.PartOfSpeech;
import com.english.eva.entity.Word;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class WordDto {
  private Long id;
  private Integer index;
  private String text;
  private String transcript;
  private String frequency;
  private String progress;
  private String levels;
  private String partsOfSpeech;
  private List<Long> meaningIds;

  public WordDto(Word word) {
    try {
      this.id = word.getId();
      this.text = word.getText();
      this.transcript = "[ " + word.getTranscript() + " ]";
      this.frequency = String.valueOf(word.getFrequency());
      this.progress = retrieveProgress(word);
      this.levels = retrieveLevels(word);
      this.partsOfSpeech = retrievePartsOfSpeech(word);
      this.meaningIds = retrieveMeaningIds(word);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      log.error("Word =>" + word.getMeanings());
//      throw new RuntimeException(e);
    }
  }

  private String retrieveProgress(Word word) {
    var knownCount = word.getMeanings().stream()
        .filter(meaning -> meaning.getLearningStatus() == LearningStatus.KNOWN ||
            meaning.getLearningStatus() == LearningStatus.LEARNT).count();
    return String.valueOf((int) (((double) knownCount / word.getMeanings().size()) * 100));
  }

  private String retrieveLevels(Word word) {
    return word.getMeanings().stream()
        .map(Meaning::getProficiencyLevel)
        .map(Enum::name)
        .distinct()
        .sorted(Comparator.naturalOrder())
        .collect(Collectors.joining(StringUtils.SPACE));
  }

  private String retrievePartsOfSpeech(Word word) {
    return word.getMeanings().stream()
        .map(Meaning::getPartOfSpeech)
        .map(PartOfSpeech::getLabel)
        .distinct().collect(Collectors.joining(", "));
  }

  private List<Long> retrieveMeaningIds(Word word) {
    return word.getMeanings().stream().map(Meaning::getId).toList();
  }
}
