package com.hed.product.repository;

import org.springframework.data.repository.Repository;

public interface OutOfStockEventRepository extends Repository<OutOfStockEventEntity, Long> {
    Iterable<OutOfStockEventEntity> saveAll(Iterable<OutOfStockEventEntity> event);
}
