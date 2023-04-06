package com.english.eva.repository;

import java.util.ArrayList;
import java.util.List;

import com.english.eva.entity.Word;
import com.english.eva.model.SearchParams;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class WordCustomRepositoryImpl implements WordCustomRepository {

  private static final String TEXT = "text";

  private final EntityManager entityManager;

  @Override
  public List<Word> findBySearchParams(SearchParams params) {
    var builder = entityManager.getCriteriaBuilder();
    var criteria = builder.createQuery(Word.class);
    var rootItem = criteria.from(Word.class);
    var predicates = new ArrayList<Predicate>();

    if (StringUtils.isNotBlank(params.getSearchKey())) {
      var predicate = builder.like(builder.lower(rootItem.get(TEXT)),
          "%" + StringUtils.lowerCase(params.getSearchKey() + "%"));
      predicates.add(predicate);
    }

    var levels = params.getLevels();
    if (CollectionUtils.isNotEmpty(levels)) {
      var join = rootItem.join("meaning");
      var predicate = builder.in(join.get("proficiencyLevel")).value(levels);
      predicates.add(predicate);
    }

    var statuses = params.getStatuses();
    if(CollectionUtils.isNotEmpty(statuses)) {
      var join = rootItem.join("meaning");
      var predicate = builder.in(join.get("learningStatus")).value(statuses);
      predicates.add(predicate);
    }

    criteria.distinct(true).where(predicates.toArray(Predicate[]::new));
    return entityManager.createQuery(criteria).getResultList();
  }
}
