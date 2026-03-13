package org.example.userservice.model;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserDto {

    private Long id;
    private String name;
    private String email;
    private Integer age;
    private LocalDateTime createdAt;
}
