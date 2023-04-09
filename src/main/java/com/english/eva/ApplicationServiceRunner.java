package com.english.eva;

import java.util.Date;
import java.util.List;

import com.english.eva.entity.LearningStatus;
import com.english.eva.entity.Meaning;
import com.english.eva.entity.MeaningSource;
import com.english.eva.entity.PartOfSpeech;
import com.english.eva.entity.ProficiencyLevel;
import com.english.eva.entity.Word;
import com.english.eva.service.MeaningService;
import com.english.eva.service.WordService;
import com.english.eva.ui.panel.meaning.MeaningTree;
import com.english.eva.ui.panel.meaning.TreeClickListener;
import com.english.eva.ui.panel.settings.SettingsPanel;
import com.english.eva.ui.panel.word.TableClickListener;
import com.english.eva.ui.panel.word.WordsTableNew;
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
    WordsTableNew.setWordService(wordService);
    TableClickListener.setWordService(wordService);
    TableClickListener.setMeaningService(meaningService);
    SettingsPanel.setWordService(wordService);
    MeaningTree.setMeaningService(meaningService);
    TreeClickListener.setMeaningService(meaningService);

//    wordService.saveReserve();
//    var path = Paths.get("/home/ming/English/words.json");
//    var data = new ObjectMapper().readValue(Files.readAllBytes(path), new TypeReference<List<Word>>() {});
//    System.out.println(data);
  }

  private void initFirstWord() {
    var dateCreated = new Date();
    var helloWord = Word.builder()
        .id(1L)
        .text("hello")
        .transcript("helˈəʊ")
        .frequency(275)
        .topic("communication")
        .dateCreated(dateCreated)
        .lastModified(dateCreated)
        .build();
    var helloMeaning = Meaning.builder()
        .id(1L)
        .target("hello (GREETING)")
        .description("used to greet someone")
        .meaningSource(MeaningSource.ENGLISH_PROFILE)
        .partOfSpeech(PartOfSpeech.EXCLAMATION)
        .proficiencyLevel(ProficiencyLevel.A1)
        .learningStatus(LearningStatus.KNOWN)
        .examples(List.of("Hello, Paul. I haven't seen you for ages.", "I just thought I'd call by and say hello.",
            "Hello Fatima, how are you? "))
        .dateCreated(dateCreated)
        .lastModified(dateCreated)
        .word(helloWord)
        .build();
    meaningService.save(helloMeaning);
  }
}
