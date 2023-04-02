package com.english.eva.ui.panel.word;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.english.eva.entity.Meaning;
import com.english.eva.entity.MeaningSource;
import com.english.eva.entity.PartOfSpeech;
import com.english.eva.entity.ProficiencyLevel;
import com.english.eva.service.MeaningService;
import com.english.eva.service.WordService;
import com.english.eva.ui.panel.meaning.MeaningTree;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;

public class TableClickListener extends MouseAdapter {

  private static WordService wordService;
  private static MeaningService meaningService;

  public static void setWordService(WordService wordService) {
    TableClickListener.wordService = wordService;
  }

  public static void setMeaningService(MeaningService meaningService) {
    TableClickListener.meaningService = meaningService;
  }

  private static Integer INIT_Y_CELL = -1;
  private final JTable wordsTable;
  private final MeaningTree meaningTree;

  public TableClickListener(JTable wordsTable, MeaningTree meaningTree) {
    this.wordsTable = wordsTable;
    this.meaningTree = meaningTree;
  }

  @Override
  public void mousePressed(MouseEvent event) {
    // selects the row at which point the mouse is clicked
    var point = event.getPoint();
    var currentRow = wordsTable.rowAtPoint(point);
    wordsTable.setRowSelectionInterval(currentRow, currentRow);
    var selectedWordId = Long.parseLong((String) wordsTable.getModel().getValueAt(currentRow, 0));
    if (SwingUtilities.isRightMouseButton(event)) {
      showWordPopupMenu(event, selectedWordId);
    }
    if (SwingUtilities.isLeftMouseButton(event)) {
      var selectedWord = wordService.getById(selectedWordId);
      meaningTree.showSelectedUserObjectTree(selectedWord);
    }

  }

  private void showWordPopupMenu(MouseEvent e, Long selectedWordId) {
    var popupMenu = new JPopupMenu();
    var addMeaningItem = new JMenuItem("Add meaning");
    addMeaningItem.addActionListener(event -> {
      var newMeaningPanel = new JPanel();
      newMeaningPanel.setLayout(new MigLayout());

      var sourceStrings = Arrays.stream(MeaningSource.values()).map(MeaningSource::getLabel).toArray(String[]::new);
      var partOfSpeechStrings = Arrays.stream(PartOfSpeech.values()).map(PartOfSpeech::getLabel).toArray(String[]::new);

      var sourceField = addComboBoxFieldEnums(newMeaningPanel, sourceStrings, "Source:");
      var targetField = addTextField(newMeaningPanel, "Target:");
      var partOfSpeechField = addComboBoxFieldEnums(newMeaningPanel, partOfSpeechStrings, "Part of speech:");
      var proficiencyLeveField = addComboBoxFieldEnums(newMeaningPanel, ProficiencyLevel.values(), "Proficiency level:");
      var descriptionField = addTextField(newMeaningPanel, "Description");
      var alsoField = addTextField(newMeaningPanel, "Also:");
      var examplesField = addTextAreaField(newMeaningPanel, "Examples:");

      var word = wordService.getById(selectedWordId);
      if (Objects.isNull(word)) {
        return;
      }

      var option = JOptionPane.showConfirmDialog(
          null,
          newMeaningPanel,
          "Add new meaning for: " + word.getText(),
          JOptionPane.OK_CANCEL_OPTION,
          JOptionPane.INFORMATION_MESSAGE);

      if (option == JOptionPane.CANCEL_OPTION) {
        System.out.println("CLOSED");
        return;
      }

      if (option == JOptionPane.OK_OPTION) {
        var dateCreated = new Date();
        var meaning = Meaning.builder()
            .meaningSource(MeaningSource.findByLabel((String) sourceField.getSelectedItem()))
            .target(targetField.getText())
            .partOfSpeech(PartOfSpeech.findByLabel((String) partOfSpeechField.getSelectedItem()))
            .proficiencyLevel((ProficiencyLevel) proficiencyLeveField.getSelectedItem())
            .description(descriptionField.getText())
            .also(alsoField.getText())
            .examples(Arrays.stream(examplesField.getText().split("\\n")).filter(StringUtils::isNotBlank).toList())
            .word(word)
            .dateCreated(dateCreated)
            .lastModified(dateCreated)
            .build();
        meaningService.save(meaning);
      }
    });
    popupMenu.add(addMeaningItem);
    popupMenu.show(e.getComponent(), e.getX(), e.getY());
  }

  private static <T> JComboBox<T> addComboBoxFieldEnums(JPanel envPanel, T[] values, String labelText) {
    var enums = new JComboBox<>(values);
    var label = new JLabel(labelText);
    envPanel.add(label, String.format("cell 0 %s", INIT_Y_CELL += 1));
    envPanel.add(enums, String.format("cell 1 %s", INIT_Y_CELL));
    return enums;
  }

  private static JTextField addTextField(JPanel panel, String labelText) {
    var label = new JLabel(labelText);
    var field = new JTextField(30);
    panel.add(label, String.format("cell 0 %s", INIT_Y_CELL += 1));
    panel.add(field, String.format("cell 1 %s", INIT_Y_CELL));
    return field;
  }

  private static JTextArea addTextAreaField(JPanel panel, String labelText) {
    var label = new JLabel(labelText);
    var field = new JTextArea(5, 30);
    panel.add(label, String.format("cell 0 %s", INIT_Y_CELL += 1));
    panel.add(new JScrollPane(field), String.format("cell 1 %s", INIT_Y_CELL));
    return field;
  }
}
