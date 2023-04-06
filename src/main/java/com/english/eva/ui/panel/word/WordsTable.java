package com.english.eva.ui.panel.word;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.english.eva.entity.LearningStatus;
import com.english.eva.entity.Meaning;
import com.english.eva.entity.PartOfSpeech;
import com.english.eva.entity.Word;
import com.english.eva.service.WordService;
import com.english.eva.ui.panel.meaning.MeaningTree;
import org.apache.commons.lang3.StringUtils;

public class WordsTable extends JTable {

  private static WordService wordService;

  public static void setWordService(WordService wordService) {
    WordsTable.wordService = wordService;
  }

  private static final String[] COLUMNS = new String[] {
      "ID", "Word", "Translation", "Frequency", "Progress", "Levels", "Parts of speech", "Topic"
  };

  private final DefaultTableModel tableModel;
  private List<Word> words;

  public WordsTable(MeaningTree meaningTree) {
    words = wordService.getAll();
    tableModel = new DefaultTableModel(getWordsData(), COLUMNS);
    setModel(tableModel);

    initColumnModel();

    addMouseListener(new TableClickListener(this, meaningTree));
    setDefaultRenderer(Object.class, new WordTableCellRenderer());
    setAutoCreateRowSorter(true);
    var sorter = (TableRowSorter<TableModel>) getRowSorter();
    sorter.setComparator(3, (o1, o2) -> {
      Integer freq1 = Integer.parseInt((String) o1);
      Integer freq2 = Integer.parseInt((String) o2);
      return freq1.compareTo(freq2);
    });
  }

  private void initColumnModel() {
    getColumnModel().getColumn(0).setMinWidth(0);
    getColumnModel().getColumn(0).setMaxWidth(0);
    getColumnModel().getColumn(0).setResizable(false);
    getColumnModel().getColumn(3).setMaxWidth(90);
  }

  public void reloadTable() {
    var wordsIds = words.stream().map(Word::getId).collect(Collectors.toSet());
    this.words = wordService.getByWordIds(wordsIds);
    tableModel.setDataVector(getWordsData(), COLUMNS);
    initColumnModel();
  }

  public void reloadTable(List<Word> words) {
    this.words = words;
    tableModel.setDataVector(getWordsData(), COLUMNS);
    initColumnModel();
  }

  private String[][] getWordsData() {
    return words.stream()
        .map(word -> {
          var levels = word.getMeaning().stream()
              .map(Meaning::getProficiencyLevel)
              .map(Enum::name)
              .distinct().sorted(Comparator.naturalOrder()).collect(Collectors.joining(StringUtils.SPACE));
          var partsOfSpeech = word.getMeaning().stream()
              .map(Meaning::getPartOfSpeech)
              .map(PartOfSpeech::getLabel)
              .distinct().collect(Collectors.joining(","));
          var knownCount = word.getMeaning().stream()
              .filter(meaning -> meaning.getLearningStatus() == LearningStatus.KNOWN ||
                  meaning.getLearningStatus() == LearningStatus.LEARNT).count();
          var progress = (int) (((double) knownCount / word.getMeaning().size()) * 100);
          return new String[] {
              String.valueOf(word.getId()),
              word.getText(),
              "[ " + word.getTranscript() + " ]",
              String.valueOf(word.getFrequency()),
              String.valueOf(progress),
              levels,
              partsOfSpeech,
              word.getTopic()
          };
        })
        .toArray(String[][]::new);
  }

//  private String[][] getWordsData() {
//    return getWordsData(wordService.getAll());
//  }

  //  private static boolean isEmpty(EnvironmentInfo env) {
//    return Stream.of(
//            env.getName(), env.getRegion().name(), env.getApiUrl(),
//            env.getKeycloakUrl(), env.getUsername(), env.getPassword())
//        .anyMatch(StringUtils::isBlank);
//  }
}
