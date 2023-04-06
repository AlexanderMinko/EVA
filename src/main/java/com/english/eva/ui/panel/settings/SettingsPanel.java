package com.english.eva.ui.panel.settings;

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;
import static javax.swing.GroupLayout.Alignment.TRAILING;

import static com.english.eva.ui.panel.util.ColorUtils.LEARNING_COLOURS;
import static com.english.eva.ui.panel.util.ColorUtils.LEVEL_COLOURS;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;

import com.english.eva.entity.LearningStatus;
import com.english.eva.entity.ProficiencyLevel;
import com.english.eva.model.SearchParams;
import com.english.eva.service.WordService;
import com.english.eva.ui.panel.word.WordsTable;

public class SettingsPanel extends JPanel {

  private static WordService wordService;
  private WordsTable wordsTable;

  private JTextField wordSearchValueField;
  private JToggleButton toggleButtonA1;
  private JToggleButton toggleButtonA2;
  private JToggleButton toggleButtonB1;
  private JToggleButton toggleButtonB2;
  private JToggleButton toggleButtonC1;
  private JToggleButton toggleButtonC2;
  private JToggleButton toggleButtonJ7;
  private JToggleButton toggleButtonAllLevels;
  private List<JToggleButton> levelButtonsList;
  private JPanel levelsPanel;
  private JButton searchButton;

  private List<JToggleButton> learningStatusButtonList;

  public SettingsPanel() {
    //search
    setBorder(new TitledBorder("Settings"));
    var groupLayout = new GroupLayout(this);
    groupLayout.setAutoCreateGaps(true);
    groupLayout.setAutoCreateContainerGaps(true);
    setLayout(groupLayout);

    initWordSearchValueField();
    initSearchButton();
    initLevelsBar();

    var toggleButtonKnown = new JToggleButton(LearningStatus.KNOWN.getLabel());
    var toggleButtonLearnt = new JToggleButton(LearningStatus.LEARNT.getLabel());
    var toggleButtonLearning = new JToggleButton(LearningStatus.LEARNING.getLabel());
    var toggleButtonPutOff = new JToggleButton(LearningStatus.PUT_OFF.getLabel());
    var toggleButtonAllLearningStatuses = new JToggleButton("Select All");
    learningStatusButtonList = List.of(toggleButtonKnown, toggleButtonLearnt, toggleButtonLearning, toggleButtonPutOff);
    learningStatusButtonList.forEach(toggle -> {
      toggle.setBackground(LEARNING_COLOURS.get(toggle.getText()));
      var font = toggle.getFont();
      toggle.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
      toggle.addActionListener(event -> {
        toggle.setBackground(toggle.isSelected() ? new Color(0, 0, 0, 0) : LEARNING_COLOURS.get(toggle.getText()));
      });
    });
    toggleButtonAllLearningStatuses.addActionListener(event -> {
      learningStatusButtonList.forEach(levelButton -> {
        levelButton.setSelected(toggleButtonAllLearningStatuses.isSelected());
        if (levelButton.isSelected()) {
          levelButton.setBackground(new Color(0, 0, 0, 0));
        } else {
          levelButton.setBackground(LEARNING_COLOURS.get(levelButton.getText()));
        }
      });
    });
    var learningStatusPanel = new JPanel();
    learningStatusPanel.setLayout(new BoxLayout(learningStatusPanel, BoxLayout.X_AXIS));
    learningStatusButtonList.forEach(learningStatusPanel::add);
    learningStatusPanel.add(toggleButtonAllLearningStatuses);

    groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
        .addGroup(groupLayout.createParallelGroup(LEADING).addComponent(wordSearchValueField).addComponent(levelsPanel))
        .addGroup(
            groupLayout.createParallelGroup(LEADING).addComponent(searchButton).addComponent(learningStatusPanel)));

    groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
        .addGroup(
            groupLayout.createParallelGroup(BASELINE).addComponent(wordSearchValueField).addComponent(searchButton))
        .addGroup(
            groupLayout.createParallelGroup(BASELINE).addComponent(levelsPanel).addComponent(learningStatusPanel)));
  }

  private void initSearchButton() {
    searchButton = new JButton();
    searchButton.setText("Search");
    searchButton.addActionListener(event -> executeWordSearch());
  }

  private void initLevelsBar() {
    toggleButtonA1 = new JToggleButton(ProficiencyLevel.A1.name());
    toggleButtonA2 = new JToggleButton(ProficiencyLevel.A2.name());
    toggleButtonB1 = new JToggleButton(ProficiencyLevel.B1.name());
    toggleButtonB2 = new JToggleButton(ProficiencyLevel.B2.name());
    toggleButtonC1 = new JToggleButton(ProficiencyLevel.C1.name());
    toggleButtonC2 = new JToggleButton(ProficiencyLevel.C2.name());
    toggleButtonJ7 = new JToggleButton(ProficiencyLevel.J7.name());
    toggleButtonAllLevels = new JToggleButton("Select All");

    levelButtonsList = List.of(toggleButtonA1, toggleButtonA2, toggleButtonB1, toggleButtonB2, toggleButtonC1,
        toggleButtonC2, toggleButtonJ7);
    levelButtonsList.forEach(levelButton -> {
      levelButton.setForeground(Color.WHITE);
      var currentFont = levelButton.getFont();
      levelButton.setBackground(LEVEL_COLOURS.get(levelButton.getText()));
      levelButton.setFont(new Font(currentFont.getName(), Font.BOLD, currentFont.getSize()));
      levelButton.addActionListener(e -> {
        levelButton.setForeground(Color.WHITE);
        if (levelButton.isSelected()) {
          levelButton.setBackground(new Color(0, 0, 0, 0));
        } else {
          levelButton.setBackground(LEVEL_COLOURS.get(levelButton.getText()));
        }
      });
    });

    toggleButtonAllLevels.addActionListener(event -> {
      var abstractButton = (AbstractButton) event.getSource();
      var selected = abstractButton.getModel().isSelected();
      levelButtonsList.forEach(levelButton -> {
        levelButton.setSelected(selected);
        if (levelButton.isSelected()) {
          levelButton.setBackground(new Color(0, 0, 0, 0));
        } else {
          levelButton.setBackground(LEVEL_COLOURS.get(levelButton.getText()));
        }
      });
    });

    levelsPanel = new JPanel();
    levelsPanel.setLayout(new BoxLayout(levelsPanel, BoxLayout.X_AXIS));
    levelButtonsList.forEach(levelsPanel::add);
    levelsPanel.add(toggleButtonAllLevels);
  }

  private void initWordSearchValueField() {
    wordSearchValueField = new JTextField("Search...", 20);
    wordSearchValueField.setForeground(Color.GRAY);
    wordSearchValueField.setText("Search...");
    wordSearchValueField.addKeyListener(new WordSearchValueFieldKeyListener());
    wordSearchValueField.addFocusListener(new FocusListener() {
      @Override
      public void focusGained(FocusEvent e) {
        if (wordSearchValueField.getText().equals("Search...")) {
          wordSearchValueField.setText("");
          wordSearchValueField.setForeground(Color.BLACK);
        }
      }

      @Override
      public void focusLost(FocusEvent e) {
        if (wordSearchValueField.getText().isEmpty()) {
          wordSearchValueField.setForeground(Color.GRAY);
          wordSearchValueField.setText("Search...");
        }
      }
    });
  }

  private void executeWordSearch() {
    var searchText = wordSearchValueField.getText();
    var searchParams = SearchParams.builder();
    if (!"Search...".equals(searchText)) {
      searchParams.searchKey(searchText);
    }
    var selectedLevels = levelButtonsList.stream()
        .filter(AbstractButton::isSelected)
        .map(AbstractButton::getText)
        .map(ProficiencyLevel::valueOf)
        .toList();
    searchParams.levels(selectedLevels);
    var selectedLearningStatuses = learningStatusButtonList.stream()
        .filter(AbstractButton::isSelected)
        .map(AbstractButton::getText)
        .map(LearningStatus::findByLabel)
        .toList();
    searchParams.statuses(selectedLearningStatuses);
    var searchResult = wordService.search(searchParams.build());
    wordsTable.reloadTable(searchResult);
  }

  public void setWordsTable(WordsTable wordsTable) {
    this.wordsTable = wordsTable;
  }

  public static void setWordService(WordService wordService) {
    SettingsPanel.wordService = wordService;
  }

  class WordSearchValueFieldKeyListener extends KeyAdapter {

    public void keyPressed(KeyEvent e) {
      if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        executeWordSearch();
      }
    }
  }
}
