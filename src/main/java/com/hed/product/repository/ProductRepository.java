package com.hed.product.repository;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;

public interface ProductRepository extends CrudRepository<ProductEntity, Long>,
    JpaSpecificationExecutor<ProductEntity> {

    static Specification<ProductEntity> bySkuVendorAndInStock(List<Pair<String, String>> skuAndVendorPairs) {
        return (root, query, cb) -> {
            List<Predicate> orPredicates = new ArrayList<>();
            for (Pair<String, String> skuAndVendorPair: skuAndVendorPairs) {
                Predicate andPredicate = cb.and(
                    cb.equal(root.get("sku"), skuAndVendorPair.getFirst()),
                    cb.equal(root.get("vendor"), skuAndVendorPair.getSecond()),
                    cb.greaterThan(root.get("stockQuantity"), 0)
                );
                orPredicates.add(andPredicate);
            }
            return cb.or(orPredicates.toArray(new Predicate[0]));
        };
    }
}
