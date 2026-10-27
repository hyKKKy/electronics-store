package com.example.backend.service;

import com.example.backend.dto.PageResponse;
import com.example.backend.dto.ProductRequest;
import com.example.backend.dto.ProductResponse;
import com.example.backend.entity.Category;
import com.example.backend.entity.Product;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.NotFoundException;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.OrderItemRepository;
import com.example.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderItemRepository orderItemRepository;

    // Каталог: пошук + фільтри + пагінація
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> search(String search, Long categoryId,
                                                BigDecimal minPrice, BigDecimal maxPrice,
                                                Pageable pageable) {
        // Порожній рядок пошуку = без пошуку
        String text = (search == null || search.isBlank()) ? null : search.trim();
        Page<ProductResponse> page = productRepository
                .search(text, categoryId, minPrice, maxPrice, pageable)
                .map(ProductResponse::from);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return ProductResponse.from(getProduct(id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        fillFromRequest(product, request);
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getProduct(id);
        fillFromRequest(product, request);
        return ProductResponse.from(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = getProduct(id);
        // Товар, який є в замовленнях, видаляти не можна — інакше зламається історія замовлень
        if (orderItemRepository.existsByProductId(id)) {
            throw new ConflictException("Неможливо видалити товар: він є в замовленнях");
        }
        productRepository.delete(product);
    }

    private void fillFromRequest(Product product, ProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new NotFoundException("Категорію з id=" + request.categoryId() + " не знайдено"));
        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setBrand(request.brand());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCategory(category);
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Товар з id=" + id + " не знайдено"));
    }
}
