package com.hed.product.repository;

public record ProductEntity(Long id, String sku, String name, int stockQuantity, String vendor) {
}
