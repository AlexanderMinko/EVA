package com.english.eva.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.english.eva.entity.Meaning;
import com.english.eva.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<Word, Long>, WordCustomRepository {

  List<Word> findByIdIn(Set<Long> ids);

  Optional<Word> findByMeaningsIn(List<Meaning> meanings);

  boolean existsByText(String text);
}
