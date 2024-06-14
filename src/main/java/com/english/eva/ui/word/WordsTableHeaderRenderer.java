package com.english.eva.ui.word;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang3.StringUtils;

public class WordsTableHeaderRenderer extends JPanel implements TableCellRenderer {

  private TableCellRenderer defaultRenderer;

  public WordsTableHeaderRenderer(TableCellRenderer defaultRenderer) {
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEtchedBorder());
    this.defaultRenderer = defaultRenderer;
  }

  @Override
  public Component getTableCellRendererComponent(
      JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    var component = defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

    if (component instanceof JLabel label && StringUtils.equalsAny(label.getText(), "Word", "Frequency")) {
      label.setIcon(null);
      var wordsTable = (WordsTableNew) table;
      printSortLabel(wordsTable.getSortingDetails(), label.getText(), label);
    }
    return component;
  }

  private static void printSortLabel(SortingDetails sortingDetails, String columnName, JLabel label) {
    if (!StringUtils.equals(columnName, sortingDetails.getColumnName())) {
      return;
    }
    var direction = sortingDetails.getDirection();
    if ("asc".equals(direction)) {
      label.setIcon(UIManager.getIcon("Table.ascendingSortIcon"));
    } else if ("desc".equals(direction)) {
      label.setIcon(UIManager.getIcon("Table.descendingSortIcon"));
    }
  }
}
