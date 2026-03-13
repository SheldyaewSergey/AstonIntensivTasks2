package org.example.userservice.service;

import org.example.userservice.exception.ValidationException;
import org.example.userservice.exception.UserNotFoundException;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.model.User;
import org.example.userservice.model.UserDto;
import org.example.userservice.repository.UserDao;
import org.example.userservice.repository.UserDaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;

    public UserServiceImpl() {
        this.userDao = new UserDaoImpl();
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        logger.info("Создание нового пользователя: {}", userDto.getEmail());
        validateUserDto(userDto, false);
        User user = UserMapper.toEntity(userDto);
        User savedUser = userDao.save(user);
        return UserMapper.toDto(savedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        logger.info("Поиск пользователя по ID: {}", id);

        Optional<User> userOpt = userDao.findById(id);

        if (userOpt.isEmpty()) {
            throw new UserNotFoundException(id);
        }

        return UserMapper.toDto(userOpt.get());
    }


    @Override
    public List<UserDto> getAllUsers() {
        logger.info("Получение списка всех пользователей");

        List<User> users = userDao.findAll();

        return users.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public UserDto updateUser(UserDto userDto) {
        logger.info("Обновление пользователя: {}", userDto.getId());

        if (userDto.getId() == null) {
            throw new ValidationException("ID пользователя не может быть пустым при обновлении");
        }

        validateUserDto(userDto, true);

        User user = UserMapper.toEntity(userDto);

        User updatedUser = userDao.update(user);

        return UserMapper.toDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        logger.info("Удаление пользователя по ID: {}", id);

        if (id == null) {
            throw new ValidationException("ID пользователя не может быть пустым");
        }

        userDao.delete(id);
    }

    private void validateUserDto(UserDto dto, boolean isUpdate) {
        if (dto == null) {
            throw new ValidationException("Данные пользователя не могут быть пустыми");
        }

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new ValidationException("Имя пользователя не может быть пустым");
        }

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email не может быть пустым");
        }

        if (!dto.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Некорректный формат email");
        }

        if (dto.getAge() == null) {
            throw new ValidationException("Возраст не может быть пустым");
        }

        if (dto.getAge() < 0 || dto.getAge() > 150) {
            throw new ValidationException("Возраст должен быть от 0 до 150");
        }

        if (!isUpdate) {
            if (dto.getName().length() < 2 || dto.getName().length() > 100) {
                throw new ValidationException("Имя должно быть от 2 до 100 символов");
            }
        }
    }
}
