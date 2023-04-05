package com.english.eva.ui.panel.word;

import static com.english.eva.ui.panel.util.ColorUtils.LEVEL_COLOURS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.english.eva.entity.ProficiencyLevel;

public class WordTableCellRenderer extends JPanel implements TableCellRenderer {

  private final DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();

  public WordTableCellRenderer() {
    setOpaque(true);
  }

  @Override
  public Component getTableCellRendererComponent(
      JTable table,
      Object value,
      boolean isSelected,
      boolean hasFocus,
      int row,
      int column) {
    var renderer = defaultTableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
        column);
    setBackground(isSelected ? Color.LIGHT_GRAY : table.getBackground());
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, table.getGridColor()));
    var font = renderer.getFont();
    var label = new JLabel();
    var stringValue = (String) value;
    if (Objects.isNull(stringValue)) {
      return this;
    }
    label.setText(stringValue);
    removeAll();
    if (column == 1) {
      label.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
    } else if (column == 4) {
      var progressBar = new JProgressBar();
      progressBar.setValue(Integer.parseInt(stringValue));
      progressBar.setStringPainted(true);
      progressBar.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
      progressBar.setBorder(new EmptyBorder(1, 1, 1, 1));
      add(progressBar);
      return this;
    }
    var isLevel = Arrays.stream(ProficiencyLevel.values()).map(Enum::name).anyMatch(stringValue::startsWith);
    if (isLevel) {
      var gridBadLayout = new BoxLayout(this, BoxLayout.X_AXIS);
      setLayout(gridBadLayout);
      var levels = Arrays.stream(stringValue.split(" ")).toList();
      for (String level : levels) {
        var levelLabel = new JLabel();
        levelLabel.setLayout(new BorderLayout());
        levelLabel.setOpaque(true);
        levelLabel.setText(level);
        levelLabel.setFont(new Font(font.getName(), Font.BOLD, font.getSize() - 2));
        levelLabel.setBackground(LEVEL_COLOURS.get(level));
        levelLabel.setForeground(Color.WHITE);
        levelLabel.setBorder(new EmptyBorder(2, 5, 0, 5));
        add(levelLabel);
        add(Box.createRigidArea(new Dimension(5, 0)));
      }
    } else {
      add(label, BorderLayout.CENTER);
    }
    return this;
  }
}
