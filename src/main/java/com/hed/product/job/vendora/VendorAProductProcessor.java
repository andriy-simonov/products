package com.hed.product.job.vendora;

import com.hed.product.service.ProductService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
class VendorAProductProcessor {

    private final RestClient restClient;

    @Autowired
    private ProductService service;

    @Autowired
    private VendorAProductMapper mapper;

    public VendorAProductProcessor(RestClient.Builder builder, @Value("${app.sync-job.vendor-a.url}") String url) {
        this.restClient = builder.baseUrl(url).build();
    }

    @CircuitBreaker(name = "vendor-a-service-circuit-breaker")
    @Retry(name = "vendor-a-service-retry")
    public void process() {
        List<VendorAProduct> products = restClient.get()
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});
        service.syncProducts(mapper.map(products));
    }
}
