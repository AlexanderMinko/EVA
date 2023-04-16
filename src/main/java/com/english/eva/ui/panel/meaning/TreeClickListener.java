package com.english.eva.ui.panel.meaning;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Objects;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import com.english.eva.entity.LearningStatus;
import com.english.eva.entity.PartOfSpeech;
import com.english.eva.service.MeaningService;
import com.english.eva.service.WordService;
import com.english.eva.ui.panel.word.WordsTableNew;

public class TreeClickListener extends MouseAdapter {

  private static MeaningService meaningService;
  private static WordService wordService;

  private final MeaningTree meaningTree;
  private final WordsTableNew wordsTable;

  public TreeClickListener(MeaningTree meaningTree, WordsTableNew wordsTable) {
    this.meaningTree = meaningTree;
    this.wordsTable = wordsTable;
  }

  @Override
  public void mouseClicked(MouseEvent event) {
    if (SwingUtilities.isRightMouseButton(event)) {
      doPopup(event);
    }
  }

  private void doPopup(MouseEvent event) {
    var path = meaningTree.getPathForLocation(event.getX(), event.getY());
    if (Objects.isNull(path)) {
      return;
    }
    var node = (DefaultMutableTreeNode) path.getLastPathComponent();
    if (node.getUserObject() instanceof String targetLearning && (targetLearning.contains("$"))) {
      var id = Long.parseLong(targetLearning.split("\\$")[2]);
      var popupMenu = new JPopupMenu();
      Arrays.stream(LearningStatus.values())
          .map(status -> new JMenuItem(status.getLabel()))
          .peek(menuItem -> menuItem.addActionListener(
              menuEvent -> handleLearningUpdate(id, LearningStatus.findByLabel(menuItem.getText()))))
          .forEach(popupMenu::add);
      var partOfSpeechMenuItem = new JMenu("Part of Speech");
      Arrays.stream(PartOfSpeech.values())
          .map(part -> new JMenuItem(part.getLabel()))
          .peek(menuItem -> menuItem.addActionListener(menuEvent -> handlePartOfSpeechUpdate(id, menuItem.getText())))
          .forEach(partOfSpeechMenuItem::add);
      popupMenu.addSeparator();
      popupMenu.add(partOfSpeechMenuItem);
      popupMenu.show(event.getComponent(), event.getX(), event.getY());
    }
  }

  private void handleLearningUpdate(long id, LearningStatus putOff) {
    meaningService.updateLearningStatus(id, putOff);
    meaningTree.showSelectedUserObjectTree();
    wordsTable.reloadTable();
  }

  private void handlePartOfSpeechUpdate(long id, String partOfSpeech) {
    meaningService.updatePartOfSpeech(id, PartOfSpeech.findByLabel(partOfSpeech));
    meaningTree.setWord(wordService.getById(meaningTree.getWord().getId()));
    meaningTree.showSelectedUserObjectTree();
    wordsTable.reloadTable();
  }

  public static void setWordService(WordService wordService) {
    TreeClickListener.wordService = wordService;
  }

  public static void setMeaningService(MeaningService meaningService) {
    TreeClickListener.meaningService = meaningService;
  }
}
