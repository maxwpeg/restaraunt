package org.komarov.classes;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.komarov.classes.UserIO.*;

@Data
@Builder
public class OrderSystem {

    private ArrayList<Administrator> administrators;

    private ArrayList<Order> orders;

    private ArrayList<Meal> meals;

    private ExecutorService executorService;

    private ArrayList<Review> reviews;

    private int totalSum;

    public void showStatistics() {
        displayPopularMeals();
        calculateAverageScores();
        countOrdersForPeriod();
    }
    private void displayPopularMeals() {
        Map<String, Integer> mealPopularity = new HashMap<>();
        for (Order order : orders) {
            for (Meal meal : order.getMeals()) {
                String mealName = meal.getName();
                mealPopularity.put(mealName, mealPopularity.getOrDefault(mealName, 0) + 1);
            }
        }

        List<Map.Entry<String, Integer>> popularMeals = new ArrayList<>(mealPopularity.entrySet());
        popularMeals.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        popularMeals.forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    }

    private void calculateAverageScores() {
        Map<String, Integer> totalScores = new HashMap<>();
        Map<String, Integer> mealCounts = new HashMap<>();

        for (Review review : reviews) {
            String mealName = review.getMeal().getName();
            totalScores.put(mealName, totalScores.getOrDefault(mealName, 0) + review.getScore());
            mealCounts.put(mealName, mealCounts.getOrDefault(mealName, 0) + 1);
        }

        totalScores.forEach((mealName, score) -> {
            int count = mealCounts.get(mealName);
            double average = (double) score / count;
            System.out.println(mealName + ": " + average);
        });
    }

    private void countOrdersForPeriod() {
        int totalOrders = orders.size();
        long canceledOrders = orders.stream().filter(order -> order.getStatus().equals("Отменен")).count();
        long completedOrders = orders.stream().filter(order -> order.getStatus().equals("Готов")).count();

        System.out.println("Всего заказов за период: " + totalOrders);
        System.out.println("Отмененных заказов: " + canceledOrders);
        System.out.println("Выполненных заказов: " + completedOrders);
    }

