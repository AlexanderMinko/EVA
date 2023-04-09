package com.english.eva.ui.panel.word;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.english.eva.entity.LearningStatus;
import com.english.eva.entity.Meaning;
import com.english.eva.entity.PartOfSpeech;
import com.english.eva.entity.Word;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    this.id = word.getId();
    this.text = word.getText();
    this.transcript = "[ " + word.getTranscript() + " ]";
    this.frequency = String.valueOf(word.getFrequency());
    this.progress = retrieveProgress(word);
    this.levels = retrieveLevels(word);
    this.partsOfSpeech = retrievePartsOfSpeech(word);
    this.meaningIds = retrieveMeaningIds(word);
  }

  private String retrieveProgress(Word word) {
    var knownCount = word.getMeaning().stream()
        .filter(meaning -> meaning.getLearningStatus() == LearningStatus.KNOWN ||
            meaning.getLearningStatus() == LearningStatus.LEARNT).count();
    return String.valueOf((int) (((double) knownCount / word.getMeaning().size()) * 100));
  }

  private String retrieveLevels(Word word) {
    return word.getMeaning().stream()
        .map(Meaning::getProficiencyLevel)
        .map(Enum::name)
        .distinct().sorted(Comparator.naturalOrder()).collect(Collectors.joining(StringUtils.SPACE));
  }

  private String retrievePartsOfSpeech(Word word) {
    return word.getMeaning().stream()
        .map(Meaning::getPartOfSpeech)
        .map(PartOfSpeech::getLabel)
        .distinct().collect(Collectors.joining(", "));
  }

  private List<Long> retrieveMeaningIds(Word word) {
    return word.getMeaning().stream().map(Meaning::getId).toList();
  }
}
