package com.hed.product.job.vendorb;

import com.hed.product.service.Product;
import com.hed.product.service.ProductService;
import de.siegmar.fastcsv.reader.AbstractBaseCsvCallbackHandler;
import de.siegmar.fastcsv.reader.CsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import static java.lang.Integer.valueOf;

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
        try (Reader reader = new BufferedReader(new FileReader(filePath))) {
            CsvReader<Product> csv = CsvReader.builder()
                .skipEmptyLines(true)
                .build(new VendorBCsvCallbackHandler(), reader);
            Iterator<Product> it = csv.iterator();
            service.processProductsInBatches(it, batchSize);
            logger.info("Synchronized vendor B products");
        } catch (RuntimeException e) {
            logger.error("Failed to parse CSV file", e);
        } catch (IOException e) {
            logger.error("Failed to open CSV file", e);
        }
    }

    private static class VendorBCsvCallbackHandler extends AbstractBaseCsvCallbackHandler<Product> {
        private long recordCount;
        private String sku;
        private String name;
        private int stockQuantity;

        @Override
        protected void handleField(int fieldIdx, char[] buf, int offset, int len, boolean quoted) {
            if (recordCount == 0) {
                return;
            }
            switch (fieldIdx) {
                case 0 -> sku = new String(buf, offset, len);
                case 1 -> name = new String(buf, offset, len);
                case 2 -> stockQuantity = valueOf(new String(buf, offset, len));
            }
        }

        @Override
        protected Product buildRecord() {
            if (recordCount ++ == 0) {
                return null;
            }
            return new Product(null, sku, name, stockQuantity, "Vendor B");
        }
    }
}
