package org.komarov.classes;

import java.time.LocalTime;
import java.util.Scanner;

public class UserIO {
  public static final String ERROR = "ERROR";

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
}