    public void processOrder(int index) {
        System.out.println("Заказ на имя " + orders.get(index).getClient() + " принят в обработку");
        orders.get(index).setStatus("Готовится");
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;

        while (elapsedTime < orders.get(index).getAllMealsDuration().toMillis()) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("Возникла ошибка при выполнении заказа.");;
            }
            elapsedTime = System.currentTimeMillis() - startTime;
        }
        finishOrder(index);
    }

    private void finishOrder(int index) {
        if (orders.get(index).getMeals().isEmpty()) {
            System.out.println("Заказ на имя " + orders.get(index).getClient() + " отменен");
            return;
        }
        System.out.println("Заказ на имя " + orders.get(index).getClient() + " обработан");
        orders.get(index).setStatus("Готов");
        payOrder(index);
        int answer = getInt("1, если хотите оставить отзыв");
        if (answer == 1) {
            collectReviews(index);
        }
    }

    private void payOrder(int index) {
        int sum = orders.get(index).getTotalSum();
        System.out.println("Сумма к оплате: " + sum);
        while (true) {
            int i = UserIO.getInt("номер банковского счета(9 цифр)");
            if (i < 100000000 || i > 999999999) {
                System.out.println("Неверный ввод, повторите попытку.");
                continue;
            }
            break;
        }
        System.out.println("Заказ был записан на ваш счет. Спасибо, что были у нас!");
        totalSum += sum;
        orders.get(index).setStatus("Оплачен");
    }

    public void collectReviews(int index) {
        System.out.println("Пожалуйста, оставьте отзыв о каждом из блюд: ");
        for (Meal meal : meals) {
            System.out.println(meal.getName() + ": ");
            int score;
            while (true) {
                score = UserIO.getInt("оценку блюда (1-5)");
                if (score < 1 || score > 5) {
                    System.out.println("Неверный ввод, повторите попытку.");
                    continue;
                }
                break;
            }
            System.out.println("Оставьте отзыв о нем(до 3000 символов): ");
            Scanner in = new Scanner(System.in);
            String reviewText = in.nextLine();
            reviews.add(Review.builder().text(reviewText).score(score).meal(meal).build());
        }
    }

    private boolean hasLogin(String login) {
        for (Administrator admin : administrators) {
            if (admin.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkPassword(String login, String password) {
        for (Administrator admin : administrators) {
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
        administrators.add(newAdmin);
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
        int answer = getAnswerToEdit("удаления");
        Meal meal = getMeal(answer - 1);
        meals.remove(meal);
        String message = """
                        Блюдо %s успешно удалено из меню.""";
        System.out.printf((message) + "%n", meal);
    }

    private int getAnswerToEdit(String var) {
        int answer = 1;
        while (true) {
            displayMeals("admin");
            System.out.println("--------------------------------------------------");
            answer = getInt("номер позиции из меню для " + var + " или "
                    + "введите 0 для отмены операции");
            if (answer == 0) {
                break;
            }
            if (answer < 0 || answer > getMeals().size()) {
                System.out.println("Неверный ввод. Повторите попытку");
                continue;
            }
            break;
        }
        return answer;
    }
    public void editCount() {
        int answer = getAnswerToEdit("изменения количества");
        if (answer == 0) {
            System.out.println("Операция изменения количества блюда отменена");
            return;
        }
        Meal currMeal = getMeal(answer - 1);
        int amount = getInt("новое количество блюда");
        currMeal.setAmount(amount);
    }

    public void editCookingTime() {
        int answer = getAnswerToEdit("изменения времени приготовления");;
        if (answer == 0) {
            System.out.println("Операция изменения времени приготовления блюда отменена");
            return;
        }
        Meal currMeal = getMeal(answer - 1);
        LocalTime cookingTime = getMinutes();
        currMeal.setCookingTime(cookingTime);
    }

    public void editPrice() {
        int answer = getAnswerToEdit("изменения цены");
        if (answer == 0) {
            System.out.println("Операция изменения цены блюда отменена");
            return;
        }
        Meal currMeal = getMeal(answer - 1);
        int price = getInt("новую цену для блюда в рублях");
        currMeal.setPrice(price);
    }

    public ArrayList<Meal> chooseMeals(String client) {
        ArrayList<Meal> meals = new ArrayList<Meal>();
        int answer = 1;
        while (true) {
            displayMeals(client);
            System.out.println("--------------------------------------------------");
            answer = getInt("номер позиции из меню для добавления или "
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
        if (client.equals(ERROR)) {
            System.out.println("Заказ отменен");
            return;
        }
        ArrayList<Meal> meals = new ArrayList<Meal>(chooseMeals(client));
        Order newOrder = new Order(UUID.randomUUID(), LocalTime.now(), meals, client, "Принят");
        orders.add(newOrder);
        executorService.submit(new OrderThread(orders.lastIndexOf(newOrder), this));
        System.out.println("Заказ на имя " + client + " успешно создан");
    }

    public synchronized void cancelOrder(String client) {
        int answer;
        while (true) {
            showOrders(client);
            System.out.println("--------------------------------------------------");
            answer = getInt("номер заказа, который хотите отменить или "
                    + "введите 0 для отмены операции");
            if (answer == 0) {
                break;
            }
            if (answer < 0 || answer > getOrders().size()) {
                System.out.println("Неверный ввод. Повторите попытку");
                continue;
            }
            if (orders.get(answer - 1).getStatus().equals("Готовится")) {
                orders.get(answer - 1).setMeals(new ArrayList<>());
                orders.get(answer - 1).setStatus("Отменен");
            } else {
                System.out.println("Выполненные заказы отменить нельзя");
            }
            break;
        }
    }

    public void addMealsToOrder(String client) {
        int answer;
        while (true) {
            showOrders(client);
            answer = getInt("номер заказа, в который хотите добавить блюда "
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
                System.out.println("Новые блюда успешно добавлены в заказ с номером " + answer + " .");
            } else {
                System.out.println("В выполненные заказы нельзя добавить блюда");
            }
            break;
        }
    }
}
