package com.english.eva.ui.panel.word;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.english.eva.service.WordService;

public class WordsTable extends JTable {

  private static WordService wordService;
  public static void setWordService(WordService wordService) {
    WordsTable.wordService = wordService;
  }

  private static final String[] COLUMNS = new String[] {
      "ID", "Word", "Translation", "Frequency", "Levels", "Parts of speech", "Topic"
  };

  public WordsTable() {
    setModel(new DefaultTableModel(getWordsData(), COLUMNS));
    getColumnModel().getColumn(0).setMinWidth(0);
    getColumnModel().getColumn(0).setMaxWidth(0);
    getColumnModel().getColumn(0).setResizable(false);

    getColumnModel().getColumn(1).setPreferredWidth(50);
    getColumnModel().getColumn(2).setPreferredWidth(50);
    getColumnModel().getColumn(3).setPreferredWidth(50);

    setShowHorizontalLines(true);
    setShowVerticalLines(true);
    setAutoCreateRowSorter(true);
    getModel().addTableModelListener(event -> {
      int row = event.getFirstRow();
      int column = event.getColumn();
      TableModel model = (TableModel) event.getSource();
      String data = (String) model.getValueAt(row, column);
      var columnName = model.getColumnName(column);
      System.out.println(data);
      System.out.println(columnName);
      System.out.println("--------");
    });

    addMouseListener(new PopClickListener(this));
  }

  private String[][] getWordsData() {
    var words = wordService.getAll();
    return words.stream()
        .map(word -> new String[] {
            String.valueOf(word.getId()),
            word.getText(),
            word.getTranscript(),
            String.valueOf(word.getFrequency()),
            word.getTopic()
        })
        .toArray(String[][]::new);
  }
}
