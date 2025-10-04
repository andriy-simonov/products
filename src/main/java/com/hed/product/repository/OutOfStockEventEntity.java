package com.hed.product.repository;

import java.time.Instant;

public record OutOfStockEventEntity(Long id, Long productId, Instant date) {
}
