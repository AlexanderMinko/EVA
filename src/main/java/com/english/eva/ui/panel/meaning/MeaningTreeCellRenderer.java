package com.english.eva.ui.panel.meaning;

import static com.english.eva.common.Constants.COLON;
import static com.english.eva.common.Constants.COLON_SPACE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import com.english.eva.entity.ProficiencyLevel;

public class MeaningTreeCellRenderer extends JPanel implements TreeCellRenderer {

  private final static Map<String, Color> LEVEL_COLOURS = new HashMap<>();
  static {
    LEVEL_COLOURS.put(ProficiencyLevel.A1.name(), new Color(255, 128, 0));
    LEVEL_COLOURS.put(ProficiencyLevel.A2.name(), new Color(0, 160, 160));
    LEVEL_COLOURS.put(ProficiencyLevel.B1.name(), new Color(255, 0, 0));
    LEVEL_COLOURS.put(ProficiencyLevel.B2.name(), new Color(0, 128, 64));
    LEVEL_COLOURS.put(ProficiencyLevel.C1.name(), new Color(48, 96, 255));
    LEVEL_COLOURS.put(ProficiencyLevel.C2.name(), new Color(160, 48, 160));
    LEVEL_COLOURS.put(ProficiencyLevel.J7.name(), new Color(26, 27, 31));
  }

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
    var renderer = defaultTreeCellRenderer.getTreeCellRendererComponent(
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
      if (castedObject.contains(COLON)) {
        var parts = castedObject.split(COLON);
        keyField.setText(parts[0].strip() + COLON_SPACE);
        keyField.setFont(new Font(font.getName(), Font.ITALIC, font.getSize() - 1));
        valueField.setText(parts[1].strip());
        valueField.setFont(new Font(font.getName(), font.getStyle(), font.getSize()));
      } else if (castedObject.contains("][]][]")) {
        
      } else if (isDesc) {
        var parts = castedObject.split("=");
        var level = parts[0].strip();
        keyField.setText(level);
        keyField.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
        keyField.setOpaque(true);
        keyField.setBorder(new EmptyBorder(0, 10, 0, 10));
        keyField.setBackground(LEVEL_COLOURS.get(level));
        keyField.setForeground(new Color(255, 255, 255));
        valueField.setText(" " + parts[1].strip());
        valueField.setFont(new Font(font.getName(), font.getStyle(), font.getSize()));
      } else if (leaf) {
        keyField.setText(castedObject);
        keyField.setFont(new Font(font.getName(), font.getStyle(), font.getSize()));
        valueField.setText(null);
      } else if (castedObject.equals("Examples")) {
        keyField.setText(castedObject);
        keyField.setFont(new Font(font.getName(), Font.BOLD, font.getSize() - 1));
        valueField.setText(null);
      } else if (isTargetRow) {
        keyField.setText(castedObject);
        keyField.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
        valueField.setText(null);
      }  else {
        keyField.setText(castedObject);
        keyField.setFont(font);
        valueField.setText(null);
      }
    } else {
      return renderer;
    }
    removeAll();
    add(keyField, BorderLayout.WEST);
    add(valueField, BorderLayout.CENTER);
    return this;
  }
}
