package org.example.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.userservice.dto.UserCreateDto;
import org.example.userservice.dto.UserResponseDto;
import org.example.userservice.dto.UserUpdateDto;
import org.example.userservice.exception.UserNotFoundException;
import org.example.userservice.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;
    private UserCreateDto createDto;
    private UserUpdateDto updateDto;
    private UserResponseDto responseDto;

    @BeforeEach
    void setUp() {
        createDto = UserCreateDto.builder()
                .name("Test User")
                .email("test@example.com")
                .age(25)
                .build();

        updateDto = UserUpdateDto.builder()
                .name("Updated User")
                .email("updated@example.com")
                .age(30)
                .build();

        responseDto = UserResponseDto.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .age(25)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /api/users — создание пользователя")
    void createUser() throws Exception {
        given(userService.createUser(any(UserCreateDto.class)))
                .willReturn(responseDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test User")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.age", is(25)))
                .andExpect(jsonPath("$.createdAt", notNullValue()));

        verify(userService, times(1)).createUser(any(UserCreateDto.class));
    }

    @Test
    @DisplayName("POST /api/users — валидация: пустое имя")
    void createUserEmptyName() throws Exception {
        createDto.setName("");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any());
    }

    @Test
    @DisplayName("POST /api/users — валидация: некорректный email")
    void createUserInvalidEmail() throws Exception {
        createDto.setEmail("not-an-email");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any());
    }

    @Test
    @DisplayName("GET /api/users/{id} — получение пользователя")
    void getUserById() throws Exception {
        given(userService.getUserById(1L)).willReturn(responseDto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test User")));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @DisplayName("GET /api/users/{id} — пользователь не найден")
    void getUserByIdNotFound() throws Exception {
        given(userService.getUserById(999L))
                .willThrow(new UserNotFoundException(999L));

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("999")));

        verify(userService, times(1)).getUserById(999L);
    }

    @Test
    @DisplayName("GET /api/users — список всех пользователей")
    void getAllUsers() throws Exception {
        given(userService.getAllUsers())
                .willReturn(List.of(responseDto));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test User")));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("PUT /api/users/{id} — обновление пользователя")
    void updateUser() throws Exception {
        UserResponseDto updatedResponse = UserResponseDto.builder()
                .id(1L)
                .name("Updated User")
                .email("updated@example.com")
                .age(30)
                .createdAt(LocalDateTime.now())
                .build();

        given(userService.updateUser(eq(1L), any(UserUpdateDto.class)))
                .willReturn(updatedResponse);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated User")))
                .andExpect(jsonPath("$.email", is("updated@example.com")));

        verify(userService, times(1)).updateUser(eq(1L), any(UserUpdateDto.class));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} — удаление пользователя")
    void deleteUser() throws Exception {
        willDoNothing().given(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @DisplayName("DELETE /api/users/{id} — пользователь не найден")
    void deleteUserNotFound() throws Exception {
        willThrow(new UserNotFoundException(999L))
                .given(userService).deleteUser(999L);

        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));

        verify(userService, times(1)).deleteUser(999L);
    }
}
