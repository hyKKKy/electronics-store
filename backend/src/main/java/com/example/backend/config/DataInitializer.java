package com.example.backend.config;

import com.example.backend.entity.Category;
import com.example.backend.entity.Product;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// Заповнює базу тестовими даними при першому запуску (якщо категорій ще немає).
// Потрібно, щоб каталог одразу був не порожній для перевірки й демонстрації.
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    // Тестовий адміністратор для локального запуску
    private static final String ADMIN_EMAIL = "admin@store.local";
    private static final String ADMIN_PASSWORD = "admin123";

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createAdminIfMissing();

        if (categoryRepository.count() > 0) {
            return;
        }

        Category phones = category("Смартфони", "Мобільні телефони");
        Category laptops = category("Ноутбуки", "Ноутбуки для роботи, навчання та ігор");
        Category headphones = category("Навушники", "Дротові та бездротові навушники");
        Category tvs = category("Телевізори", "Smart TV різних діагоналей");

        product("iPhone 16", "Apple", "41999.00", 12, phones, "Смартфон Apple, 128 ГБ");
        product("Galaxy S25", "Samsung", "37999.00", 8, phones, "Смартфон Samsung, 256 ГБ");
        product("Redmi Note 14", "Xiaomi", "9999.00", 25, phones, "Бюджетний смартфон, 128 ГБ");
        product("MacBook Air 13 M4", "Apple", "54999.00", 5, laptops, "Ноутбук Apple, 16 ГБ RAM, 256 ГБ SSD");
        product("IdeaPad Slim 5", "Lenovo", "32999.00", 7, laptops, "Ноутбук для навчання, 16 ГБ RAM");
        product("ROG Strix G16", "ASUS", "67999.00", 3, laptops, "Ігровий ноутбук, RTX 4060");
        product("WH-1000XM5", "Sony", "13999.00", 10, headphones, "Бездротові навушники з шумозаглушенням");
        product("AirPods Pro 2", "Apple", "10999.00", 0, headphones, "Бездротові навушники (немає в наявності)");
        product("OLED55C4", "LG", "54999.00", 4, tvs, "Телевізор OLED 55\"");
    }

    private void createAdminIfMissing() {
        if (userRepository.existsByEmail(ADMIN_EMAIL)) {
            return;
        }
        User admin = new User();
        admin.setEmail(ADMIN_EMAIL);
        admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setName("Адміністратор");
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
    }

    private Category category(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return categoryRepository.save(category);
    }

    private void product(String name, String brand, String price, int stock, Category category, String description) {
        Product product = new Product();
        product.setName(name);
        product.setBrand(brand);
        product.setPrice(new BigDecimal(price));
        product.setStock(stock);
        product.setCategory(category);
        product.setDescription(description);
        productRepository.save(product);
    }
}
