package com.english.eva.repository;

import java.util.List;
import java.util.Set;

import com.english.eva.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<Word, Long>, WordCustomRepository {

  List<Word> findByIdIn(Set<Long> ids);

  boolean existsByText(String text);
}
