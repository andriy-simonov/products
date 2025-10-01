package com.hed.product.job.vendorb;

import com.hed.product.service.Product;
import com.hed.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@SpringJUnitConfig(SyncProductsVendorBJob.class)
@TestPropertySource(properties = {"app.sync-job.vendor-b.batch-size=2"})
class SyncProductsVendorBJobTest {

    @MockitoBean
    private ProductService service;

    @Autowired
    private SyncProductsVendorBJob job;

    @Test
    void testSyncEvenNumberOfProducts() {
        Product product1 = new Product(null, "PFK46", "Product 1", 37, "Vendor 1");
        Product product2 = new Product(null, "BZR17", "Product 2", 0, "Vendor 1");
        Product product3 = new Product(null, "APK08", "Product 3", 11, "Vendor 2");
        Product product4 = new Product(null, "LOF91", "Product 4", 0, "Vendor 2");
        List<Product> evenNumberOfProducts = List.of(product1, product2, product3, product4);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Product>> syncCaptor = ArgumentCaptor.forClass(List.class);
        job.processProductsInBatches(evenNumberOfProducts.iterator());

        Mockito.verify(service, times(2)).syncProducts(syncCaptor.capture());

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
        job.processProductsInBatches(oddNumbersOfProducts.iterator());

        Mockito.verify(service, times(3)).syncProducts(syncCaptor.capture());

        List<List<Product>> capturedBatches = syncCaptor.getAllValues();
        assertEquals(3, capturedBatches.size());
    }
}
