package com.english.eva.ui.panel.meaning;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Objects;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import com.english.eva.entity.LearningStatus;
import com.english.eva.service.MeaningService;
import com.english.eva.ui.panel.word.WordsTableNew;

public class TreeClickListener extends MouseAdapter {

  private static MeaningService meaningService;

  public static void setMeaningService(MeaningService meaningService) {
    TreeClickListener.meaningService = meaningService;
  }

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
      popupMenu.show(event.getComponent(), event.getX(), event.getY());
    }
  }

  private void handleLearningUpdate(long id, LearningStatus putOff) {
    meaningService.updateLearningStatus(id, putOff);
    reloadTree();
    wordsTable.reloadTable();
  }

  private void reloadTree() {
    meaningTree.showSelectedUserObjectTree();
  }
}
