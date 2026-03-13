package org.example.userservice.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class ConsoleInputReader {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleInputReader.class);
    private final Scanner scanner;

    public ConsoleInputReader() {
        this.scanner = new Scanner(System.in);
    }

    public String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public Integer readInteger(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                return null;
            }

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число");
                logger.warn("Некорректный ввод числа: {}", input);
            }
        }
    }


    public Long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                return null;
            }

            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите корректное число");
                logger.warn("Некорректный ввод числа: {}", input);
            }
        }
    }

    public Integer readMenuChoice(String prompt, int maxChoice) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= maxChoice) {
                    return choice;
                } else {
                    System.out.println("Ошибка: выберите пункт от 1 до " + maxChoice);
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число от 1 до " + maxChoice);
                logger.warn("Некорректный выбор меню: {}", input);
            }
        }
    }

    public void close() {
        scanner.close();
    }
}
