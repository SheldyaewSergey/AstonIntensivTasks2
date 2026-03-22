package org.example.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.userservice.dto.UserCreateDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void createUser_realFlow() throws Exception {
        UserCreateDto dto = UserCreateDto.builder()
                .name("Real User")
                .email("real@test.com")
                .age(25)
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("real@test.com"));
    }

    @Test
    void createUser_duplicateEmail_shouldFail() throws Exception {
        String json = """
            {
              "name": "Test",
              "email": "dup@test.com",
              "age": 25
            }
        """;

        // первый запрос
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        // второй — должен упасть
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict()); // возможно сейчас будет 500
    }

    @Test
    void createUser_invalidEmail_shouldReturn400() throws Exception {
        String json = """
            {
              "name": "Test",
              "email": "invalid",
              "age": 25
            }
        """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}
