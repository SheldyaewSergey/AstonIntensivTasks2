package org.example.userservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {

    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotNull(message = "Возраст не может быть пустым")
    @Min(0)
    @Max(150)
    private Integer age;
}
