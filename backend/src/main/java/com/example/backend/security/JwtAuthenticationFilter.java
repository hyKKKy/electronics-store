package com.example.backend.security;

import com.example.backend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// Читає заголовок "Authorization: Bearer <token>" і, якщо токен валідний,
// авторизує користувача на час цього запиту.
// Не @Component: інакше Spring Boot зареєструє його ще й як звичайний servlet-фільтр
// поза ланцюжком Spring Security. Створюється в SecurityConfig.
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length());
            // Користувача беремо з БД, щоб зміна ролі чи видалення діяли одразу
            jwtService.extractUserId(token)
                    .flatMap(userRepository::findById)
                    .ifPresent(user -> {
                        AuthUser authUser = AuthUser.from(user);
                        var authentication = new UsernamePasswordAuthenticationToken(
                                authUser, null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    });
        }
        filterChain.doFilter(request, response);
    }
}
