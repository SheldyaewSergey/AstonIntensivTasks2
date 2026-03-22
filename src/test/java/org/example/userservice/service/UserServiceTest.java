package org.example.userservice.service;

import org.example.userservice.dto.UserCreateDto;
import org.example.userservice.model.User;
import org.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createUser_shouldSaveToDatabase() {
        UserCreateDto dto = UserCreateDto.builder()
                .name("Service User")
                .email("service@test.com")
                .age(30)
                .build();

        var result = userService.createUser(dto);

        assertNotNull(result.getId());

        User fromDb = userRepository.findById(result.getId()).orElseThrow();
        assertEquals("service@test.com", fromDb.getEmail());
    }
}