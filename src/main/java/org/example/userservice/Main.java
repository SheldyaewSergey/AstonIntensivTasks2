package org.example.userservice;

import org.example.userservice.config.HibernateConfig;
import org.example.userservice.ui.ConsoleMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Запуск приложения User Service");
        System.out.println("Запуск User Service");

        ConsoleMenu menu = null;

        try {
            HibernateConfig.init();
            logger.info("Hibernate успешно инициализирован");

            menu = new ConsoleMenu();
            menu.showMenu();

        } catch (Exception e) {
            logger.error("Критическая ошибка приложения", e);
            System.out.println("Критическая ошибка: " + e.getMessage());
        } finally {
            if (menu != null) {
                menu.close();
            }
            HibernateConfig.close();
            logger.info("Приложение завершено");
        }
    }
}