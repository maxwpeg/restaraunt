package org.komarov.classes;

import java.io.Serializable;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class Meal implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;

    private String name;

    private LocalTime cookingTime;

    private int price;

    private int amount;

}