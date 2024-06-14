package com.english.eva.entity;

import java.util.Date;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PreUpdate;

public class MeaningEntityListener {

  @PersistenceContext
  private EntityManager entityManager;

  @PreUpdate
  public void onChildEntityUpdate(Meaning meaning) {
    var word = meaning.getWord();
    if (word != null) {
      word.setLastModified(new Date());
      entityManager.persist(word);
    }
  }

}
