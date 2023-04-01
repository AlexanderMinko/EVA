package com.english.eva.entity;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "meaning")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Meaning {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "target")
  private String target;

  @Column(name = "part_of_speech")
  private String partOfSpeech;

  @Enumerated(EnumType.STRING)
  @Column(name = "proficiency_level")
  private ProficiencyLevel proficiencyLevel;

  @Enumerated(EnumType.STRING)
  @Column(name = "meaning_source")
  private MeaningSource meaningSource;

  @Column(name = "description")
  private String description;

  @Column(name = "example")
  @ElementCollection
  @CollectionTable(name = "examples", joinColumns = @JoinColumn(name = "meaning_id"))
  private List<String> examples;

  @Column(name = "also")
  private String also;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "word_id")
  private Word word;
}
