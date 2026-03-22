package org.example.userservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {

    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
    private String name;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotNull(message = "Возраст не может быть пустым")
    @Min(value = 0, message = "Возраст не может быть отрицательным")
    @Max(value = 150, message = "Возраст должен быть не более 150")
    private Integer age;
}
