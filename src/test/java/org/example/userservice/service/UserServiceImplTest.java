package org.example.userservice.service;

import org.example.userservice.exception.UserNotFoundException;
import org.example.userservice.exception.ValidationException;
import org.example.userservice.model.User;
import org.example.userservice.model.UserDto;
import org.example.userservice.repository.UserDao;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto testDto;
    private User testEntity;

    @BeforeEach
    public void setUp() {
        testDto = new UserDto();
        testDto.setId(1L);
        testDto.setName("Test User");
        testDto.setEmail("test@example.com");
        testDto.setAge(25);
        testDto.setCreatedAt(LocalDateTime.now());

        testEntity = new User();
        testEntity.setId(1L);
        testEntity.setName("Test User");
        testEntity.setEmail("test@example.com");
        testEntity.setAge(25);
        testEntity.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @Order(1)
    @DisplayName("Создание пользователя")
    public void testCreateUser() {
        when(userDao.save(any(User.class))).thenReturn(testEntity);

        UserDto result = userService.createUser(testDto);

        assertNotNull(result);
        assertEquals("Test User", result.getName());
        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    @Order(2)
    @DisplayName("Создание пользователя с пустым именем")
    public void testCreateUserEmptyName() {
        testDto.setName("");

        assertThrows(ValidationException.class, () -> userService.createUser(testDto));

        verify(userDao, never()).save(any());
    }

    @Test
    @Order(3)
    @DisplayName("Создание пользователя с некорректным email")
    public void testCreateUserInvalidEmail() {
        testDto.setEmail("invalid-email");

        assertThrows(ValidationException.class, () -> userService.createUser(testDto));
        verify(userDao, never()).save(any());
    }

    @Test
    @Order(4)
    @DisplayName("Получение пользователя по ID")
    public void testGetUserById() {
        when(userDao.findById(1L)).thenReturn(Optional.of(testEntity));

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userDao, times(1)).findById(1L);
    }

    @Test
    @Order(5)
    @DisplayName("Получение несуществующего пользователя")
    public void testGetUserByIdNotFound() {
        when(userDao.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    @Order(6)
    @DisplayName("Получение всех пользователей")
    public void testGetAllUsers() {
        when(userDao.findAll()).thenReturn(Arrays.asList(testEntity));

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userDao, times(1)).findAll();
    }

    @Test
    @Order(7)
    @DisplayName("Обновление пользователя")
    public void testUpdateUser() {
        testDto.setName("Updated Name");
        when(userDao.update(any(User.class))).thenReturn(testEntity);

        UserDto result = userService.updateUser(testDto);

        assertNotNull(result);
        verify(userDao, times(1)).update(any(User.class));
    }

    @Test
    @Order(8)
    @DisplayName("Удаление пользователя")
    public void testDeleteUser() {
        doNothing().when(userDao).delete(1L);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userDao, times(1)).delete(1L);
    }

    @Test
    @Order(9)
    @DisplayName("Удаление с null ID")
    public void testDeleteUserNullId() {
        assertThrows(ValidationException.class, () -> userService.deleteUser(null));
        verify(userDao, never()).delete(any());
    }
}
