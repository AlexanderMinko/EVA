package com.english.eva.ui.panel.settings;

import java.util.Date;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.english.eva.entity.Word;
import com.english.eva.service.WordService;
import net.miginfocom.swing.MigLayout;

public class SettingsPanel extends JPanel {

  private static WordService wordService;

  public static void setWordService(WordService wordService) {
    SettingsPanel.wordService = wordService;
  }

  private static Integer INIT_Y_CELL = -1;

  public SettingsPanel() {
    //search
    setBorder(new TitledBorder("Settings"));

    //Search duplicates
    var wordSearchLabel = new JLabel("Search:");
    var wordSearchValue = new JTextField(20);

    //Add new word
    var button = new JButton();
    button.setText("Add New");

    button.addActionListener(event -> {
      var newWordPanel = new JPanel();
      newWordPanel.setLayout(new MigLayout());

      var textField = addTextField(newWordPanel, "Word:");
      var transcriptField = addTextField(newWordPanel, "Transcript:");
      var frequencyField = addTextField(newWordPanel, "Frequency:");
      var topicField = addTextField(newWordPanel, "Topic:");

      var option = JOptionPane.showConfirmDialog(
          null,
          newWordPanel,
          "Add new word",
          JOptionPane.OK_CANCEL_OPTION,
          JOptionPane.INFORMATION_MESSAGE);

      if (option == JOptionPane.CANCEL_OPTION) {
        System.out.println("CLOSED");
        return;
      }

      if (option == JOptionPane.OK_OPTION) {
        var dateCreated = new Date();
        var word = Word.builder()
            .text(textField.getText())
            .transcript(transcriptField.getText())
            .frequency(Integer.parseInt(frequencyField.getText()))
            .topic(topicField.getText())
            .dateCreated(dateCreated)
            .lastModified(dateCreated)
            .build();
        wordService.save(word);
//          table.setModel(new DefaultTableModel(getEnvironmentData(), COLUMNS));
      }
    });

    add(wordSearchLabel);
    add(wordSearchValue);
    add(button);
  }

  private static JTextField addTextField(JPanel panel, String labelText) {
    var label = new JLabel(labelText);
    var field = new JTextField(20);
    panel.add(label, String.format("cell 0 %s", INIT_Y_CELL += 1));
    panel.add(field, String.format("cell 1 %s", INIT_Y_CELL));
    return field;
  }
}
