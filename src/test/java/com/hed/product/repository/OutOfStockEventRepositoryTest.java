package com.hed.product.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OutOfStockEventRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OutOfStockEventRepository eventRepository;

    @Test
    public void testSave() {
        ProductEntity product = productRepository.save(new ProductEntity());
        OutOfStockEventEntity event = new OutOfStockEventEntity(product);

        Iterable<OutOfStockEventEntity> persistedEvents = eventRepository.saveAll(List.of(event));

        Iterator<OutOfStockEventEntity> it = persistedEvents.iterator();
        while (it.hasNext()) {
            OutOfStockEventEntity persistedEvent = it.next();
            assertNotNull(persistedEvent.getId());
            assertNotNull(persistedEvent.getDate());
            assertEquals(product.getId(), persistedEvent.getProduct().getId());
        }
    }
}
