package com.english.eva.ui.word;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import javax.swing.*;

import org.apache.commons.lang3.StringUtils;

import com.english.eva.entity.Example;
import com.english.eva.entity.LearningStatus;
import com.english.eva.entity.Meaning;
import com.english.eva.entity.MeaningSource;
import com.english.eva.entity.PartOfSpeech;
import com.english.eva.entity.ProficiencyLevel;
import com.english.eva.entity.Word;
import com.english.eva.service.MeaningService;
import com.english.eva.service.WordService;
import com.english.eva.ui.meaning.MeaningTree;

import net.miginfocom.swing.MigLayout;

public class TableClickListener extends MouseAdapter {

  private static WordService wordService;
  private static MeaningService meaningService;

  private static Integer INIT_Y_CELL = -1;
  private final WordsTableNew wordsTable;
  private final MeaningTree meaningTree;

  public TableClickListener(WordsTableNew wordsTable, MeaningTree meaningTree) {
    this.wordsTable = wordsTable;
    this.meaningTree = meaningTree;
  }

  @Override
  public void mousePressed(MouseEvent event) {
    // selects the row at which point the mouse is clicked
    var point = event.getPoint();
    var currentRow = wordsTable.rowAtPoint(point);
    wordsTable.setRowSelectionInterval(currentRow, currentRow);
    var selectedWordId = (Long) wordsTable.getModel().getValueAt(currentRow, WordTableModel.HIDDEN_WORD_ID);
    if (SwingUtilities.isRightMouseButton(event)) {
      showWordPopupMenu(event, selectedWordId);
    }
    if (SwingUtilities.isLeftMouseButton(event)) {
      var selectedWord = wordService.getById(selectedWordId);
      meaningTree.setWord(selectedWord);
      SwingUtilities.invokeLater(meaningTree::showSelectedUserObjectTree);
    }
  }

  private void showWordPopupMenu(MouseEvent e, Long selectedWordId) {
    var popupMenu = new JPopupMenu();

    var addNewWordItem = new JMenuItem("Add new word");
    addNewWordItem.addActionListener(event -> handleAddNewWordItem());
    var addMeaningItem = new JMenuItem("Add meaning");
    addMeaningItem.addActionListener(event -> handleAddMeaningItem(selectedWordId));
    var addMeaningItemExperimental = new JMenuItem("Add meaning exp");
    addMeaningItemExperimental.addActionListener(event -> new ExperimentalHandler(meaningTree)
        .handleAddMeaningItem(selectedWordId));
    var editWordItem = new JMenuItem("Edit word");
    editWordItem.addActionListener(event -> handleEditWordItem(selectedWordId));
    var deleteWordItem = new JMenuItem("Delete word");
    deleteWordItem.addActionListener(event -> handleDeleteWordItem());

    popupMenu.add(addMeaningItem);
    popupMenu.add(addNewWordItem);
    popupMenu.add(addMeaningItemExperimental);
    popupMenu.add(editWordItem);
    popupMenu.add(deleteWordItem);
    popupMenu.show(e.getComponent(), e.getX(), e.getY());
  }

  private void handleAddNewWordItem() {
    INIT_Y_CELL = -1;
    var newWordPanel = new JPanel();
    newWordPanel.setLayout(new MigLayout());

    var textField = addTextField(newWordPanel, "Word", 20);
    var warningLabel = new JLabel();
    warningLabel.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
    warningLabel.setVisible(false);
    newWordPanel.add(warningLabel, "cell 0 0");
    textField.addKeyListener(new KeyListener() {
      public void keyTyped(KeyEvent e) {}
      public void keyPressed(KeyEvent e) {}
      public void keyReleased(KeyEvent e) {
        warningLabel.setVisible(wordService.isWordExistsByText(textField.getText()));
      }
    });

    var transcriptField = addTextField(newWordPanel, "Transcript", 20);
    var frequencyField = addTextField(newWordPanel, "Frequency", 20);

    var option = JOptionPane.showOptionDialog(
        null,
        newWordPanel,
        "Add new word",
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.PLAIN_MESSAGE,
        null,
        new String[] {"Add", "Cancel"},
        null);

    if (option == JOptionPane.CANCEL_OPTION) {
      return;
    }

    if (option == JOptionPane.OK_OPTION) {
      var dateCreated = new Date();
      var frequencyText = frequencyField.getText().strip();
      var transcriptText = transcriptField.getText().strip();
      if (transcriptText.contains("/")) {
        transcriptText = transcriptText.replace("/", StringUtils.EMPTY);
      }
      var word = Word.builder()
          .text(textField.getText().strip())
          .transcript(transcriptText)
          .frequency(StringUtils.isBlank(frequencyText) ? 0 : Integer.parseInt(frequencyText))
          .dateCreated(dateCreated)
          .lastModified(dateCreated)
          .build();
      var saved = wordService.save(word);
      saved.setMeanings(new ArrayList<>());
      wordsTable.reloadTable(saved);
    }
  }

  private void handleDeleteWordItem() {
    var tableModel = (WordTableModel) wordsTable.getModel();
    var selectedRow = wordsTable.getSelectedRow();
    var wordId = (Long) tableModel.getValueAt(selectedRow, WordTableModel.HIDDEN_WORD_ID);
    var wordText = (String) tableModel.getValueAt(selectedRow, WordTableModel.COLUMN_WORD);
    var option = JOptionPane.showOptionDialog(
        null,
        new JLabel("<html>Are you sure that you want to delete <b>" + wordText + "</b> word?"),
        "Confirm word deleting: " + wordText,
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.WARNING_MESSAGE,
        null,
        new String[] {"Yes", "Cancel"},
        null);

    if (option == JOptionPane.CANCEL_OPTION) {
      return;
    }

    if (option == JOptionPane.OK_OPTION) {
      wordService.delete(wordId);
      tableModel.removeRow(selectedRow);
    }
  }

