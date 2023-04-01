package com.english.eva.ui.panel.word;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

public class PopClickListener extends MouseAdapter {

  private final JTable wordsTable;

  public PopClickListener(JTable wordsTable) {
    this.wordsTable = wordsTable;
  }

  public void mousePressed(MouseEvent event) {
    // selects the row at which point the mouse is clicked
    var point = event.getPoint();
    var currentRow = wordsTable.rowAtPoint(point);
    var currentColumn = wordsTable.columnAtPoint(point);
    wordsTable.setRowSelectionInterval(currentRow, currentRow);
    if (SwingUtilities.isRightMouseButton(event)) {
      var selectedWordId = Integer.parseInt((String) wordsTable.getModel().getValueAt(currentRow, 0));
      doPop(event, selectedWordId);
    }
  }

  private void doPop(MouseEvent e, Integer selectedWordId) {
    var popupMenu = new JPopupMenu();
    var addMeaningItem = new JMenuItem("Add meaning");
    addMeaningItem.addActionListener(event -> {
      System.out.println("NEW MODAL=" + selectedWordId);
    });
    popupMenu.add(addMeaningItem);
    popupMenu.show(e.getComponent(), e.getX(), e.getY());
  }
}
