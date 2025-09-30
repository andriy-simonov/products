package com.hed.product.service;

public record Product(
    String id,
    String sku,
    String name,
    int stockQuantity,
    String vendor
) {
}
