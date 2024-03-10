package org.komarov.classes;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class Meal {

    private UUID id;

    private String name;

    private LocalTime cookingTime;

    private int price;

    private int amount;

}