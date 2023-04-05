package com.english.eva.entity;

import java.util.Arrays;

public enum LearningStatus {

  KNOWN("Known"),
  LEARNING("Learning"),
  PUT_OFF("Put off");

  private final String label;

  LearningStatus(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public static LearningStatus findByLabel(String label) {
    return Arrays.stream(values()).filter(current -> current.getLabel().equals(label)).findFirst()
        .orElse(null);
  }
}
