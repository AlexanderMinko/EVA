package com.english.eva.repository;

import java.util.List;
import java.util.Optional;

import com.english.eva.entity.Meaning;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeaningRepository extends JpaRepository<Meaning, Long> {

  List<Meaning> findByWordId(Long wordId);

}
