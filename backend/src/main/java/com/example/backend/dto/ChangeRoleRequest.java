package com.example.backend.dto;

import com.example.backend.entity.Role;
import jakarta.validation.constraints.NotNull;

public record ChangeRoleRequest(
        @NotNull(message = "Роль обов'язкова")
        Role role
) {
}
