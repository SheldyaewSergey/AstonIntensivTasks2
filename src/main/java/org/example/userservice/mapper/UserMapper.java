package org.example.userservice.mapper;

import org.example.userservice.model.User;
import org.example.userservice.model.UserDto;


public class UserMapper {

    public static User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setAge(dto.getAge());
        return user;
    }

    public static UserDto toDto(User entity) {
        if (entity == null) {
            return null;
        }

        return UserDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .age(entity.getAge())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
