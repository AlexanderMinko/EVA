package com.english.eva;

import com.english.eva.service.MeaningService;
import com.english.eva.service.WordService;
import com.english.eva.ui.panel.meaning.MeaningTree;
import com.english.eva.ui.panel.settings.SettingsPanel;
import com.english.eva.ui.panel.word.TableClickListener;
import com.english.eva.ui.panel.word.WordsTable;
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
  }
}
