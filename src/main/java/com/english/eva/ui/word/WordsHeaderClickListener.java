package com.english.eva.ui.word;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WordsHeaderClickListener extends MouseAdapter {

  private final WordsTableNew wordsTable;

  public WordsHeaderClickListener(WordsTableNew wordsTable) {
    this.wordsTable = wordsTable;
  }

  @Override
  public void mousePressed(MouseEvent event) {
    var point = event.getPoint();
    var column = wordsTable.columnAtPoint(point);
    switch (column) {
      case WordTableModel.COLUMN_WORD -> setSortingDetails("Word");
      case WordTableModel.COLUMN_FREQUENCY -> setSortingDetails("Frequency");
      default -> {}
    }
  }

  private void setSortingDetails(String columnName) {
    var wordSortingDetails = wordsTable.getSortingDetails();
    if (!columnName.equals(wordSortingDetails.getColumnName())) {
      wordSortingDetails.setDirection("asc");
    } else if ("asc".equals(wordSortingDetails.getDirection())) {
      wordSortingDetails.setDirection("desc");
    } else {
      wordSortingDetails.setDirection("asc");
    }
    wordSortingDetails.setColumnName(columnName);
    wordsTable.sortData();
  }
}
