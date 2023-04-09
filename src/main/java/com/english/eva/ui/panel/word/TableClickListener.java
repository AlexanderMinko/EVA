package com.english.eva.ui.panel.word;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.english.eva.entity.LearningStatus;
import com.english.eva.entity.Meaning;
import com.english.eva.entity.MeaningSource;
import com.english.eva.entity.PartOfSpeech;
import com.english.eva.entity.ProficiencyLevel;
import com.english.eva.entity.Word;
import com.english.eva.service.MeaningService;
import com.english.eva.service.WordService;
import com.english.eva.ui.panel.meaning.MeaningTree;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;

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
      meaningTree.showSelectedUserObjectTree();
    }
  }

  private void showWordPopupMenu(MouseEvent e, Long selectedWordId) {
    var popupMenu = new JPopupMenu();

    var addNewWordItem = new JMenuItem("Add new word");
    addNewWordItem.addActionListener(event -> handleAddNewWordItem());
    var addMeaningItem = new JMenuItem("Add meaning");
    addMeaningItem.addActionListener(event -> handleAddMeaningItem(selectedWordId));
    var deleteWordItem = new JMenuItem("Delete word");
    deleteWordItem.addActionListener(event -> handleDeleteWordItem());

    popupMenu.add(addMeaningItem);
    popupMenu.add(addNewWordItem);
    popupMenu.addSeparator();
    popupMenu.add(deleteWordItem);
    popupMenu.show(e.getComponent(), e.getX(), e.getY());
  }

  private void handleAddNewWordItem() {
    var newWordPanel = new JPanel();
    newWordPanel.setLayout(new MigLayout());

    var textField = addTextField(newWordPanel, "Word", 20);
    var transcriptField = addTextField(newWordPanel, "Transcript", 20);
    var frequencyField = addTextField(newWordPanel, "Frequency", 20);
    var topicField = addTextField(newWordPanel, "Topic", 20);

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
      var word = Word.builder()
          .text(textField.getText().strip())
          .transcript(transcriptField.getText().strip())
          .frequency(Integer.parseInt(frequencyField.getText().strip()))
          .topic(topicField.getText().strip())
          .dateCreated(dateCreated)
          .lastModified(dateCreated)
          .build();
      var saved = wordService.save(word);
      saved.setMeaning(new ArrayList<>());
      wordsTable.reloadTable(saved);
    }
  }

  private void handleDeleteWordItem() {
    var tableModel = (DefaultTableModel) wordsTable.getModel();
    var selectedRow = wordsTable.getSelectedRow();
    var wordId = (String) tableModel.getValueAt(selectedRow, 0);
    var wordText = (String) tableModel.getValueAt(selectedRow, 1);
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
      wordService.delete(Long.parseLong(wordId));
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
    var targetField = addTextField(newMeaningPanel, "Target");
    var partOfSpeechField = addComboBoxFieldEnums(newMeaningPanel, partOfSpeechStrings, "Part of speech");
    var proficiencyLeveField = addComboBoxFieldEnums(newMeaningPanel, ProficiencyLevel.values(), "Proficiency level");
    var learningStatus = addComboBoxFieldEnums(newMeaningPanel, learningStatusStrings, "Learning status");
    var descriptionField = addTextField(newMeaningPanel, "Description");
    var examplesField = addTextAreaField(newMeaningPanel, "Examples");
    var alsoField = addTextField(newMeaningPanel, "Also");

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
          .also(alsoField.getText())
          .examples(Arrays.stream(examplesField.getText().split("\\n")).filter(StringUtils::isNotBlank).toList())
          .word(word)
          .dateCreated(dateCreated)
          .lastModified(dateCreated)
          .build();
      meaningService.save(meaning);
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
    var field = new JTextArea(6, 40);
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
