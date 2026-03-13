package org.example.userservice.ui;

import org.example.userservice.exception.DatabaseException;
import org.example.userservice.exception.ValidationException;
import org.example.userservice.exception.UserNotFoundException;
import org.example.userservice.model.UserDto;
import org.example.userservice.service.UserService;
import org.example.userservice.service.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ConsoleMenu {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleMenu.class);

    private final UserService userService;
    private final ConsoleInputReader inputReader;

    public ConsoleMenu() {
        this.userService = new UserServiceImpl();
        this.inputReader = new ConsoleInputReader();
    }

    public void showMenu() {
        logger.info("Запуск консольного меню");
        boolean running = true;

        while (running) {
            printMenu();
            int choice = inputReader.readMenuChoice("Выберите пункт меню (1-6): ", 6);

            try {
                switch (choice) {
                    case 1:
                        createUser();
                        break;
                    case 2:
                        getUserById();
                        break;
                    case 3:
                        getAllUsers();
                        break;
                    case 4:
                        updateUser();
                        break;
                    case 5:
                        deleteUser();
                        break;
                    case 6:
                        running = false;
                        logger.info("Приложение завершено пользователем");
                        break;
                }
            } catch (UserNotFoundException e) {
                System.out.println(e.getMessage());
                logger.warn("Пользователь не найден: {}", e.getMessage());
            } catch (ValidationException e) {
                System.out.println(e.getMessage());
                logger.warn("Ошибка валидации: {}", e.getMessage());
            } catch (DatabaseException e) {
                System.out.println("Ошибка базы данных: " + e.getMessage());
                logger.error("Ошибка базы данных", e);
            } catch (Exception e) {
                System.out.println("Неожиданная ошибка: " + e.getMessage());
                logger.error("Неожиданная ошибка", e);
            }

            if (running) {
                System.out.println("\nНажмите Enter для продолжения...");
                inputReader.readString("");
            }
        }
    }

    private void printMenu() {

        System.out.println("\n========================================");
        System.out.println("1. Создать нового пользователя");
        System.out.println("2. Найти пользователя по ID");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("6. Выход");
        System.out.println("========================================");
    }

    private void createUser() {
        System.out.println("\n--- Создание нового пользователя ---");

        String name = inputReader.readString("Введите имя: ");
        String email = inputReader.readString("Введите email: ");
        Integer age = inputReader.readInteger("Введите возраст: ");

        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);
        dto.setAge(age);

        try {
            UserDto created = userService.createUser(dto);
            System.out.println("\nПользователь успешно создан!");
            printUser(created);
            logger.info("Пользователь создан: {}", created.getEmail());
        } catch (Exception e) {
            throw e;
        }
    }

    private void getUserById() {
        System.out.println("\n--- Поиск пользователя по ID ---");

        Long id = inputReader.readLong("Введите ID пользователя: ");

        if (id == null) {
            System.out.println("ID не может быть пустым");
            return;
        }

        UserDto user = userService.getUserById(id);
        System.out.println("\nПользователь найден:");
        printUser(user);
        logger.info("Пользователь найден по ID: {}", id);
    }

    private void getAllUsers() {
        System.out.println("\n--- Список всех пользователей ---");

        List<UserDto> users = userService.getAllUsers();

        if (users.isEmpty()) {
            System.out.println("Список пользователей пуст");
        } else {
            System.out.println("Найдено пользователей: " + users.size());
            for (UserDto user : users) {
                printUser(user);
                System.out.println("----------------------------------------");
            }
        }
        logger.info("Получено всех пользователей: {}", users.size());
    }

    private void updateUser() {
        System.out.println("\n--- Обновление пользователя ---");

        Long id = inputReader.readLong("Введите ID пользователя для обновления: ");

        if (id == null) {
            System.out.println("ID не может быть пустым");
            return;
        }

        String name = inputReader.readString("Введите новое имя: ");
        String email = inputReader.readString("Введите новый email: ");
        Integer age = inputReader.readInteger("Введите новый возраст: ");

        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setName(name);
        dto.setEmail(email);
        dto.setAge(age);

        UserDto updated = userService.updateUser(dto);
        System.out.println("\nПользователь успешно обновлен!");
        printUser(updated);
        logger.info("Пользователь обновлен: {}", updated.getEmail());
    }

    private void deleteUser() {
        System.out.println("\n--- Удаление пользователя ---");

        Long id = inputReader.readLong("Введите ID пользователя для удаления: ");

        if (id == null) {
            System.out.println("ID не может быть пустым");
            return;
        }

        userService.deleteUser(id);
        System.out.println("\nПользователь успешно удален!");
        logger.info("Пользователь удален по ID: {}", id);
    }

    private void printUser(UserDto user) {
        if (user == null) {
            return;
        }
        System.out.println("ID: " + user.getId());
        System.out.println("Имя: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Возраст: " + user.getAge());
        System.out.println("Дата создания: " + user.getCreatedAt());
    }

    public void close() {
        inputReader.close();
    }
}

