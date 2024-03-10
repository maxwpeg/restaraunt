package org.komarov.classes;

import static org.komarov.classes.UserIO.getInt;
import static org.komarov.classes.UserIO.getLine;
import static org.komarov.classes.UserIO.getMinutes;
import static org.komarov.classes.UserIO.ERROR;
import static org.komarov.classes.UserIO.retry;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Order_System {

    private ArrayList<Administrator> _administrators;

    private List<Order> orders;

    private ArrayList<Meal> meals;

    private ExecutorService executorService;

    public void processOrder(Order order) {
      System.out.println("Заказ на имя " + order.getClient() + " принят в обработку");
      order.setStatus("Готовится");

      long startTime = System.currentTimeMillis();
      long elapsedTime = 0;

      while (elapsedTime < order.getAllMealsDuration().toMillis()) {
          if (order.getStatus().equals("Отменен")) {
              System.out.println("Заказ на имя " + order.getClient() + " отменен");
              return;
          }
        try {
          TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        elapsedTime = System.currentTimeMillis() - startTime;
      }

      System.out.println("Заказ на имя " + order.getClient() + " обработан");
      order.setStatus("Готов");
    }

    private boolean hasLogin(String login) {
        for (Administrator admin : _administrators) {
            if (admin.getLogin().equals(login)) {
               return true;
            }
        }
        return false;
    }

    private boolean checkPassword(String login, String password) {
        for (Administrator admin : _administrators) {
            if (admin.getLogin().equals(login) && admin.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public boolean registerAdministrator() {
        String login = getLine("новый логин администратора");
        if (login.equals(ERROR)) {
            System.out.println("Аутентификация отменена");
            return false;
        }
        System.out.println("Логин успешно установлен");
        String password = getLine("новый пароль");
        if (password.equals(ERROR)) {
            System.out.println("Регистрация отменена");
            return false;
        }
        while (true) {
            String passwordConfirm;
            while (true) {
                System.out.println("Введите новый пароль повторно: ");
                Scanner in = new Scanner(System.in);
                passwordConfirm = in.nextLine();
                if (!passwordConfirm.isEmpty()) {
                    break;
                }
                if (!retry("Пароль не может быть пустым")) {
                    System.out.println("Отмена операции...");
                    passwordConfirm = ERROR;
                    break;
                }
            }
            if (passwordConfirm.equals(ERROR)) {
                System.out.println("Регистрация отменена");
                return false;
            }
            if (!passwordConfirm.equals(password)) {
                if (!retry("Введенные пароли не совпадают.")) {
                    System.out.println("Регистрация отменена");
                    return false;
                }
                continue;
            }
            System.out.println("Пароль успешно установлен.");
            break;
        }
        Administrator newAdmin = new Administrator(login, password);
        _administrators.add(newAdmin);
        System.out.println("Администратор успешно зарегистрирован.");
        return true;
    }

    public boolean authenticateAdministrator() {
        System.out.println("Запущен процесс аутентификации администратора...");
        while (true) {
            String name = getLine("логин администратора");
            if (name.equals(ERROR)) {
                System.out.println("Аутентификация отменена");
                return false;
            }
            if (hasLogin(name)) {
                while (true) {
                    String password = getLine("пароль");
                    if (password.equals(ERROR)) {
                        System.out.println("Аутентификация отменена");
                        return false;
                    }
                    if (checkPassword(name, password)) {
                        System.out.println("Успешная аутентификация.");
                        return true;
                    }
                    if (!retry("Неверный пароль.")) {
                        System.out.println("Аутентификация отменена.");
                        return false;
                    }
                }
            }
            if (!retry("Администратор с таким логином не зарегистрирован.")) {
                System.out.println("Аутентификация отменена.");
                return false;
            }
        }
    }

    public void displayMeals(String client) {
        System.out.println("--------------------------------------------------");
        System.out.println("Меню " + client);
        for (int i = 0; i < meals.size(); ++i) {
            Meal currMeal = meals.get(i);
            if (currMeal.getAmount() > 0) {
                System.out.println((i + 1) + ". " + currMeal.getName() + '\t' + currMeal.getPrice()
                    + " \t" + currMeal.getCookingTime() + '\t' + currMeal.getAmount());
            }
        }
        System.out.println("--------------------------------------------------");
    }

    private Meal getMeal(int index) {
        Meal currMeal = meals.get(index);
        meals.get(index).setAmount(meals.get(index).getAmount() - 1);
        return currMeal;
    }

    public void addMeal() {
        String mealName = getLine("название нового блюда");
        if (mealName.equals(ERROR)) {
            System.out.println("Операция добавления блюда отменена");
            return;
        }
        LocalTime minutes = getMinutes();
        int price;
        while (true) {
            price = getInt("цену в рублях");
            if (price > 0 && price < 1000000) {
                break;
            }
            System.out.println("Цена не может быть меньше нуля или больше 1000000");
        }
        int amount;
        while (true) {
            amount = getInt("количество блюд в наличии");
            if (amount > 0) {
                break;
            }
            System.out.println("Количество блюд не может быть отрицательным");
        }
        Meal meal = new Meal(UUID.randomUUID(), mealName, minutes, price, amount);
        meals.add(meal);
        String message = """
                Блюдо %s в количестве %d успешно добавлено в меню.""";
        System.out.printf((message) + "%n", meal, meal.getAmount());
    }

    public void removeMeal() {
        String mealName = getLine("название блюда для удаления");
        if (mealName.equals(ERROR)) {
            System.out.println("Операция удаления блюда отменена");
            return;
        }
        for (Meal meal : getMeals()) {
            if (Objects.equals(meal.getName(), mealName)) {
                meals.remove(meal);
                String message = """
                Блюдо %s успешно удалено из меню.""";
                System.out.printf((message) + "%n", meal);
                return;
            }
        }
        System.out.println("Такого блюда в меню нет.");
    }

    public void editCount() {
        String mealName = getLine("название блюда для изменения количества");
        if (mealName.equals(ERROR)) {
            System.out.println("Операция изменения количества блюда отменена");
            return;
        }
        for (Meal meal : getMeals()) {
            if (Objects.equals(meal.getName(), mealName)) {
                int amount = getInt("новое количество блюда");
                meal.setAmount(amount);
                return;
            }
        }
        System.out.println("Такого блюда в меню нет.");
    }

    public void editCookingTime() {
        String mealName = getLine("название блюда для изменения времени приготовления");
        if (mealName.equals(ERROR)) {
            System.out.println("Операция изменения времени приготовления блюда отменена");
            return;
        }
        for (Meal meal : getMeals()) {
            if (Objects.equals(meal.getName(), mealName)) {
                LocalTime cookingTime = getMinutes();
                meal.setCookingTime(cookingTime);
                return;
            }
        }
        System.out.println("Такого блюда в меню нет.");
    }

    public void editPrice() {
        String mealName = getLine("название блюда для изменения цены");
        if (mealName.equals(ERROR)) {
            System.out.println("Операция изменения цены блюда отменена");
            return;
        }
        for (Meal meal : getMeals()) {
            if (Objects.equals(meal.getName(), mealName)) {
                int price = getInt("новую цену для блюда в рублях");
                meal.setPrice(price);
                return;
            }
        }
        System.out.println("Такого блюда в меню нет.");
    }

    public ArrayList<Meal> chooseMeals(String client) {
        ArrayList<Meal> meals = new ArrayList<Meal>();
        int answer = 1;
        while(true) {
            displayMeals(client);
            System.out.println("--------------------------------------------------");
            answer = getInt("Введите номер позиции из меню для добавления или "
                + "введите 0 для отмены операции");
            if (answer == 0) {
                break;
            }
            if (answer < 0 || answer > getMeals().size()) {
                System.out.println("Неверный ввод. Повторите попытку");
                continue;
            }

            Meal currMeal = getMeal(answer - 1);
            meals.add(currMeal);
            System.out.println("Вы добавили блюдо " + currMeal.getName() + " в заказ для " + client);
        }
        return meals;
    }

    public void showOrders() {
        System.out.println("--------------------------------------------------");
        System.out.println("Заказы");
        for (int i = 0; i < orders.size(); ++i) {
            Order order = orders.get(i);
            System.out.println((i + 1) + ". " + order.getClient() + '\t' + order.getStatus()
                + " \t" + order.remainedTime());
        }
        System.out.println("--------------------------------------------------");
    }

    public void showOrders(String client) {
        System.out.println("--------------------------------------------------");
        System.out.println("Заказы");
        for (int i = 0; i < orders.size(); ++i) {
            Order order = orders.get(i);
            if (order.getClient().equals(client)) {
                System.out.println((i + 1) + ". " + order.getClient() + '\t' + order.getStatus()
                    + " \t" + order.remainedTime());
            }
        }
        System.out.println("--------------------------------------------------");
    }

    public void createOrder(String client) {
//        String client = getLine("ваше имя для продолжения");
        if (client.equals(ERROR)) {
            System.out.println("Заказ отменен");
            return;
        }
        ArrayList<Meal> meals = new ArrayList<Meal>(chooseMeals(client));
        Order newOrder = new Order(UUID.randomUUID(), LocalTime.now(), meals, client, "Принят");
        executorService.submit(new OrderThread(newOrder, this));
        orders.add(newOrder);
        System.out.println("Заказ на имя " + client + " успешно создан");
    }

    public void cancelOrder(String client) {
        int answer;
        while(true) {
            showOrders(client);
            System.out.println("--------------------------------------------------");
            answer = getInt("Введите номер заказа, который хотите отменить или "
                + "введите 0 для отмены операции");
            if (answer == 0) {
                break;
            }
            if (answer < 0 || answer > getOrders().size()) {
                System.out.println("Неверный ввод. Повторите попытку");
                continue;
            }
            if (orders.get(answer - 1).getStatus().equals("Готовится")) {
                orders.get(answer - 1).setStatus("Отменен");
            } else {
                System.out.println("Выполненные заказы отменить нельзя");
            }
            break;
        }
    }

    public void addMealsToOrder(String client) {
        int answer;
        while(true) {
            showOrders(client);
            answer = getInt("Введите номер заказа, в который хотите добавить блюда "
                + "введите 0 для отмены операции");
            if (answer == 0) {
                break;
            }
            if (answer < 0 || answer > getOrders().size()) {
                System.out.println("Неверный ввод. Повторите попытку");
                continue;
            }
            ArrayList<Meal> meals = new ArrayList<Meal>(chooseMeals(client));
            if (orders.get(answer - 1).getStatus().equals("Готовится")) {
                orders.get(answer - 1).addMeals(meals);
                System.out.println("Новые блюда успешно добавлены в заказ с номером " + answer + " ." );
            } else {
                System.out.println("В выполненные заказы нельзя добавить блюда");
            }
            break;
        }
    }



}
