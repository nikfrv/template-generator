package com.example.templategenerator.model.dto.db;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Email обязателен") @Email(message = "Некорректный email") String email,

        @NotBlank(message = "Пароль обязателен") @Size(min = 4, message = "Пароль должен быть не менее 4 символов") String password,

        @NotBlank(message = "Имя обязательно") String fullName) {
}
