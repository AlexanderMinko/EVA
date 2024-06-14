package com.english.eva.entity;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum PartOfSpeech {
  ADJECTIVE("Adjective"),
  ADVERB("Adverb"),
  AUXILIARY_VERB("Auxiliary verb"),
  CONJUNCTION("Conjunction"),
  DETERMINER("Determiner"),
  EXCLAMATION("Exclamation"),
  MODAL_VERB("Modal verb"),
  NOUN("Noun"),
  PHRASAL_VERB("Phrasal verb"),
  PHRASE("Phrase"),
  PREPOSITION("Preposition"),
  PRONOUN("Pronoun"),
  VERB("Verb");

  private final String label;

  PartOfSpeech(String label) {
    this.label = label;
  }

  public static PartOfSpeech findByLabel(String label) {
    return Arrays.stream(values())
        .filter(current -> current.getLabel().equalsIgnoreCase(label))
        .findFirst()
        .orElse(null);
  }
}
