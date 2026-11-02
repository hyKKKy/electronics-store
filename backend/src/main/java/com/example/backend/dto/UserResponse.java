package com.example.backend.dto;

import com.example.backend.entity.Role;
import com.example.backend.entity.User;

// Дані користувача, які сервер віддає клієнту (без пароля)
public record UserResponse(Long id, String email, String name, Role role) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getName(), user.getRole());
    }
}
