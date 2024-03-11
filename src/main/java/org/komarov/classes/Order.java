package org.komarov.classes;

import java.io.Serializable;
import java.time.Duration;
import java.util.Scanner;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.ArrayList;

@Builder
@Data
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;

    private LocalTime startTime;

    private ArrayList<Meal> meals;

    private String client;

    private String status;

    public int getTotalSum()  {
        int sum = 0;
        for(Meal meal :  meals) {
            sum += meal.getPrice();
        }
        return sum;
    }

    public Duration getAllMealsDuration() {
        LocalTime totalTime = LocalTime.of(0, 0);
        for (Meal meal : meals) {
            LocalTime currTime = meal.getCookingTime();
            totalTime = totalTime.plusHours(currTime.getHour())
                    .plusMinutes(currTime.getMinute())
                    .plusSeconds(currTime.getSecond());
        }
        return Duration.ofHours(totalTime.getHour()).plusMinutes(totalTime.getMinute())
                .plusSeconds(totalTime.getSecond());
    }

    private Duration elapsedTime() {
        return Duration.between(startTime, LocalTime.now());
    }

    public long remainedTime() {
        return getAllMealsDuration().minus(elapsedTime()).getSeconds();
    }

    public void addMeals(ArrayList<Meal> newMeals) {
        meals.addAll(newMeals);
    }
}
