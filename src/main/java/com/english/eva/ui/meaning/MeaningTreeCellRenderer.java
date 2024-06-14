package com.english.eva.ui.meaning;

import static com.english.eva.ui.util.ColorUtils.LEARNING_COLOURS;
import static com.english.eva.ui.util.ColorUtils.LEVEL_COLOURS;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import com.english.eva.entity.ProficiencyLevel;

public class MeaningTreeCellRenderer extends JPanel implements TreeCellRenderer {

  private final DefaultTreeCellRenderer defaultTreeCellRenderer = new DefaultTreeCellRenderer();

  public MeaningTreeCellRenderer() {
    setLayout(new BorderLayout());
    setOpaque(false);
  }

  @Override
  public Component getTreeCellRendererComponent(
      JTree tree,
      Object value,
      boolean selected,
      boolean expanded,
      boolean leaf,
      int row,
      boolean hasFocus) {
    var renderer = (DefaultTreeCellRenderer) defaultTreeCellRenderer.getTreeCellRendererComponent(
        tree, value, selected, expanded, leaf, row, hasFocus);
    setBorder(new EmptyBorder(5, 5, 5, 5));
    final var keyField = new JLabel();
    final var valueField = new JLabel();
    var path = tree.getPathForRow(row);
    var isTargetRow = false;
    if (Objects.nonNull(path)) {
      isTargetRow = path.getPath().length == 3 || path.getPath().length == 4;
    }
    var font = renderer.getFont();
    var node = (DefaultMutableTreeNode) value;
    var userObject = node.getUserObject();
    if (userObject instanceof String castedObject) {
      var isDesc = Arrays.stream(ProficiencyLevel.values()).map(Enum::name).anyMatch(castedObject::startsWith);
      if (castedObject.contains("$")) {
        handleTargetLearningCase(keyField, valueField, font, castedObject);
      } else if (isDesc) {
        handleProficiencyDescriptionCase(keyField, valueField, font, castedObject);
      } else if (leaf) {
        handleLeafCase(keyField, valueField, font, castedObject);
      } else if (castedObject.equals("Examples")) {
        handleExamplesCase(keyField, valueField, font, castedObject);
      } else if (isTargetRow) {
        handleTargetRowCase(keyField, valueField, font, castedObject);
      } else {
        handleDefaultCase(keyField, valueField, font, castedObject);
      }
    } else {
      return renderer;
    }
    removeAll();
    add(keyField, BorderLayout.WEST);
    add(valueField, BorderLayout.CENTER);
    return this;
  }

  private static void handleTargetLearningCase(JLabel keyField, JLabel valueField, Font font, String castedObject) {
    var parts = castedObject.split("\\$");
    var learningStatus = parts[0].strip();
    var target = parts[1].strip();

    keyField.setText(target + " ");
    keyField.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));

    valueField.setText(learningStatus);
    valueField.setOpaque(true);
    valueField.setBackground(LEARNING_COLOURS.get(learningStatus));
    valueField.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
    valueField.setBorder(new EmptyBorder(3, 3, 3, 3));
  }

  private static void handleDefaultCase(JLabel keyField, JLabel valueField, Font font, String castedObject) {
    keyField.setText(castedObject);
    keyField.setFont(font);
    valueField.setText(null);
  }

  private static void handleTargetRowCase(JLabel keyField, JLabel valueField, Font font, String castedObject) {
    keyField.setText(castedObject);
    keyField.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
    valueField.setText(null);
  }

  private static void handleExamplesCase(JLabel keyField, JLabel valueField, Font font, String castedObject) {
    keyField.setText(castedObject);
    keyField.setFont(new Font(font.getName(), Font.ITALIC, font.getSize() - 1));
    valueField.setText(null);
  }

  private static void handleLeafCase(JLabel keyField, JLabel valueField, Font font, String castedObject) {
    keyField.setText(castedObject);
    keyField.setFont(new Font(font.getName(), font.getStyle(), font.getSize()));
    valueField.setText(null);
  }

  private static void handleProficiencyDescriptionCase(
      JLabel keyField,
      JLabel valueField,
      Font font,
      String castedObject) {
    var parts = castedObject.split("=");
    var level = parts[0].strip();
    keyField.setText(level);
    keyField.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
    keyField.setOpaque(true);
    keyField.setBorder(new EmptyBorder(0, 10, 0, 10));
    keyField.setBackground(LEVEL_COLOURS.get(level));
    keyField.setForeground(new Color(255, 255, 255));
    valueField.setText(" " + parts[1].strip());
    valueField.setFont(new Font(font.getName(), font.getStyle(), font.getSize() + 2));
  }

//  private void handleTargetLearningCase(JLabel keyField, JLabel valueField, Font font, String castedObject) {
//    var parts = castedObject.split("\\$");
//    var learningStatus = parts[0].strip();
//    var target = parts[1].strip();
//
//    keyField.setText(target + " ");
//    keyField.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
//
//    valueField.setText(learningStatus);
//    valueField.setOpaque(true);
//    valueField.setBackground(LEANING_COLOURS.get(learningStatus));
//    valueField.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
//    valueField.setBorder(new EmptyBorder(3, 3, 3, 3));
//
//  }

//  private static <T> JComboBox<T> addComboBoxFieldEnums(JPanel envPanel, T[] values, String labelText) {
//    var enums = new JComboBox<>(values);
//    var label = new JLabel(labelText);
//    envPanel.add(label, String.format("cell 0 %s", INIT_Y_CELL += 1));
//    envPanel.add(enums, String.format("cell 1 %s", INIT_Y_CELL));
//    return enums;
//  }
}
