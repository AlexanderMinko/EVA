package com.english.eva;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.english.eva.entity.Word;
import com.english.eva.service.MeaningService;
import com.english.eva.service.WordService;
import com.english.eva.ui.panel.meaning.MeaningTree;
import com.english.eva.ui.panel.meaning.TreeClickListener;
import com.english.eva.ui.panel.settings.SettingsPanel;
import com.english.eva.ui.panel.word.TableClickListener;
import com.english.eva.ui.panel.word.WordsTable;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceRunner implements ApplicationRunner {

  private final WordService wordService;
  private final MeaningService meaningService;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    WordsTable.setWordService(wordService);
    TableClickListener.setWordService(wordService);
    TableClickListener.setMeaningService(meaningService);
    SettingsPanel.setWordService(wordService);
    MeaningTree.setMeaningService(meaningService);
    TreeClickListener.setMeaningService(meaningService);
//    var path = Paths.get("/home/ming/English/words.json");
//    var data = new ObjectMapper().readValue(Files.readAllBytes(path), new TypeReference<List<Word>>() {});
//    System.out.println(data);
  }
}
