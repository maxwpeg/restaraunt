package org.komarov.classes;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Review {

  private String text;

  private int score;
}
