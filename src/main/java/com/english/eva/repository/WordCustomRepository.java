package com.english.eva.repository;

import java.util.List;

import com.english.eva.entity.Word;
import com.english.eva.model.SearchParams;

public interface WordCustomRepository {

  List<Word> findBySearchParams(SearchParams params);

}
