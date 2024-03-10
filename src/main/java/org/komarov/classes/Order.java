package org.komarov.classes;

import java.time.Duration;
import java.util.Scanner;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.ArrayList;

@Builder
@Data
public class Order {

    private UUID id;

    private LocalTime startTime;

    private ArrayList<Meal> meals;

    private String client;

    private volatile String status;

    public Duration getAllMealsDuration() {
        LocalTime totalTime = LocalTime.of(0,0);
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

    public boolean isTimeUp() {
        return elapsedTime().compareTo(getAllMealsDuration()) >= 0;
    }

    public String getStatus() {
        if (elapsedTime().compareTo(getAllMealsDuration()) < 0) {
           return "Готовится";
        }
        return "Готов";
    }

    public void pay() {
        while (true) {
            int i = UserIO.getInt("номер банковского счета(9 цифр)");
            if (i < 100000000 || i > 999999999) {
                System.out.println("Неверный ввод, повторите попытку.");
                continue;
            }
            break;
        }
        System.out.println("Заказ был записан на ваш счет. Спасибо, что были у нас!");
        setStatus("Оплачен");

    }

    public Review collectReview() {
        int score;
        while (true) {
            score = UserIO.getInt("оценку(1-5)");
            if (score < 1 || score > 5) {
                System.out.println("Неверный ввод, повторите попытку.");
                continue;
            }
            break;
        }
        System.out.println("Оставьте отзыв о работе(до 3000 символов): ");
        Scanner in = new Scanner(System.in);
        String reviewText = in.nextLine();
        return Review.builder().text(reviewText).score(score).build();
    }
    public void setStatus(String status) {
        this.status = status;
//        System.out.println(this);
        if (status.equals("Готов")) {
            pay();
        }
        if (status.equals("Оплачен")) {
            collectReview();
        }
    }

    public void addMeals(ArrayList<Meal> newMeals) {
        meals.addAll(newMeals);
    }
}