  private void handleAddMeaningItem(Long selectedWordId) {
    var newMeaningPanel = new JPanel();
    newMeaningPanel.setLayout(new MigLayout());

    var sourceStrings = Arrays.stream(MeaningSource.values()).map(MeaningSource::getLabel).toArray(String[]::new);
    var partOfSpeechStrings = Arrays.stream(PartOfSpeech.values()).map(PartOfSpeech::getLabel).toArray(String[]::new);
    var learningStatusStrings = Arrays.stream(LearningStatus.values()).map(LearningStatus::getLabel).toArray(String[]::new);

    var sourceField = addComboBoxFieldEnums(newMeaningPanel, sourceStrings, "Source");
    sourceField.setSelectedItem(MeaningSource.CAMBRIDGE_DICTIONARY.getLabel());
    var targetField = addTextField(newMeaningPanel, "Target");
    var partOfSpeechField = addComboBoxFieldEnums(newMeaningPanel, partOfSpeechStrings, "Part of speech");
    var proficiencyLeveField = addComboBoxFieldEnums(newMeaningPanel, ProficiencyLevel.values(), "Proficiency level");
    proficiencyLeveField.setSelectedItem(ProficiencyLevel.J7);
    var learningStatus = addComboBoxFieldEnums(newMeaningPanel, learningStatusStrings, "Learning status");
    learningStatus.setSelectedItem(LearningStatus.LEARNING.getLabel());
    var descriptionField = addTextField(newMeaningPanel, "Description", 50);
    var examplesField = addTextAreaField(newMeaningPanel, "Examples");

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
      var dateCreated = new Date();
      var meaning = Meaning.builder()
              .meaningSource(MeaningSource.findByLabel((String) sourceField.getSelectedItem()))
              .target(targetField.getText())
              .partOfSpeech(PartOfSpeech.findByLabel((String) partOfSpeechField.getSelectedItem()))
              .proficiencyLevel((ProficiencyLevel) proficiencyLeveField.getSelectedItem())
              .learningStatus(LearningStatus.findByLabel((String) learningStatus.getSelectedItem()))
              .description(descriptionField.getText())
              .word(word)
              .dateCreated(dateCreated)
              .lastModified(dateCreated)
              .build();
      var examples = Arrays.stream(examplesField.getText().split("\\n"))
          .filter(StringUtils::isNotBlank)
          .map(text -> new Example(text, meaning))
          .toList();
      meaning.setExamples(examples);
      meaningService.save(meaning);
      meaningTree.setWord(wordService.getById(selectedWordId));
      meaningTree.showSelectedUserObjectTree();
    }
  }

  private void handleEditWordItem(Long selectedWordId) {
    var exitingWord = wordService.getById(selectedWordId);
    if (Objects.isNull(exitingWord)) {
      return;
    }
    var existingWordPanel = new JPanel();
    existingWordPanel.setLayout(new MigLayout());

    var textField = addTextField(existingWordPanel, "Word", 20);
    textField.setText(exitingWord.getText());
    var transcriptField = addTextField(existingWordPanel, "Transcript", 20);
    transcriptField.setText(exitingWord.getTranscript());
    var frequencyField = addTextField(existingWordPanel, "Frequency", 20);
    frequencyField.setText(exitingWord.getFrequency() == 0 ? "" : String.valueOf(exitingWord.getFrequency()));

    var option = JOptionPane.showOptionDialog(
        null,
        existingWordPanel,
        "Edit word: " + exitingWord.getText(),
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.PLAIN_MESSAGE,
        null,
        new String[] {"Edit", "Cancel"},
        null);

    if (option == JOptionPane.CANCEL_OPTION) {
      return;
    }

    if (option == JOptionPane.OK_OPTION) {
      var isAnyUpdated = false;
      var newText = textField.getText().strip();
      if (!StringUtils.equals(exitingWord.getText(), newText)) {
        exitingWord.setText(newText);
        isAnyUpdated = true;
      }
      var newTranscript = transcriptField.getText().strip();
      if (!StringUtils.equals(exitingWord.getTranscript(), newTranscript)) {
        exitingWord.setTranscript(newTranscript);
        isAnyUpdated = true;
      }
      var frequencyText = frequencyField.getText().strip();
      var newFrequency = StringUtils.isBlank(frequencyText) ? 0 : Integer.parseInt(frequencyText);
      if (!exitingWord.getFrequency().equals(newFrequency)) {
        exitingWord.setFrequency(newFrequency);
        isAnyUpdated = true;
      }
      if (!isAnyUpdated) {
        return;
      }
//      exitingWord.setLastModified(new Date());
      wordService.save(exitingWord);
      wordsTable.reloadTable();
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
    var field = new JTextArea(8, 50);
    panel.add(label, String.format("cell 0 %s", INIT_Y_CELL += 1));
    panel.add(new JScrollPane(field), String.format("cell 1 %s", INIT_Y_CELL));
    return field;
  }

  public static void setWordService(WordService wordService) {
    TableClickListener.wordService = wordService;
  }

  public static void setMeaningService(MeaningService meaningService) {
    TableClickListener.meaningService = meaningService;
  }
}
