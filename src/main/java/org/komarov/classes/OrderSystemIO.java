package org.komarov.classes;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class OrderSystemIO {

    public static void saveToFile(OrderSystem orderSystem, String filename) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename))) {
            outputStream.writeObject(orderSystem.getAdministrators());
            outputStream.writeObject(orderSystem.getMeals());
            outputStream.writeObject(orderSystem.getOrders());
            outputStream.writeObject(orderSystem.getTotalSum());
            outputStream.writeObject(orderSystem.getReviews());
            System.out.println("Данные успешно сохранены в файл " + filename);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении данных в файл: " + e.getMessage());
        }
    }
    public static OrderSystem loadFromFile(String filename) {
        OrderSystem orderSystem = null;
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename))) {
            ArrayList<Administrator> administrators = (ArrayList<Administrator>) inputStream.readObject();
            ArrayList<Meal> meals = (ArrayList<Meal>) inputStream.readObject();
            ArrayList<Order> orders = (ArrayList<Order>) inputStream.readObject();
            int totalSum = (int) inputStream.readObject();
            ArrayList<Review> reviews = (ArrayList<Review>) inputStream.readObject();
            orderSystem = OrderSystem
                    .builder()
                        .administrators(administrators)
                            .executorService(Executors.newFixedThreadPool(3))
                                .meals(meals)
                                        .orders(orders)
                                                .reviews(reviews)
                                                        .totalSum(totalSum)
                                                            .build();
            System.out.println("Данные успешно загружены из файла " + filename);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка при загрузке данных из файла: " + e.getMessage());
        }
        return orderSystem;
    }
}
