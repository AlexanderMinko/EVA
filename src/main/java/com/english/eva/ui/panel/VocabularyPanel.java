package com.english.eva.ui.panel;

import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import com.english.eva.ui.meaning.MeaningTree;
import com.english.eva.ui.settings.SettingsPanel;
import com.english.eva.ui.word.WordsTableNew;
import net.miginfocom.swing.MigLayout;

public class VocabularyPanel extends AbstractPanel implements ActionPanel {

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
    var mainSplitPane = new JSplitPane();
    mainSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
    mainSplitPane.setResizeWeight(0.3);

    var meaningTree = new MeaningTree();
    var wordsTable = new WordsTableNew(meaningTree);
    meaningTree.setWordsTable(wordsTable);
//    var wordsPane = new JTabbedPane();
//    var meaningPanel = new JPanel();
//    wordsPane.addTab("Words", new JScrollPane(wordsTable));
//    wordsPane.addTab("Meanings", meaningPanel);
    mainSplitPane.setLeftComponent(new JScrollPane(wordsTable));
    mainSplitPane.setRightComponent(new JScrollPane(meaningTree));

    var settingsPanel = new SettingsPanel();
    settingsPanel.setWordsTable(wordsTable);
    add(settingsPanel, "cell 0 0, pushx");
    add(mainSplitPane, "cell 0 1, push, grow");
    isComponentLoaded = true;
  }

}

//  private static JTabbedPane initTabs() {
//    var rootTab = new JTabbedPane();
//    rootTab.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
//    var vocabularyPanel = new VocabularyPanel();
//    vocabularyPanel.initComponents();
//    rootTab.addTab("Vocabulary", vocabularyPanel);
//    rootTab.setSelectedIndex(0);
//    return rootTab;
//  }
