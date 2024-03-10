package org.komarov;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import org.komarov.classes.Administrator;
import org.komarov.classes.Meal;
import org.komarov.classes.Order;
import org.komarov.classes.Order_System;

public class Main {

  public static void main(String[] args) {
    Meal meal1 = Meal.builder().price(12).cookingTime(LocalTime.of(0,0, 12)).name("aaa").amount(1).build();
    Meal meal2 = Meal.builder().price(2).cookingTime(LocalTime.of(0,0, 3)).name("bbb").amount(22).build();
    Meal meal3 = Meal.builder().price(2).cookingTime(LocalTime.of(0,0, 6)).name("ccc").amount(22).build();
    ArrayList<Meal> meals = new ArrayList<>();
    meals.add(meal1);
    meals.add(meal2);
    meals.add(meal3);
    Order_System orderSystem = Order_System.builder()
        ._administrators(new ArrayList<Administrator>())
        .orders(new ArrayList<Order>())
        .meals(meals)
        .executorService(Executors.newFixedThreadPool(3))
        .build();
    orderSystem.createOrder("Ivan");
    orderSystem.addMealsToOrder("Ivan");
    orderSystem.getExecutorService().shutdown();
  }

}