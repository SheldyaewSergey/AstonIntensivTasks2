package org.example.userservice.repository;
import org.example.userservice.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveUser_shouldWork() {
        User user = User.builder()
                .name("Repo User")
                .email("repo@test.com")
                .age(22)
                .build();

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
    }

    @Test
    void saveUser_duplicateEmail_shouldFail() {
        User user1 = User.builder()
                .name("User1")
                .email("dup@test.com")
                .age(20)
                .build();

        User user2 = User.builder()
                .name("User2")
                .email("dup@test.com")
                .age(25)
                .build();

        userRepository.save(user1);

        assertThrows(Exception.class, () -> {
            userRepository.saveAndFlush(user2);
        });
    }
}
