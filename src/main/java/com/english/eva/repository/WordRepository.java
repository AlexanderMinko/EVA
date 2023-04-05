package com.english.eva.repository;

import com.english.eva.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<Word, Long>, WordCustomRepository {

}
