package com.english.eva.ui.panel;

import java.awt.Dimension;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;

import com.english.eva.entity.Word;
import com.english.eva.service.WordService;
import com.english.eva.ui.panel.word.WordsTable;
import net.miginfocom.swing.MigLayout;

public class VocabularyPanel extends AbstractPanel implements ActionPanel {

  private static WordService wordService;

  public static void setWordService(WordService wordService) {
    VocabularyPanel.wordService = wordService;
  }

  private JPanel settingsPanel;
  private JSplitPane mainSplitPane;

  private static Integer INIT_Y_CELL = -1;

  public VocabularyPanel() {
    super();
    setLayout(new MigLayout("fill"));
    setPreferredSize(new Dimension(600, 600));
  }

  @Override
  public void initComponents() {
    if (isComponentLoaded) {
      return;
    }
    initSettingsPanel();
    initSplitPane();

    add(settingsPanel, "cell 0 0, pushx");
    add(mainSplitPane, "cell 0 1, push, grow");
    isComponentLoaded = true;
  }

  private void initSettingsPanel() {
    //search
    settingsPanel = new JPanel();
    settingsPanel.setBorder(new TitledBorder("Settings"));

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

    settingsPanel.add(wordSearchLabel);
    settingsPanel.add(wordSearchValue);
    settingsPanel.add(button);
  }

  private static JTextField addTextField(JPanel panel, String labelText) {
    var label = new JLabel(labelText);
    var field = new JTextField(20);
    panel.add(label, String.format("cell 0 %s", INIT_Y_CELL += 1));
    panel.add(field, String.format("cell 1 %s", INIT_Y_CELL));
    return field;
  }

//  private static boolean isEmpty(EnvironmentInfo env) {
//    return Stream.of(
//            env.getName(), env.getRegion().name(), env.getApiUrl(),
//            env.getKeycloakUrl(), env.getUsername(), env.getPassword())
//        .anyMatch(StringUtils::isBlank);
//  }

  private void initSplitPane() {
    mainSplitPane = new JSplitPane();
    mainSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
    mainSplitPane.setResizeWeight(0.3);
    mainSplitPane.setLeftComponent(new JScrollPane(new WordsTable()));
    mainSplitPane.setRightComponent(new JScrollPane(new JTree()));
  }

}
