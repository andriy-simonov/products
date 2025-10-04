DROP TABLE IF EXISTS out_of_stock_event;
DROP TABLE IF EXISTS product;

CREATE TABLE product (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(50) NOT NULL,
    name VARCHAR(100),
    stock_quantity INT,
    vendor VARCHAR(50) NOT NULL,
    CONSTRAINT uk_product_sku_vendor UNIQUE (sku, vendor)
);

CREATE TABLE out_of_stock_event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    date TIMESTAMP NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product(id)
);
