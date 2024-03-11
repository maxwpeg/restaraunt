package org.komarov.classes;

import java.time.LocalTime;
import java.util.Scanner;

public class UserIO {
    public static final String ERROR = "ERROR";

    private static OrderSystem orderSystem;

    public static boolean retry(String message) {
        int answer;
        while (true) {
            System.out.println(message + " Повторить попытку(1) или отменить аутентификацию(2)?");
            Scanner scanner = new Scanner(System.in);
            try {
                answer = scanner.nextInt();
            } catch (Exception e) {
                System.out.println("Неверный ввод, повторите попытку.");
                continue;
            }
            if (answer == 1) {
                return true;
            }
            if (answer == 2) {
                return false;
            }
            System.out.println("Неверный ввод, повторите попытку.");
        }
    }

    public static int getInt(String var) {
        int answer;
        while (true) {
            System.out.println("Введите " + var + " в формате целого числа(int): ");
            Scanner scanner = new Scanner(System.in);
            try {
                answer = scanner.nextInt();
                return answer;
            } catch (Exception e) {
                System.out.println("Неверный ввод, повторите попытку.");
            }
        }
    }

    public static String getLine(String var) {
        String res;
        while (true) {
            System.out.println("Введите " + var + ": ");
            Scanner in = new Scanner(System.in);
            res = in.nextLine();
            if (!res.isEmpty()) {
                break;
            }
            if (!retry(var + " не может быть пустым")) {
                System.out.println("Отмена операции...");
                res = ERROR;
                break;
            }
        }
        return res;
    }

    public static LocalTime getMinutes() {
        int input;
        while (true) {
            input = getInt("количество минут");
            if (input > 0 && input < 1439) {
                break;
            }
            System.out.println("Число минут может быть только в интервале [0, 1439].");
            System.out.println("Повторите попытку.");
        }
        return LocalTime.of(input / 60, input % 60);
    }

    private static void finish() {
        orderSystem.getExecutorService().shutdown();
        OrderSystemIO.saveToFile(orderSystem, "system.ser");
        System.out.println("Программа завершила работу");
        System.out.println("До новых встреч!");
    }

    private static void userActions() {
        String name = getLine("ваше имя для продолжения");
        while (true) {
            System.out.println("Выберите действие");
            System.out.println("1. Создать заказ");
            System.out.println("2. Отменить заказ");
            System.out.println("3. Добавить блюда в заказ");
            System.out.println("4. Выйти");
            int answer = getInt("номер действия");
            switch (answer) {
                case 1:
                    orderSystem.createOrder(name);
                    break;
                case 2:
                    orderSystem.cancelOrder(name);
                    break;
                case 3:
                    orderSystem.addMealsToOrder(name);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private static void administratorActions() {
        while (true) {
            System.out.println("Выберите действие");
            System.out.println("1. Показать статистику");
            System.out.println("2. Добавить блюдо");
            System.out.println("3. Удалить блюдо");
            System.out.println("4. Смена количество блюда");
            System.out.println("5. Смена время приготовления блюда");
            System.out.println("6. Смена цену блюда");
            System.out.println("7. Выйти");
            int answer = getInt("номер действия");
            switch (answer) {
                case 1:
                    orderSystem.showStatistics();
                    break;
                case 2:
                    orderSystem.addMeal();
                    break;
                case 3:
                    orderSystem.removeMeal();
                    break;
                case 4:
                    orderSystem.editCount();
                    break;
                case 5:
                    orderSystem.editCookingTime();
                    break;
                case 6:
                    orderSystem.editPrice();
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private static boolean setUpAdmin() {
        while (true) {
            System.out.println("Администратор: ");
            System.out.println("Выберите действие");
            System.out.println("1. Войти");
            System.out.println("2. Зарегистрироваться");
            System.out.println("3. Выйти");
            int answer = getInt("номер действия");
            switch (answer) {
                case 1:
                    return orderSystem.authenticateAdministrator();
                case 2:
                    return orderSystem.registerAdministrator();
                case 3:
                    return false;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private static void start() {
        while (true) {
            System.out.println("Добро пожаловать!");
            System.out.println("Выберите действие");
            System.out.println("1. Войти как посетитель");
            System.out.println("2. Войти как администратор");
            System.out.println("3. Выйти");
            int answer = getInt("номер действия");
            switch (answer) {
                case 1:
                    userActions();
                    break;
                case 2:
                    if (setUpAdmin()) {
                        administratorActions();
                    }
                    break;
                case 3:
                    finish();
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    public static void tryToStart() {
        orderSystem = OrderSystemIO.loadFromFile("system.ser");;
        try {
            start();
        } catch (Exception e) {
            System.out.println("Возникла ошибка исполнения, перезапуск....");
            tryToStart();
        }
    }
}
