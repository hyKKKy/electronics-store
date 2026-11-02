package com.example.backend.controller;

import com.example.backend.dto.ChangeRoleRequest;
import com.example.backend.dto.PageResponse;
import com.example.backend.dto.UserResponse;
import com.example.backend.security.AuthUser;
import com.example.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// Керування користувачами (тільки ADMIN, див. SecurityConfig)
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    // Приклад: GET /api/admin/users?search=gmail&page=0&size=20
    @GetMapping
    public PageResponse<UserResponse> search(
            @RequestParam(name = "search", required = false) String search,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return userService.search(search, pageable);
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable("id") Long id) {
        return userService.findById(id);
    }

    @PatchMapping("/{id}/role")
    public UserResponse changeRole(@PathVariable("id") Long id,
                                   @Valid @RequestBody ChangeRoleRequest request,
                                   @AuthenticationPrincipal AuthUser authUser) {
        return userService.changeRole(id, request.role(), authUser.id());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id, @AuthenticationPrincipal AuthUser authUser) {
        userService.delete(id, authUser.id());
    }
}
