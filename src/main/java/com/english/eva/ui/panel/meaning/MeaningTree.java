package com.english.eva.ui.panel.meaning;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Comparator;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.english.eva.entity.Meaning;
import com.english.eva.entity.MeaningSource;
import com.english.eva.entity.PartOfSpeech;
import com.english.eva.entity.Word;
import com.english.eva.service.MeaningService;

public class MeaningTree extends JTree {

  private static MeaningService meaningService;

  public static void setMeaningService(MeaningService meaningService) {
    MeaningTree.meaningService = meaningService;
  }

  public MeaningTree() {
    setVisible(false);
    setRootVisible(false);
    setShowsRootHandles(true);
    setCellRenderer(new MeaningTreeCellRenderer());

    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        for (int row : getSelectionRows()) {
          removeSelectionInterval(row, row);
        }
      }
    });

  }

  public void showSelectedUserObjectTree(Word word) {
    var root = new DefaultMutableTreeNode(word.getText());
    var meanings = word.getMeaning();
    var sources = meanings.stream().map(Meaning::getMeaningSource).distinct().toList();

    for (MeaningSource source : sources) {
      var sourceNode = new DefaultMutableTreeNode(source.getLabel());
      root.add(sourceNode);
      var partsOfSpeechBySource = meanings.stream()
          .filter(meaning -> meaning.getMeaningSource() == source)
          .map(Meaning::getPartOfSpeech).distinct().sorted(Comparator.naturalOrder())
          .toList();
      for (PartOfSpeech partOfSpeech : partsOfSpeechBySource) {
        var partOfSpeechNode = new DefaultMutableTreeNode(word.getText() + " · " + partOfSpeech.getLabel());
        var meaningsBySourceAndPartOfSpeech = meanings.stream()
            .filter(meaning -> meaning.getMeaningSource() == source)
            .filter(meaning -> meaning.getPartOfSpeech() == partOfSpeech)
            .map(Meaning::getId)
            .toList();
        for (Long meaningId : meaningsBySourceAndPartOfSpeech) {
          var meaning = meaningService.getMeaning(meaningId);
          var meaningNode = new DefaultMutableTreeNode(meaning.getTarget());
          var descriptionNode = new DefaultMutableTreeNode(
              meaning.getProficiencyLevel() + "=" + meaning.getDescription());
          meaningNode.add(descriptionNode);
          partOfSpeechNode.add(meaningNode);
          var examplesNode = new DefaultMutableTreeNode("Examples");
          for (String example : meaning.getExamples()) {
            var exampleNode = new DefaultMutableTreeNode(example);
            examplesNode.add(exampleNode);
          }
          meaningNode.add(examplesNode);
          sourceNode.add(partOfSpeechNode);
        }
      }
    }

    var defaultTreeModel = new DefaultTreeModel(root);
    setModel(defaultTreeModel);
    for (int i = 0; i < getRowCount(); i++) {
      var path = getPathForRow(i);
      if (!path.toString().contains("Examples")) {
        expandPath(path);
      }
    }
    setVisible(true);
  }
}
