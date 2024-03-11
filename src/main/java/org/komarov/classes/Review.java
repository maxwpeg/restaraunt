package org.komarov.classes;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    private Meal meal;

    private String text;

    private int score;
}
