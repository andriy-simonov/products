package com.hed.product.service;

import com.hed.product.repository.ProductEntity;
import com.hed.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductMapper mapper;

    public List<Product> getProducts() {
        Iterable<ProductEntity> products = repository.findAll();

        return mapper.map(products);
    }
}
