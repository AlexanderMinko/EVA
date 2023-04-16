package com.english.eva.entity;

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
import lombok.ToString;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Table(name = "words")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Word implements Comparable<Word> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "text")
  private String text; //StringUtils.substringBefore("Stack Overflow - A place to ask stuff", " - ")

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

  @ToString.Exclude
  @OneToMany(mappedBy = "word", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Meaning> meaning;

  @Override
  public int compareTo(Word word) {
    return word.getDateCreated().compareTo(dateCreated);
  }
}
