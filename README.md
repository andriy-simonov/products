# 🛍️ Products Service

## 🚀 Build and Run Instructions

This project is hosted in a public GitHub repository and uses **Maven** for build automation.

### 🧱 Build the JAR

```bash
mvn clean package
```

### 🐳 Run with Docker Compose (Recommended)

```bash
docker compose up
```

### 🐳 Manual Docker Setup

1. Create a custom network:

   ```bash
   docker network create products-network
   ```

2. Build and run the products service:

   ```bash
   docker build -t products-service .
   docker run --rm -d --name products-service \
     -e VENDOR_A_URL=http://vendor-a-service/products \
     -e VENDOR_B_FTP_DROP=/tmp/vendor-b/stock.csv \
     -v /tmp/vendor-b/stock.csv:/tmp/vendor-b/stock.csv \
     -p 8080:8080 --network products-network products-service
   ```

3. Build and run Vendor A service:

   ```bash
   cd vendor-a-service
   docker build -t nginx-vendor-a .
   docker run --rm -d --name vendor-a-service \
     -p 8090:80 --network products-network nginx-vendor-a
   ```

---

## 🏪 Vendor Simulation

- **Vendor A**: Simulated via an Nginx container exposing a static JSON file with a list of products.
- **Vendor B**: Simulated as an FTP drop at `/tmp/vendor-b/stock.csv`.

> Both parameters are configurable for development convenience. A sample `stock.csv` is available in `./src/main/resources`.

---

## ⚙️ Assumptions and Trade-offs

- This service balances **ETL efficiency** with **general-purpose product management**, implementing a complete Spring Boot application rather than a specialized data pipeline.
- The service follows an **API-first approach** using OpenAPI specification. The API contract is defined in `./src/main/resources/product.yaml` and serves as the single source of truth for the REST API. During Maven's compile phase, a plugin automatically generates the controller interface and model classes from this specification. This ensures that implementation stays synchronized with the API contract and enables early validation of API design before implementation begins.
- The service implements **resilience patterns** using Resilience4j. Vendor A product updates employ retry and circuit breaker patterns to gracefully handle service failures and prevent cascading outages. The products API endpoint uses throttling to limit request rates and protect against overload. Together, these patterns ensure the service remains stable and responsive under adverse conditions.
- Each layer has dedicated DTOs for **decoupling**, which introduces **mapping overhead** but improves maintainability
- **Spring JDBC** is preferred over **JPA** for its ability to compose flexible SQL queries and execute efficient batch operations. This choice enables optimized bulk inserts and updates while eliminating unnecessary database round trips. Although JPA is simpler for general use cases, JDBC provides the control necessary for high-performance data processing, trading convenience for efficiency.
- **FastCSV** is chosen over **OpenCSV** for better support of modern Java features (e.g., records).

---

## 💡 Ideas for Improvement

- Add REST endpoint validation
- Implement pagination
- Improve error handling and logging for faulty execution flows
- Profile and fine-tune batch parameters for optimal performance and memory usage

---
