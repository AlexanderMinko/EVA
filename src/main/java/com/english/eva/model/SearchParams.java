package com.english.eva.model;

import java.util.List;

import com.english.eva.entity.ProficiencyLevel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchParams {
  private String searchKey;
  private List<ProficiencyLevel> levels;
}
