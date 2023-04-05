package com.english.eva.repository;

import java.util.List;

import com.english.eva.entity.LearningStatus;
import com.english.eva.entity.Meaning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MeaningRepository extends JpaRepository<Meaning, Long> {

  List<Meaning> findByWordId(Long wordId);

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query(value = "UPDATE Meaning m SET m.learningStatus = :learningStatus WHERE m.id = :id")
  void updateLearningStatus(Long id, LearningStatus learningStatus);

}
