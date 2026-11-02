package com.example.backend.service;

import com.example.backend.dto.PageResponse;
import com.example.backend.dto.ProductResponse;
import com.example.backend.dto.StockAdjustmentRequest;
import com.example.backend.dto.StockIncomeRequest;
import com.example.backend.dto.StockMovementResponse;
import com.example.backend.dto.WarehouseSummaryResponse;
import com.example.backend.entity.MovementType;
import com.example.backend.entity.Order;
import com.example.backend.entity.Product;
import com.example.backend.entity.StockMovement;
import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.NotFoundException;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Облік товарів на складі. Будь-яка зміна залишку проходить через changeStock,
// тож журнал рухів завжди відповідає фактичним залишкам.
@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;

    // Надходження товару від постачальника
    @Transactional
    public StockMovementResponse receive(Long productId, StockIncomeRequest request) {
        Product product = getProductForUpdate(productId);
        StockMovement movement = changeStock(product, request.quantity(), MovementType.INCOME, null,
                textOrDefault(request.comment(), "Надходження на склад"));
        return StockMovementResponse.from(movement);
    }

    // Інвентаризація: виставляємо фактичний залишок
    @Transactional
    public StockMovementResponse adjust(Long productId, StockAdjustmentRequest request) {
        Product product = getProductForUpdate(productId);
        int delta = request.actualStock() - product.getStock();
        if (delta == 0) {
            throw new BadRequestException("Залишок не змінився");
        }
        StockMovement movement = changeStock(product, delta, MovementType.ADJUSTMENT, null,
                textOrDefault(request.comment(), "Інвентаризація"));
        return StockMovementResponse.from(movement);
    }

    @Transactional(readOnly = true)
    public PageResponse<StockMovementResponse> movements(Long productId, MovementType type, Pageable pageable) {
        return PageResponse.from(stockMovementRepository.search(productId, type, pageable)
                .map(StockMovementResponse::from));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> lowStock(int threshold) {
        return productRepository.findByStockLessThanEqualOrderByStockAscNameAsc(threshold).stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public WarehouseSummaryResponse summary(int lowStockThreshold) {
        return new WarehouseSummaryResponse(
                productRepository.count(),
                productRepository.sumStock(),
                productRepository.sumStockValue(),
                productRepository.countByStock(0),
                productRepository.countByStockLessThanEqual(lowStockThreshold)
        );
    }

    // Змінює залишок на delta і записує рух у журнал.
    // Викликається всередині транзакції сервісу, який вже заблокував товар.
    @Transactional
    public StockMovement changeStock(Product product, int delta, MovementType type, Order order, String comment) {
        int newStock = product.getStock() + delta;
        if (newStock < 0) {
            throw new ConflictException("Недостатньо товару '" + product.getName()
                    + "' на складі: доступно " + product.getStock());
        }
        product.setStock(newStock);

        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setType(type);
        movement.setQuantity(delta);
        movement.setStockAfter(newStock);
        movement.setOrder(order);
        movement.setComment(comment);
        return stockMovementRepository.save(movement);
    }

    private Product getProductForUpdate(Long id) {
        return productRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFoundException("Товар з id=" + id + " не знайдено"));
    }

    private static String textOrDefault(String text, String defaultText) {
        return (text == null || text.isBlank()) ? defaultText : text.trim();
    }
}
