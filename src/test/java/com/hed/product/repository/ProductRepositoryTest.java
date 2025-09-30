package com.hed.product.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    private ProductRepository repository;

    @Test
    public void testFindAll() {
        ProductEntity product1 = new ProductEntity(null, "NCB37", "Product 1", 3, "Vendor 1");
        ProductEntity product2 = new ProductEntity(null, "KAF11", "Product 2", 8, "Vendor 2");
        ProductEntity product3 = new ProductEntity(null, "LPV42", "Product 3", 4, "Vendor 1");
        ProductEntity product4 = new ProductEntity(null, "PUS78", "Product 4", 7, "Vendor 2");
        repository.saveAll(List.of(product1, product2, product3, product4));

        Iterable<ProductEntity> products = repository.findAll();

        int count = 0;
        for(ProductEntity product: products) {
            assertNotNull(product.getId());
            count += 1;
        }
        assertEquals(4, count);
    }
}
