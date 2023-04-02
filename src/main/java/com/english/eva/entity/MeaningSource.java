package com.english.eva.entity;

import java.lang.reflect.Array;
import java.util.Arrays;

public enum MeaningSource {
  ENGLISH_PROFILE("English Profile"),
  CAMBRIDGE_DICTIONARY("Cambridge dictionary"),
  GOOGLE("Google");

  private final String label;

  MeaningSource(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public static MeaningSource findByLabel(String label) {
    return Arrays.stream(values()).filter(current -> current.getLabel().equals(label)).findFirst()
        .orElse(null);
  }
}
