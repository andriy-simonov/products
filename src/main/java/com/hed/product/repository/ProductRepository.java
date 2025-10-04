package com.hed.product.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.join;
import static java.util.Collections.emptyList;

@Repository
public class ProductRepository {
    private final String SELECT = "SELECT * FROM product";
    private final String MERGE = "MERGE INTO product (sku, name, stock_quantity, vendor) "
        + "KEY(sku, vendor) VALUES (?, ?, ?, ?)";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<ProductEntity> productRowMapper = (rs, rowNum) ->
        new ProductEntity(
            rs.getLong("id"),
            rs.getString("sku"),
            rs.getString("name"),
            rs.getInt("stock_quantity"),
            rs.getString("vendor")
        );

    public List<ProductEntity> findAll() {
        return jdbcTemplate.query(SELECT, productRowMapper);
    }

    public List<ProductEntity> findBySkuVendorAndInStock(List<Pair<String, String>> skuAndVendorPairs) {
        if (skuAndVendorPairs == null) {
            throw new IllegalArgumentException("Sku and vendors list must not be null");
        }
        if (skuAndVendorPairs.isEmpty()) {
            return emptyList();
        }

        StringBuilder sql = new StringBuilder("SELECT * FROM product WHERE ");
        List<Object> params = new ArrayList<>();

        List<String> orClauses = new ArrayList<>();
        for (Pair<String, String> pair : skuAndVendorPairs) {
            orClauses.add("(sku = ? AND vendor = ?)");
            params.add(pair.first());
            params.add(pair.second());
        }

        sql.append("(")
            .append(join(" OR ", orClauses))
            .append(")");
        sql.append(" AND stock_quantity > 0");

        return jdbcTemplate.query(sql.toString(), params.toArray(), productRowMapper);
    }

    public int save(ProductEntity product) {
        return jdbcTemplate.update(MERGE,
            product.sku(),
            product.name(),
            product.stockQuantity(),
            product.vendor()
        );
    }

    public int[] saveAll(List<ProductEntity> products) {
        return jdbcTemplate.batchUpdate(MERGE,
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ProductEntity product = products.get(i);
                    ps.setString(1, product.sku());
                    ps.setString(2, product.name());
                    ps.setInt(3, product.stockQuantity());
                    ps.setString(4, product.vendor());
                }

                @Override
                public int getBatchSize() {
                    return products.size();
                }
            }
        );
    }
}
