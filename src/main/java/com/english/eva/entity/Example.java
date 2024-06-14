package com.english.eva.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "example")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Example {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "text", length = 1024)
  private String text;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "meaning_id")
  private Meaning meaning;

  public Example(String text, Meaning meaning) {
    this.text = text;
    this.meaning = meaning;
  }

}
