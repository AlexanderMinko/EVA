package com.english.eva.ui.word;

import java.util.List;
import java.util.stream.Collectors;
import javax.swing.table.AbstractTableModel;

import com.english.eva.entity.Word;

public class WordTableModel extends AbstractTableModel {
  public static final int COLUMN_WORD = 0;
  public static final int COLUMN_TRANSCRIPT = 1;
  public static final int COLUMN_FREQUENCY = 2;
  public static final int COLUMN_PROGRESS = 3;
  public static final int COLUMN_LEVELS = 4;
  public static final int COLUMN_PARTS_OF_SPEECH = 5;
  public static final int HIDDEN_WORD_ID = 6;

  private final String[] columnNames = {"Word", "Transcript", "Frequency", "Progress", "Levels", "Parts of speech", "ID"};
  private List<WordDto> wordDtoList;

  public WordTableModel(List<Word> words) {
    this.wordDtoList = words.stream().map(WordDto::new).collect(Collectors.toList());

    int indexCount = 1;
    for (WordDto word : this.wordDtoList) {
      word.setIndex(indexCount++);
    }
  }

  public void setData(List<Word> words) {
    this.wordDtoList = words.stream().map(WordDto::new).collect(Collectors.toList());
//    justifyRows(0, getRowCount());
    fireTableStructureChanged();
  }

  @Override
  public int getRowCount() {
    return wordDtoList.size();
  }

  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  @Override
  public String getColumnName(int columnIndex) {
    return columnNames[columnIndex];
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    if (wordDtoList.isEmpty()) {
      return Object.class;
    }
    return getValueAt(0, columnIndex).getClass();
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    var wordDto = wordDtoList.get(rowIndex);
    return switch (columnIndex) {
      case COLUMN_WORD -> wordDto.getText();
      case COLUMN_TRANSCRIPT -> wordDto.getTranscript();
      case COLUMN_FREQUENCY -> wordDto.getFrequency();
      case COLUMN_PROGRESS -> wordDto.getProgress();
      case COLUMN_LEVELS -> wordDto.getLevels();
      case COLUMN_PARTS_OF_SPEECH -> wordDto.getPartsOfSpeech();
      case HIDDEN_WORD_ID -> wordDto.getId();
      default -> throw new IllegalStateException("Unexpected value: " + columnIndex);
    };
  }

  public void removeRow(int row) {
    wordDtoList.remove(row);
    fireTableRowsDeleted(row, row);
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return true;
  }
}
