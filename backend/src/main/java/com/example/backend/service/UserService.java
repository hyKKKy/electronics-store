package com.example.backend.service;

import com.example.backend.dto.ChangePasswordRequest;
import com.example.backend.dto.PageResponse;
import com.example.backend.dto.UpdateProfileRequest;
import com.example.backend.dto.UserResponse;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.NotFoundException;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    // ---------- Власний профіль ----------

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return UserResponse.from(getUser(id));
    }

    @Transactional
    public UserResponse updateProfile(Long id, UpdateProfileRequest request) {
        User user = getUser(id);
        user.setName(request.name().trim());
        return UserResponse.from(user);
    }

    @Transactional
    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = getUser(id);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadRequestException("Невірний поточний пароль");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
    }

    // ---------- Адміністрування ----------

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> search(String search, Pageable pageable) {
        String text = (search == null || search.isBlank()) ? null : search.trim();
        return PageResponse.from(userRepository.search(text, pageable).map(UserResponse::from));
    }

    @Transactional
    public UserResponse changeRole(Long id, Role role, Long currentUserId) {
        // Щоб адміністратор випадково не позбавив себе доступу до адмінки
        if (id.equals(currentUserId)) {
            throw new BadRequestException("Не можна змінити роль самому собі");
        }
        User user = getUser(id);
        user.setRole(role);
        return UserResponse.from(user);
    }

    @Transactional
    public void delete(Long id, Long currentUserId) {
        if (id.equals(currentUserId)) {
            throw new BadRequestException("Не можна видалити самого себе");
        }
        User user = getUser(id);
        // Замовлення мають залишатися в історії, тому користувача з замовленнями не видаляємо
        if (orderRepository.existsByUserId(id)) {
            throw new ConflictException("Неможливо видалити користувача: у нього є замовлення");
        }
        userRepository.delete(user);
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Користувача з id=" + id + " не знайдено"));
    }
}
