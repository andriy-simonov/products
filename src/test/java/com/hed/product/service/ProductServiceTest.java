package com.hed.product.service;

import com.hed.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringJUnitConfig(ProductService.class)
class ProductServiceTest {
    @MockitoBean
    private ProductRepository repository;

    @MockitoBean
    private ProductMapper mapper;

    @Autowired
    private ProductService service;

    @Test
    void getProducts() {
        service.getProducts();

        verify(repository).findAll();
        verify(mapper).map(any(Iterable.class));
    }
}
