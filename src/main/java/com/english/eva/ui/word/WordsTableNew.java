package com.english.eva.ui.word;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.swing.JTable;

import com.english.eva.entity.Word;
import com.english.eva.service.WordService;
import com.english.eva.ui.meaning.MeaningTree;
import lombok.Getter;
import lombok.Setter;

public class WordsTableNew extends JTable {

  private static WordService wordService;

  @Getter
  @Setter
  public SortingDetails sortingDetails = new SortingDetails();
  private WordTableModel wordTableModel;
  private List<Word> words;

  public WordsTableNew(MeaningTree meaningTree) {
    this.words = wordService.getAll();
    wordTableModel = new WordTableModel(this.words);
    setModel(wordTableModel);
    addMouseListener(new TableClickListener(this, meaningTree));
    setDefaultRenderer(Object.class, new WordTableCellRenderer());
    initColumnModel();
    getTableHeader().setDefaultRenderer(new WordsTableHeaderRenderer(getTableHeader().getDefaultRenderer()));
    getTableHeader().addMouseListener(new WordsHeaderClickListener(this));
  }

  private void initColumnModel() {
    getColumnModel().getColumn(WordTableModel.HIDDEN_WORD_ID).setMinWidth(0);
    getColumnModel().getColumn(WordTableModel.HIDDEN_WORD_ID).setMaxWidth(0);
    getColumnModel().getColumn(WordTableModel.HIDDEN_WORD_ID).setResizable(false);
    getColumnModel().getColumn(WordTableModel.COLUMN_FREQUENCY).setPreferredWidth(90);
    getColumnModel().getColumn(WordTableModel.COLUMN_FREQUENCY).setMaxWidth(120);
    getColumnModel().getColumn(WordTableModel.COLUMN_PROGRESS).setPreferredWidth(110);
    getColumnModel().getColumn(WordTableModel.COLUMN_PROGRESS).setMaxWidth(110);
  }

  public void sortData() {
    var comparator = getSortingComparator();
    if (Objects.nonNull(comparator)) {
      words = words.stream().sorted(comparator).toList();
      wordTableModel.setData(words);
      initColumnModel();
    }
  }

  private Comparator<Word> getSortingComparator() {
    Comparator<Word> comparator = null;
    if ("Word".equals(sortingDetails.getColumnName())) {
      comparator = Comparator.comparing(Word::getText, String.CASE_INSENSITIVE_ORDER);
    } else if ("Frequency".equals(sortingDetails.getColumnName())) {
      comparator = Comparator.comparing(Word::getFrequency, Comparator.naturalOrder());
    }
    if (Objects.isNull(comparator)) {
      return null;
    }
    if ("desc".equals(sortingDetails.getDirection())) {
      comparator = comparator.reversed();
    }
    return comparator;
  }

  public static void setWordService(WordService wordService) {
    WordsTableNew.wordService = wordService;
  }

  public void reloadTable() {
    var wordsIds = words.stream().map(Word::getId).collect(Collectors.toSet());
    this.words = wordService.getByWordIds(wordsIds).stream().sorted().toList();
    wordTableModel.setData(words);
    initColumnModel();
  }

  public void reloadTable(Word word) {
    var wordsIds = words.stream().map(Word::getId).collect(Collectors.toSet());
    var currentWords = wordService.getByWordIds(wordsIds);
    currentWords.add(word);
    this.words = currentWords.stream().sorted().collect(Collectors.toList());
    wordTableModel.setData(this.words);
    initColumnModel();
  }

  public void reloadTable(List<Word> words) {
    this.words = words.stream().sorted().toList();
    wordTableModel.setData(this.words);
    initColumnModel();
  }
}
