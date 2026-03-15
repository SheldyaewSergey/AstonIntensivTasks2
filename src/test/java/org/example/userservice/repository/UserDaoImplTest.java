package org.example.userservice.repository;

import org.example.userservice.config.HibernateConfig;
import org.example.userservice.exception.UserNotFoundException;
import org.example.userservice.model.User;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDaoImplTest {

    private static UserDao userDao;
    private static User savedUser;

    @BeforeAll
    public static void SetUp() {
        System.setProperty("hibernate.connection.url",
                "jdbc:postgresql://localhost:5432/test_db");
        System.setProperty("hibernate.connection.username", "postgres");
        System.setProperty("hibernate.connection.password", "secret");

        HibernateConfig.init();
        userDao = new UserDaoImpl();
    }

    @Test
    @Order(1)
    @DisplayName("Сохранение нового пользователя")
    public void testSave() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setAge(25);

        User saved = userDao.save(user);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Test User", saved.getName());
        assertNotNull(saved.getCreatedAt());

        savedUser = saved;
    }

    @Test
    @Order(2)
    @DisplayName("Поиск пользователя по ID")
    public void testFindById() {
        Optional<User> found = userDao.findById(savedUser.getId());

        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    @Order(3)
    @DisplayName("Поиск несуществующего пользователя")
    public void testFindByIdNotFound() {
        Optional<User> found = userDao.findById(9999L);
        assertFalse(found.isPresent());
    }

    @Test
    @Order(4)
    @DisplayName("Получение всех пользователей")
    public void testFindAll() {
        List<User> users = userDao.findAll();
        assertFalse(users.isEmpty());
        assertTrue(users.size() >= 1);
    }

    @Test
    @Order(5)
    @DisplayName("Обновление пользователя")
    public void testUpdate() {
        savedUser.setName("Updated Name");
        savedUser.setAge(30);

        User updated = userDao.update(savedUser);

        assertEquals("Updated Name", updated.getName());
        assertEquals(30, updated.getAge());
    }

    @Test
    @Order(6)
    @DisplayName("Обновление несуществующего пользователя")
    public void testUpdateNotFound() {
        User user = new User();
        user.setId(9999L);
        user.setName("Fake");
        user.setEmail("fake@test.com");
        user.setAge(20);

        assertThrows(UserNotFoundException.class, () -> userDao.update(user));
    }

    @Test
    @Order(7)
    @DisplayName("Удаление пользователя")
    public void testDelete() {
        userDao.delete(savedUser.getId());

        Optional<User> deleted = userDao.findById(savedUser.getId());
        assertFalse(deleted.isPresent());
    }

    @Test
    @Order(8)
    @DisplayName("Удаление несуществующего пользователя")
    public void testDeleteNotFound() {
        assertThrows(UserNotFoundException.class, () -> userDao.delete(9999L));
    }

    @AfterAll
    public static void tearDown() {
        HibernateConfig.close();
    }
}

