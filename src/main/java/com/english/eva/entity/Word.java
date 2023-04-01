package com.english.eva.entity;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "words")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Word {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "text")
  private String text;

  @Column(name = "transcript")
  private String transcript;

  @Column(name = "frequency")
  private Integer frequency;

  @Column(name = "topic")
  private String topic;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "date_created", nullable = false, updatable = false)
  @CreatedDate
  private Date dateCreated;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_modified", nullable = false)
  @LastModifiedDate
  private Date lastModified;

  @OneToMany(mappedBy = "word", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Meaning> meaning;

}