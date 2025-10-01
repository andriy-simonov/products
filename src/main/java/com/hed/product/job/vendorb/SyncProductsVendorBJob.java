package com.hed.product.job.vendorb;

import com.hed.product.service.Product;
import com.hed.product.service.ProductService;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.ColumnPositionMappingStrategyBuilder;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class SyncProductsVendorBJob {
    private final Logger logger = LoggerFactory.getLogger(SyncProductsVendorBJob.class);

    private final String CRON_EXPRESSION = "${app.sync-job.vendor-b.cron}";

    @Value("${app.sync-job.ftp-drop.path}")
    private String filePath;

    @Value("${app.sync-job.vendor-b.batch-size}")
    private int batchSize;

    @Autowired
    private ProductService service;

    @Scheduled(cron = CRON_EXPRESSION)
    public void synchronize() {
        try (FileReader fileReader = new FileReader(filePath)) {
            Iterator<Product> it = readProducts(fileReader);
            processProductsInBatches(it);
        } catch (RuntimeException e) {
            logger.error("Failed to parse CSV file", e);
        } catch (IOException e) {
            logger.error("Failed to open CSV file", e);
        }
    }

    void processProductsInBatches(Iterator<Product> it) {
        List<Product> products = new ArrayList<>();
        int i = 0;
        while (it.hasNext()) {
            products.add(it.next());
            i += 1;
            if (i == batchSize) {
                service.syncProducts(products);
                products.clear();
                i = 0;
            }
        }
        if (!products.isEmpty()) {
            service.syncProducts(products);
        }
    }

    private Iterator<Product> readProducts(FileReader fileReader) {
        String[] columns = new String[]{"id", "ski", "name", "stockQuantity", "vendor"};
        ColumnPositionMappingStrategy<Product> strategy = new ColumnPositionMappingStrategyBuilder<Product>().build();
        strategy.setType(Product.class);
        strategy.setColumnMapping(columns);

        return new CsvToBeanBuilder<Product>(fileReader)
            .withMappingStrategy(strategy)
            .build()
            .iterator();
    }
}
