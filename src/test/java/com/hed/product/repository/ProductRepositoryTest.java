package com.hed.product.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import(ProductRepository.class)
class ProductRepositoryTest {
    @Autowired
    private ProductRepository repository;

    @Test
    @Sql("/schema.sql")
    public void testFindAll() {
        ProductEntity product1 = new ProductEntity(null, "NCB37", "Product 1", 3, "Vendor A");
        ProductEntity product2 = new ProductEntity(null, "KAF11", "Product 2", 8, "Vendor B");
        ProductEntity product3 = new ProductEntity(null, "LPV42", "Product 3", 4, "Vendor A");
        ProductEntity product4 = new ProductEntity(null, "PUS78", "Product 4", 7, "Vendor B");
        repository.saveAll(List.of(product1, product2, product3, product4));

        Iterable<ProductEntity> products = repository.findAll();

        int count = 0;
        for(ProductEntity product: products) {
            assertNotNull(product.id());
            count += 1;
        }
        assertEquals(4, count);
    }

    @Test
    @Sql("/schema.sql")
    public void testSave() {
        ProductEntity product = new ProductEntity(null, "NCB37", "Product 1", 3, "Vendor A");
        repository.save(product);

        product = new ProductEntity(37L, "NCB37", "Product 3", 7, "Vendor A");
        repository.save(product);

        List<ProductEntity> persistedProducts = repository.findAll();

        assertEquals(1, persistedProducts.size());
        ProductEntity persistedProduct = persistedProducts.get(0);
        assertEquals(1, persistedProduct.id());
        assertEquals("NCB37", persistedProduct.sku());
        assertEquals("Product 3", persistedProduct.name());
        assertEquals(7, persistedProduct.stockQuantity());
        assertEquals("Vendor A", persistedProduct.vendor());
    }

    @Test
    public void testFindBySkuVendorAndInStock() {
        ProductEntity product1 = new ProductEntity(null, "NCB37", "Product 1", 3, "Vendor A");
        ProductEntity product2 = new ProductEntity(null, "KAF11", "Product 2", 8, "Vendor B");
        ProductEntity product3 = new ProductEntity(null, "LPV42", "Product 3", 4, "Vendor A");
        ProductEntity product4 = new ProductEntity(null, "PUS78", "Product 4", 7, "Vendor B");
        ProductEntity product5 = new ProductEntity(null, "YBR23", "Product 5", 0, "Vendor B");
        repository.saveAll(List.of(product1, product2, product3, product4, product5));

        List<Pair<String, String>> skuAndVendorPairs = List.of(
            new Pair("NCB37", "Vendor B"),
            new Pair("KAF11", "Vendor A"),
            new Pair("NCB37", "Vendor A"),
            new Pair("YBR23", "Vendor B")
        );
        List<ProductEntity> persistedProducts = repository.findBySkuVendorAndInStock(skuAndVendorPairs);
        assertEquals(1, persistedProducts.size());

    }
}
