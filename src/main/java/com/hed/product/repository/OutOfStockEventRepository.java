package com.hed.product.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class OutOfStockEventRepository {
    private final String SELECT = "SELECT * FROM out_of_stock_event";
    private final String INSERT = "INSERT INTO out_of_stock_event (product_id, date) VALUES (?, ?)";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<OutOfStockEventEntity> eventRowMapper = (rs, rowNum) ->
        new OutOfStockEventEntity(
            rs.getLong("id"),
            rs.getLong("product_id"),
            rs.getTimestamp("date").toInstant()
        );

    public List<OutOfStockEventEntity> findAll() {
        return jdbcTemplate.query(SELECT, eventRowMapper);
    }

    public int[] saveAll(List<OutOfStockEventEntity> events) {
        return jdbcTemplate.batchUpdate(INSERT, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    OutOfStockEventEntity event = events.get(i);
                    ps.setLong(1, event.productId());
                    ps.setTimestamp(2, Timestamp.from(event.date()));
                }

                @Override
                public int getBatchSize() {
                    return events.size();
                }
            }
        );
    }
}
