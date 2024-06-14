package com.english.eva.entity;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum MeaningSource {
  ENGLISH_PROFILE("English Profile"),
  CAMBRIDGE_DICTIONARY("Cambridge dictionary"),
  GOOGLE("Google");

  private final String label;

  MeaningSource(String label) {
    this.label = label;
  }

  public static MeaningSource findByLabel(String label) {
    return Arrays.stream(values())
        .filter(current -> current.getLabel().equalsIgnoreCase(label))
        .findFirst()
        .orElse(null);
  }
}
