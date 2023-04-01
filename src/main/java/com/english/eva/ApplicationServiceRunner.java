package com.english.eva;

import com.english.eva.service.WordService;
import com.english.eva.ui.panel.VocabularyPanel;
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

  @Override
  public void run(ApplicationArguments args) throws Exception {
    VocabularyPanel.setWordService(wordService);
    WordsTable.setWordService(wordService);
  }
}
