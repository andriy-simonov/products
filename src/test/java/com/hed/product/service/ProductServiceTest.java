package com.hed.product.service;

import com.hed.product.repository.OutOfStockEventEntity;
import com.hed.product.repository.OutOfStockEventRepository;
import com.hed.product.repository.ProductRepository;
import com.hed.product.repository.ProductEntity;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
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

    @MockitoSpyBean
    private ProductService service;

    @Test
    public void testGetProducts() {
        service.getProducts();

        verify(productRepository).findAll();
        verify(mapper).mapEntities(any(Iterable.class));
    }

    @Test
    public void testSyncProducts() {
        Product product1 = new Product(null, "PFK46", "Product 1", 37, "Vendor A");
        Product product2 = new Product(null, "BZR17", "Product 2", 0, "Vendor A");
        Product product3 = new Product(null, "APK08", "Product 3", 11, "Vendor B");
        Product product4 = new Product(null, "LOF91", "Product 4", 0, "Vendor B");
        Product product5 = new Product(null, "KEB45", "Product 5", 0, "Vendor B");
        List<Product> products = List.of(product1, product2, product3, product4, product5);

        ProductEntity entity2 = new ProductEntity(2L, "BZR17", "Product 2", 5, "Vendor A");
        ProductEntity entity4 = new ProductEntity(4L, "LOF91", "Product 4", 2, "Vendor B");
        when(productRepository.findBySkuVendorAndInStock(any(List.class)))
            .thenReturn(List.of(entity2, entity4));

        service.syncProducts(products);

        ArgumentCaptor<List<OutOfStockEventEntity>> saveAllEventsCaptor = ArgumentCaptor.forClass(List.class);
        verify(eventRepository).saveAll(saveAllEventsCaptor.capture());
        List<OutOfStockEventEntity> eventsToPersists = saveAllEventsCaptor.getValue();
        assertEquals(2, eventsToPersists.size());

        assertNull(eventsToPersists.get(0).id());
        assertEquals(2L, eventsToPersists.get(0).productId());
        assertNotNull(eventsToPersists.get(0).date());

        assertEquals(4L, eventsToPersists.get(1).productId());

        verify(productRepository).saveAll(any(List.class));
    }

    @Test
    void testSyncEvenNumberOfProducts() {
        Product product1 = new Product(null, "PFK46", "Product 1", 37, "Vendor 1");
        Product product2 = new Product(null, "BZR17", "Product 2", 0, "Vendor 1");
        Product product3 = new Product(null, "APK08", "Product 3", 11, "Vendor 2");
        Product product4 = new Product(null, "LOF91", "Product 4", 0, "Vendor 2");
        List<Product> evenNumberOfProducts = List.of(product1, product2, product3, product4);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Product>> syncCaptor = ArgumentCaptor.forClass(List.class);
        service.processProductsInBatches(evenNumberOfProducts.iterator(), 2);

        verify(service, times(2)).syncProducts(syncCaptor.capture());

        List<List<Product>> capturedBatches = syncCaptor.getAllValues();
        assertEquals(2, capturedBatches.size());
    }

    @Test
    void testSyncOddNumberOfProducts() {
        Product product1 = new Product(null, "PFK46", "Product 1", 37, "Vendor 1");
        Product product2 = new Product(null, "BZR17", "Product 2", 0, "Vendor 1");
        Product product3 = new Product(null, "APK08", "Product 3", 11, "Vendor 2");
        Product product4 = new Product(null, "LOF91", "Product 4", 0, "Vendor 2");
        Product product5 = new Product(null, "KTN28", "Product 5", 7, "Vendor 2");
        List<Product> oddNumbersOfProducts = List.of(product1, product2, product3, product4, product5);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Product>> syncCaptor = ArgumentCaptor.forClass(List.class);
        service.processProductsInBatches(oddNumbersOfProducts.iterator(), 2);

        verify(service, times(3)).syncProducts(syncCaptor.capture());

        List<List<Product>> capturedBatches = syncCaptor.getAllValues();
        assertEquals(3, capturedBatches.size());
    }
}
