package com.hed.product.service;

import com.hed.product.repository.OutOfStockEventEntity;
import com.hed.product.repository.OutOfStockEventRepository;
import com.hed.product.repository.ProductRepository;
import com.hed.product.repository.ProductEntity;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(ProductService.class)
class ProductServiceTest {
    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private OutOfStockEventRepository eventRepository;

    @MockitoBean
    private ProductMapper mapper;

    @Autowired
    private ProductService service;

    @Test
    public void testGetProducts() {
        service.getProducts();

        verify(productRepository).findAll();
        verify(mapper).mapEntities(any(Iterable.class));
    }

    @Test
    public void testSyncProducts() {
        Product product1 = new Product(null, "PFK46", "Product 1", 37, "Vendor 1");
        Product product2 = new Product(null, "BZR17", "Product 2", 0, "Vendor 1");
        Product product3 = new Product(null, "APK08", "Product 3", 11, "Vendor 2");
        Product product4 = new Product(null, "LOF91", "Product 4", 0, "Vendor 2");
        Product product5 = new Product(null, "KEB45", "Product 4", 0, "Vendor 2");
        List<Product> products = List.of(product1, product2, product3, product4, product5);

        ProductEntity entity2 = new ProductEntity(3L, "BZR17", "Product 2", 5, "Vendor 1");
        ProductEntity entity4 = new ProductEntity(5L, "LOF91", "Product 4", 2, "Vendor 2");
        when(productRepository.findAll(any(Specification.class)))
            .thenReturn(List.of(entity2, entity4));

        service.syncProducts(products);

        ArgumentCaptor<List<OutOfStockEventEntity>> saveAllEventsCaptor = ArgumentCaptor.forClass(List.class);
        verify(eventRepository).saveAll(saveAllEventsCaptor.capture());
        List<OutOfStockEventEntity> eventsToPersists = saveAllEventsCaptor.getValue();
        assertEquals(2, eventsToPersists.size());

        assertNull(eventsToPersists.get(0).getId());
        assertEquals(3L, eventsToPersists.get(0).getProduct().getId());
        assertNotNull(eventsToPersists.get(0).getDate());

        assertEquals(5L, eventsToPersists.get(1).getProduct().getId());

        verify(productRepository).saveAll(any(Iterable.class));
    }
}
