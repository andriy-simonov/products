package com.hed.product.controller;

import com.hed.product.service.ProductService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController implements ProductsApi {
    @Autowired
    private ProductService service;

    @Autowired
    private ProductModelMapper mapper;

    @RateLimiter(name = "product-controller")
    @Override
    public ResponseEntity<List<ProductModel>> getProducts() {
        List<ProductModel> products = mapper.map(service.getProducts());
        return ResponseEntity.ok(products);
    }
}
