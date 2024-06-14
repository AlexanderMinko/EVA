package com.english.eva.ui.word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.english.eva.entity.Example;
import com.english.eva.entity.LearningStatus;
import com.english.eva.entity.Meaning;
import com.english.eva.entity.MeaningSource;
import com.english.eva.entity.PartOfSpeech;
import com.english.eva.entity.ProficiencyLevel;
import com.english.eva.service.MeaningService;
import com.english.eva.service.WordService;
import com.english.eva.ui.meaning.MeaningTree;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;

public class ExperimentalHandler {

  private MeaningTree meaningTree;

  public ExperimentalHandler(MeaningTree meaningTree) {
    this.meaningTree = meaningTree;
  }

  private static MeaningService meaningService;
  private static WordService wordService;

  public static void setWordService(WordService wordService) {
    ExperimentalHandler.wordService = wordService;
  }

  public static void setMeaningService(MeaningService meaningService) {
    ExperimentalHandler.meaningService = meaningService;
  }

  private static Integer INIT_Y_CELL = -1;

  public void handleAddMeaningItem(Long selectedWordId) {
    var newMeaningPanel = new JPanel();
    newMeaningPanel.setLayout(new MigLayout());

    var sourceStrings = Arrays.stream(MeaningSource.values()).map(MeaningSource::getLabel).toArray(String[]::new);
    var partOfSpeechStrings = Arrays.stream(PartOfSpeech.values()).map(PartOfSpeech::getLabel).toArray(String[]::new);
    var learningStatusStrings = Arrays.stream(LearningStatus.values()).map(LearningStatus::getLabel)
        .toArray(String[]::new);

    var partOfSpeechField = addComboBoxFieldEnums(newMeaningPanel, partOfSpeechStrings, "Part of speech");
    var learningStatus = addComboBoxFieldEnums(newMeaningPanel, learningStatusStrings, "Learning status");
    var text = addTextAreaField(newMeaningPanel, "Text");

    var word = wordService.getById(selectedWordId);
    if (Objects.isNull(word)) {
      return;
    }

    var option = JOptionPane.showOptionDialog(
        null,
        newMeaningPanel,
        "Add new meaning for: " + word.getText(),
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.PLAIN_MESSAGE,
        null,
        new String[] {"Add", "Cancel"},
        null);

    if (option == JOptionPane.CANCEL_OPTION) {
      return;
    }

    if (option == JOptionPane.OK_OPTION) {
      var content = text.getText();
      var list = Arrays.stream(content.split("\\n")).filter(StringUtils::isNotBlank).toList();
      var meaningStringsArray = new ArrayList<List<String>>();
      var meaningStrings = new ArrayList<String>();
      for (String str : list) {
        meaningStrings.add(str);
        if ((str.contains(".") || str.contains("!") || str.contains("?")) && str.endsWith(")")) {
          meaningStringsArray.add(meaningStrings);
          meaningStrings = new ArrayList<>();
        }
      }
      var meanings = new ArrayList<Meaning>();
      for (List<String> meaningStringsList : meaningStringsArray) {
        var proficiencyLevel = meaningStringsList.get(1).substring(0, 2);
        var description = meaningStringsList.get(1).substring(2).strip();
//        var simpleExamples = meaningStringsList.stream().filter(s -> s.contains("Dictionary examples:")).findFirst().orElse(StringUtils.EMPTY);
        var simpleExamples = new ArrayList<Example>();
        var startAdd = false;
        for (String s : meaningStringsList) {
          var example = new Example();
          if (s.contains("Learner example")) {
            startAdd = false;
          }
          if (startAdd) {
            example.setText(s);
            simpleExamples.add(example);
          }
          if (s.contains("Dictionary example")) {
            startAdd = true;
          }
        }

//        var examples = meaningStringsList.stream()
//            .filter(str -> str.endsWith("."))
//            .collect(Collectors.toList());
        var learnerExample = meaningStringsList.stream().filter(str -> str.endsWith(")") &&
            (str.contains(".") || str.contains("!") || str.contains("?"))).findFirst().orElse(StringUtils.EMPTY);
        var exampleLearner = new Example();
        exampleLearner.setText(learnerExample.substring(0, learnerExample.indexOf("(")).strip());
        simpleExamples.add(exampleLearner);
        var dateCreated = new Date();
        var meaning = Meaning.builder()
            .meaningSource(MeaningSource.ENGLISH_PROFILE)
            .target(meaningStringsList.get(0))
            .partOfSpeech(PartOfSpeech.findByLabel((String) partOfSpeechField.getSelectedItem()))
            .proficiencyLevel(ProficiencyLevel.valueOf(proficiencyLevel))
            .learningStatus(LearningStatus.findByLabel((String) learningStatus.getSelectedItem()))
            .description(description)
            .examples(simpleExamples)
            .word(word)
            .dateCreated(dateCreated)
            .lastModified(dateCreated)
            .build();
        simpleExamples.forEach(exampl -> exampl.setMeaning(meaning));
        meanings.add(meaning);
      }
      meaningService.saveBatch(meanings);
      meaningTree.setWord(wordService.getById(selectedWordId));
      meaningTree.showSelectedUserObjectTree();
    }
  }

  private static <T> JComboBox<T> addComboBoxFieldEnums(JPanel envPanel, T[] values, String labelText) {
    var enums = new JComboBox<>(values);
    var label = new JLabel(labelText);
    envPanel.add(label, String.format("cell 0 %s", INIT_Y_CELL += 1));
    envPanel.add(enums, String.format("cell 1 %s", INIT_Y_CELL));
    return enums;
  }

  private static JTextField addTextField(JPanel panel, String labelText, int columns) {
    var label = new JLabel(labelText);
    var field = new JTextField(columns);
    panel.add(label, String.format("cell 0 %s", INIT_Y_CELL += 1));
    panel.add(field, String.format("cell 1 %s", INIT_Y_CELL));
    return field;
  }

  private static JTextField addTextField(JPanel panel, String labelText) {
    return addTextField(panel, labelText, 40);
  }

  private static JTextArea addTextAreaField(JPanel panel, String labelText) {
    var label = new JLabel(labelText);
    var field = new JTextArea(30, 80);
    panel.add(label, String.format("cell 0 %s", INIT_Y_CELL += 1));
    panel.add(new JScrollPane(field), String.format("cell 1 %s", INIT_Y_CELL));
    return field;
  }
}
