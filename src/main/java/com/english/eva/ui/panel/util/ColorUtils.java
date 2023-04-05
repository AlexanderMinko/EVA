package com.english.eva.ui.panel.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.english.eva.entity.LearningStatus;
import com.english.eva.entity.ProficiencyLevel;

public class ColorUtils {

  public final static Map<String, Color> LEVEL_COLOURS = new HashMap<>();
  public final static Map<String, Color> LEARNING_COLOURS = new HashMap<>();

  static {
    LEVEL_COLOURS.put(ProficiencyLevel.A1.name(), new Color(255, 128, 0));
    LEVEL_COLOURS.put(ProficiencyLevel.A2.name(), new Color(0, 160, 160));
    LEVEL_COLOURS.put(ProficiencyLevel.B1.name(), new Color(255, 0, 0));
    LEVEL_COLOURS.put(ProficiencyLevel.B2.name(), new Color(0, 128, 64));
    LEVEL_COLOURS.put(ProficiencyLevel.C1.name(), new Color(48, 96, 255));
    LEVEL_COLOURS.put(ProficiencyLevel.C2.name(), new Color(160, 48, 160));
    LEVEL_COLOURS.put(ProficiencyLevel.J7.name(), new Color(26, 27, 31));
  }

  static {
    LEARNING_COLOURS.put(LearningStatus.KNOWN.getLabel(), new Color(156, 255, 205));
    LEARNING_COLOURS.put(LearningStatus.LEARNING.getLabel(), new Color(255, 189, 128));
    LEARNING_COLOURS.put(LearningStatus.PUT_OFF.getLabel(), new Color(184, 148, 197));
  }

}
