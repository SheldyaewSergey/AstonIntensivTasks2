package org.example.userservice.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("Пользователь с email '" + email + "' уже существует");
    }
}