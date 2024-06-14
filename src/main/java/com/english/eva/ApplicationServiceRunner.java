package com.english.eva;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.english.eva.entity.Example;
import com.english.eva.entity.LearningStatus;
import com.english.eva.entity.Meaning;
import com.english.eva.entity.MeaningSource;
import com.english.eva.entity.PartOfSpeech;
import com.english.eva.entity.ProficiencyLevel;
import com.english.eva.entity.Word;
import com.english.eva.repository.ExampleRepository;
import com.english.eva.repository.MeaningRepository;
import com.english.eva.repository.WordRepository;
import com.english.eva.service.MeaningService;
import com.english.eva.service.WordService;
import com.english.eva.ui.meaning.MeaningTree;
import com.english.eva.ui.meaning.TreeClickListener;
import com.english.eva.ui.settings.SettingsPanel;
import com.english.eva.ui.word.ExperimentalHandler;
import com.english.eva.ui.word.TableClickListener;
import com.english.eva.ui.word.WordsTableNew;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceRunner implements ApplicationRunner {

    private final WordService wordService;
    private final MeaningService meaningService;
    private final WordRepository wordRepository;
    private final MeaningRepository meaningRepository;
    private final ExampleRepository exampleRepository;

    @Override
    public void run(ApplicationArguments args) {
        WordsTableNew.setWordService(wordService);
        TableClickListener.setWordService(wordService);
        TableClickListener.setMeaningService(meaningService);
        SettingsPanel.setWordService(wordService);
        MeaningTree.setMeaningService(meaningService);
        TreeClickListener.setWordService(wordService);
        TreeClickListener.setMeaningService(meaningService);
        ExperimentalHandler.setWordService(wordService);
        ExperimentalHandler.setMeaningService(meaningService);
        log.info("Meanings: [{}]", meaningRepository.findAll().size());
//        retrieveFreqWords();
    }

    private final WebClient webClient;

    void retrieveFreqWords() {
//        var response = webClient.get()
//                .uri("https://lb.dioco.io/base_dict_getAllWordsForLang?lang_G=en&numWords=100000")
//                .retrieve()
//                .bodyToMono(JsonResponse.class)
//                .map(resp -> resp.data().words())
//                .map(dataWords -> dataWords.stream()
//                        .map(DataWord::text)
//                        .collect(Collectors.toList()))
//                .block();
//        var wordsMap = new HashMap<String, Integer>();
//        for (int i = 0; i < response.size(); i++) {
//            wordsMap.put(response.get(i), i + 1);
//        }

        var words = wordRepository.findAll()
                .stream()
                .filter(word -> word.getFrequency() == 0)
//                .filter(word -> response.contains(word.getText()))
                .toList();
        System.out.println(words.size());
//        wordRepository.saveAll(words);
    }


    record DataWord(String text) {
    }

    record WordData(List<DataWord> words) {
    }

    record JsonResponse(String status, WordData data) {
    }

    private static final String WORDS_PATH = "/home/ming/English/EngProfile/words.json";

    @SneakyThrows
    void examplesMigration() {
        var mapper = new ObjectMapper();
        var path = Paths.get(WORDS_PATH);
        var data = mapper.readValue(Files.readAllBytes(path), new TypeReference<List<WordImportDto>>() {
        });
        data.sort(Comparator.comparing(WordImportDto::getText, String.CASE_INSENSITIVE_ORDER).reversed());

        var savedMeanings = meaningRepository.findAll();
        savedMeanings.forEach(savedMeaning -> {
            data.stream()
                    .flatMap(wordImportDto -> wordImportDto.getMeanings().stream())
                    .filter(meaning -> StringUtils.equals(meaning.getTarget(), savedMeaning.getTarget()))
                    .filter(meaning -> meaning.getPartOfSpeech() == savedMeaning.getPartOfSpeech())
                    .forEach(meaning -> {
                        var examples = meaning.getExamples().stream()
                                .map(exampleText -> new Example(exampleText, savedMeaning)).toList();
                        var savedExamples = exampleRepository.saveAll(examples);
                        savedMeaning.setExamples(savedExamples);
                        meaningRepository.save(savedMeaning);
                        log.info("Meaning with ID=[{}] has been updated with examples=[{}]",
                                savedMeaning.getId(),
                                savedMeaning.getExamples());
                    });
        });

//        for (WordImportDto wordDto : data) {
//            var word = new Word();
//            var dateCreated = new Date();
//            word.setText(wordDto.getText());
//            word.setTranscript(wordDto.getMeanings().stream().findFirst()
//                    .map(MeaningImportDto::getTranscript)
//                    .map(transcript -> transcript.replaceAll("/", ""))
//                    .orElse(null));
//            word.setFrequency(0);
//            word.setDateCreated(dateCreated);
//            word.setLastModified(dateCreated);
//            word.setMeanings(new ArrayList<>());
//            var meaningsDto = wordDto.getMeanings();
//            for (MeaningImportDto meaningImportDto : meaningsDto) {
//                var meaning = new Meaning();
//                meaning.setTarget(meaningImportDto.getTarget());
//                meaning.setTranscript(meaningImportDto.getTranscript());
//                meaning.setTopic(meaningImportDto.getTopic());
//                meaning.setProficiencyLevel(meaningImportDto.getProficiencyLevel());
//                meaning.setLearningStatus(meaningImportDto.getLearningStatus());
//                meaning.setMeaningSource(meaningImportDto.getMeaningSource());
//                meaning.setDescription(meaningImportDto.getDescription());
//                meaning.setPartOfSpeech(Objects.isNull(meaningImportDto.getPartOfSpeech())
//                        ? PartOfSpeech.NOUN
//                        : meaningImportDto.getPartOfSpeech());
//                meaning.setDateCreated(dateCreated);
//                meaning.setLastModified(dateCreated);
//                var examples = meaningImportDto.getExamples().stream().map(text -> new Example(text, meaning)).toList();
//                meaning.setWord(word);
//                meaning.setExamples(examples);
//                word.getMeanings().add(meaning);
//            }
//            words.add(word);
//        }
//        words.sort(Comparator.comparing(Word::getText));
//        wordRepository.saveAll(words);
    }

    private static void showduplicates(List<WordImportDto> data) {
        var duplicates = new HashMap<String, Integer>();
        data.forEach(word -> {
            word.getMeanings().forEach(meaning -> {
                var key = meaning.getTarget();
                if (duplicates.containsKey(key)) {
                    duplicates.put(meaning.getTarget(), duplicates.get(key) + 1);
                } else {
                    duplicates.put(key, 1);
                }

            });
        });
        duplicates.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .forEach(entry -> System.out.println(entry.getKey() + " : " + entry.getValue()));
    }

//    void showExamples() {
//        meaningRepository.findAll().forEach(meaning -> {
//            var oldExamples = meaning.getExamplesOld();
//            var newExamples = oldExamples.stream()
//                    .map(oldExample -> new Example(null, oldExample, meaning)).toList();
//            meaning.setExamples(newExamples);
//            meaningRepository.save(meaning);
//            log.info("saved examples: ID=[{}], Examples=[{}]", meaning.getId(), newExamples);
//        });
//    }

    //------------------------------------------------------------------------------------------------------------------

    private final static String RESULT_PATH = "/home/ming/English/EngProfile/words.json";
    private final static String SUMMARY_PATH_RESULT = "/home/ming/English/EngProfile/meaning_summary.json";
    private final static String BACKUP_WORDS = "/home/ming/English/words.json";

    @SneakyThrows
    private void addBackupWords() {
        var mapper = new ObjectMapper();
        var path = Paths.get(BACKUP_WORDS);
        var data = mapper.readValue(Files.readAllBytes(path), new TypeReference<List<WordBackup>>() {
        });
        var words = wordRepository.findAll().stream().collect(Collectors.toMap(Word::getText, Function.identity()));
        var dateCreated = new Date();
        var backupMeaning = data.stream().flatMap(backupWord -> backupWord.getMeaning().stream()).collect(Collectors.toList());
        var existingMeanings = words.values().stream().flatMap(existWord -> existWord.getMeanings().stream()).collect(Collectors.toList());

        for (MeaningBackup meaningBackup : backupMeaning) {
            for (Meaning existingMeaning : existingMeanings) {
                if (existingMeaning.getTarget().equalsIgnoreCase(meaningBackup.getTarget())) {
                    existingMeaning.setLearningStatus(meaningBackup.getLearningStatus());
                    meaningRepository.save(existingMeaning);
                    log.info("UPDATED [{}]", existingMeaning);
                }
            }

            for (WordBackup wordBackup : data) {
                var existingWord = words.get(wordBackup.getText());
                if (Objects.isNull(existingWord)) {
//        log.error("NULL. [{}]", wordBackup.getText());
//        var word = new Word();
//        word.setText(wordBackup.getText());
//        word.setTranscript(wordBackup.getTranscript());
//        word.setFrequency(wordBackup.getFrequency());
//        word.setDateCreated(dateCreated);
//        word.setLastModified(dateCreated);
//        word.setMeanings(new ArrayList<>());
//        var backupMeanings = wordBackup.getMeaning();
//        for (MeaningBackup backupMeaning : backupMeanings) {
//          var meaning = new Meaning();
//          var partOfSpeech = backupMeaning.getPartOfSpeech().equalsIgnoreCase("PHASE")
//              ? PartOfSpeech.PHRASE
//              : PartOfSpeech.findByLabel(backupMeaning.getPartOfSpeech());
//          meaning.setTarget(backupMeaning.getTarget());
//          meaning.setTranscript(wordBackup.getTranscript());
//          meaning.setTopic(backupMeaning.getTopic());
//          meaning.setProficiencyLevel(backupMeaning.getProficiencyLevel());
//          meaning.setLearningStatus(backupMeaning.getLearningStatus());
//          meaning.setMeaningSource(backupMeaning.getMeaningSource());
//          meaning.setDescription(backupMeaning.getDescription());
//          meaning.setPartOfSpeech(partOfSpeech);
//          meaning.setDateCreated(dateCreated);
//          meaning.setLastModified(dateCreated);
//          var examples = backupMeaning.getExamples().stream().map(text -> new Example(text, meaning)).toList();
//          meaning.setWord(word);
//          meaning.setExamples(examples);
//          word.getMeanings().add(meaning);
                }
            }
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class WordBackup {

        private String text;
        private String transcript;
        private Integer frequency;
        private List<MeaningBackup> meaning = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class MeaningBackup {
        private String target;
        private String topic;
        private String partOfSpeech;
        private ProficiencyLevel proficiencyLevel;
        private MeaningSource meaningSource;
        private LearningStatus learningStatus;
        private String description;
        private List<String> examples;
    }

    @SneakyThrows
    private void updateMeanings() {
        var mapper = new ObjectMapper();
        var path = Paths.get(SUMMARY_PATH_RESULT);
        var data = mapper.readValue(Files.readAllBytes(path), new TypeReference<List<MeaningSummary>>() {
        });
        var meanings = meaningRepository.findAll();
        for (MeaningSummary summary : data) {
            var meaningList = meanings.stream()
                    .filter(currentMeaning -> {
                        var meaningTarget = currentMeaning.getTarget();
                        var summaryTarget = summary.getTarget();
                        var summaryGuidWord = summary.getGuidWord();
                        var result = meaningTarget.contains(summaryTarget);
//            if (meaningTarget.contains("(")) {
//              result = result && meaningTarget.contains(summaryGuidWord);
//            }
                        return result;
                    })
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(meaningList)) {
                log.error("Meaning not found for target: [{}] and guidword [{}]", summary.getTarget(), summary.getGuidWord());
                continue;
            }
            for (Meaning meaning : meaningList) {
                if (StringUtils.isNotBlank(summary.getPartOfSpeech()) &&
                        !summary.getPartOfSpeech().equalsIgnoreCase(meaning.getPartOfSpeech().name())) {
                    System.out.println(summary);
                    System.out.println(meaning);
                    meaning.setPartOfSpeech(PartOfSpeech.findByLabel(summary.getPartOfSpeech()));
                    log.info("Part of speech does not match. Summary: [{}], meaning: [{}]", summary, meaning);
//          meaningRepository.save(meaning);
                }
            }
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class MeaningSummary {
        private String target;
        private String guidWord;
        private String partOfSpeech;
    }

    @SneakyThrows
    private void importWords() {
        var mapper = new ObjectMapper();
        var path = Paths.get(RESULT_PATH);
        var data = mapper.readValue(Files.readAllBytes(path), new TypeReference<List<WordImportDto>>() {
        });
        var words = new ArrayList<Word>();
        data.sort(Comparator.comparing(WordImportDto::getText, String.CASE_INSENSITIVE_ORDER).reversed());
        for (WordImportDto wordDto : data) {
            var word = new Word();
            var dateCreated = new Date();
            word.setText(wordDto.getText());
            word.setTranscript(wordDto.getMeanings().stream().findFirst()
                    .map(MeaningImportDto::getTranscript)
                    .map(transcript -> transcript.replaceAll("/", ""))
                    .orElse(null));
            word.setFrequency(0);
            word.setDateCreated(dateCreated);
            word.setLastModified(dateCreated);
            word.setMeanings(new ArrayList<>());
            var meaningsDto = wordDto.getMeanings();
            for (MeaningImportDto meaningImportDto : meaningsDto) {
                var meaning = new Meaning();
                meaning.setTarget(meaningImportDto.getTarget());
                meaning.setTranscript(meaningImportDto.getTranscript());
                meaning.setTopic(meaningImportDto.getTopic());
                meaning.setProficiencyLevel(meaningImportDto.getProficiencyLevel());
                meaning.setLearningStatus(meaningImportDto.getLearningStatus());
                meaning.setMeaningSource(meaningImportDto.getMeaningSource());
                meaning.setDescription(meaningImportDto.getDescription());
                meaning.setPartOfSpeech(Objects.isNull(meaningImportDto.getPartOfSpeech())
                        ? PartOfSpeech.NOUN
                        : meaningImportDto.getPartOfSpeech());
                meaning.setDateCreated(dateCreated);
                meaning.setLastModified(dateCreated);
                var examples = meaningImportDto.getExamples().stream().map(text -> new Example(text, meaning)).toList();
                meaning.setWord(word);
                meaning.setExamples(examples);
                word.getMeanings().add(meaning);
            }
            words.add(word);
        }
        words.sort(Comparator.comparing(Word::getText));
        wordRepository.saveAll(words);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class WordImportDto {
        private String text;
        private List<MeaningImportDto> meanings = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class MeaningImportDto {
        private String target;
        private String transcript;
        private String topic;
        private PartOfSpeech partOfSpeech;
        private ProficiencyLevel proficiencyLevel;
        private MeaningSource meaningSource;
        private LearningStatus learningStatus;
        private String description;
        private List<String> examples;
    }

    private void initFirstWord() {
//    var dateCreated = new Date();
//    var helloWord = Word.builder()
//        .id(1L)
//        .text("hello")
//        .transcript("helˈəʊ")
//        .frequency(275)
//        .topic("communication")
//        .dateCreated(dateCreated)
//        .lastModified(dateCreated)
//        .build();
//    var helloMeaning = Meaning.builder()
//        .id(1L)
//        .target("hello (GREETING)")
//        .description("used to greet someone")
//        .meaningSource(MeaningSource.ENGLISH_PROFILE)
//        .partOfSpeech(PartOfSpeech.EXCLAMATION)
//        .proficiencyLevel(ProficiencyLevel.A1)
//        .learningStatus(LearningStatus.KNOWN)
//        .examples(List.of("Hello, Paul. I haven't seen you for ages.", "I just thought I'd call by and say hello.",
//            "Hello Fatima, how are you? "))
//        .dateCreated(dateCreated)
//        .lastModified(dateCreated)
//        .word(helloWord)
//        .build();
//    meaningService.save(helloMeaning);
    }
}
