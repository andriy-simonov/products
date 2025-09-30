package com.hed.product.job.vendorb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SyncProductsVendorBJob {
    private final Logger logger = LoggerFactory.getLogger(SyncProductsVendorBJob.class);

    private final String CRON_EXPRESSION = "${app.sync-job.vendor-b.cron}";

    @Scheduled(cron = CRON_EXPRESSION)
    public void sync() {
        logger.info("syncB");
    }
}
