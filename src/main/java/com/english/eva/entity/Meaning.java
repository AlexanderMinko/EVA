package com.english.eva.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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

  @Enumerated(EnumType.STRING)
  @Column(name = "part_of_speech")
  private PartOfSpeech partOfSpeech;

  @Enumerated(EnumType.STRING)
  @Column(name = "proficiency_level")
  private ProficiencyLevel proficiencyLevel;

  @Enumerated(EnumType.STRING)
  @Column(name = "meaning_source")
  private MeaningSource meaningSource;

  @Enumerated(EnumType.STRING)
  @Column(name = "learning_status")
  private LearningStatus learningStatus;

  @Column(name = "description", length = 1024)
  private String description;

  @Column(name = "example", columnDefinition = "VARCHAR(2024)")
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "examples", joinColumns = @JoinColumn(name = "meaning_id"))
  private List<String> examples;

  @Column(name = "also")
  private String also;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "date_created", nullable = false, updatable = false)
  @CreatedDate
  private Date dateCreated;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_modified", nullable = false)
  @LastModifiedDate
  private Date lastModified;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "word_id")
  private Word word;
}
