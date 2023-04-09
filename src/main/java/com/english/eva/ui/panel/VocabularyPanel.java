package com.english.eva.ui.panel;

import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.english.eva.ui.panel.meaning.MeaningTree;
import com.english.eva.ui.panel.settings.SettingsPanel;
import com.english.eva.ui.panel.word.WordsTableNew;
import net.miginfocom.swing.MigLayout;

public class VocabularyPanel extends AbstractPanel implements ActionPanel {

  private JSplitPane mainSplitPane;

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
    mainSplitPane = new JSplitPane();
    mainSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
    mainSplitPane.setResizeWeight(0.3);

    var meaningTree = new MeaningTree();
    var wordsTable = new WordsTableNew(meaningTree);
    meaningTree.setWordsTable(wordsTable);
    mainSplitPane.setLeftComponent(new JScrollPane(wordsTable));
    mainSplitPane.setRightComponent(new JScrollPane(meaningTree));

    var settingsPanel = new SettingsPanel();
    settingsPanel.setWordsTable(wordsTable);
    add(settingsPanel, "cell 0 0, pushx");
    add(mainSplitPane, "cell 0 1, push, grow");
    isComponentLoaded = true;
  }

}
