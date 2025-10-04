package com.hed.product.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import({OutOfStockEventRepository.class, ProductRepository.class})
class OutOfStockEventRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OutOfStockEventRepository eventRepository;

    @Test
    public void testSave() {
        productRepository.save(new ProductEntity(null, "APT32", "Product 1", 3, "Vendor A"));
        OutOfStockEventEntity event = new OutOfStockEventEntity(null, 1L, Instant.now());

        eventRepository.saveAll(List.of(event));

        List<OutOfStockEventEntity> persistedEvents = eventRepository.findAll();
        OutOfStockEventEntity persistedEvent = persistedEvents.get(0);
        assertEquals(1L, persistedEvent.id());
        assertEquals(1L, persistedEvent.productId());
        assertNotNull(persistedEvent.date());
    }
}
