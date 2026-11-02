package com.example.backend.security;

import com.example.backend.entity.Role;
import com.example.backend.entity.User;

// Поточний авторизований користувач. Кладеться в SecurityContext фільтром
// і отримується в контролерах через @AuthenticationPrincipal.
public record AuthUser(Long id, String email, Role role) {

    public static AuthUser from(User user) {
        return new AuthUser(user.getId(), user.getEmail(), user.getRole());
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}
