package com.hed.product.job.vendora;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SyncProductsVendorAJob {
    private final Logger logger = LoggerFactory.getLogger(SyncProductsVendorAJob.class);

    private final String CRON_EXPRESSION = "${app.sync-job.vendor-a.cron}";

    @Scheduled(cron = CRON_EXPRESSION)
    public void synchronize() {
        logger.info("syncA");
    }
}
