package com.hed.product.service;

import com.hed.product.repository.OutOfStockEventEntity;
import com.hed.product.repository.OutOfStockEventRepository;
import com.hed.product.repository.ProductEntity;
import com.hed.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OutOfStockEventRepository eventRepository;

    @Autowired
    private ProductMapper mapper;

    public List<Product> getProducts() {
        Iterable<ProductEntity> products = productRepository.findAll();

        return mapper.mapEntities(products);
    }

    public void syncProducts(List<Product> products) {
        List<Pair<String, String>> outOfStockProducts = new ArrayList<>();
        for (Product product: products) {
            if (product.stockQuantity() == 0) {
                outOfStockProducts.add(Pair.of(product.sku(), product.vendor()));
            }
        }
        Iterable<ProductEntity> recentlySoldOutProducts =
            productRepository.findAll(ProductRepository.bySkuVendorAndInStock(outOfStockProducts));
        List<OutOfStockEventEntity> events = new ArrayList<>();
        for (ProductEntity recentlySoldOutProduct: recentlySoldOutProducts) {
            logger.info("Product went out of stock {}", recentlySoldOutProduct.getId());
            events.add(new OutOfStockEventEntity(recentlySoldOutProduct));
        }
        eventRepository.saveAll(events);
        productRepository.saveAll(mapper.map(products));
    }
}
