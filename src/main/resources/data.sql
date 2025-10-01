INSERT INTO product (ID, NAME, SKU, VENDOR, STOCK_QUANTITY)
SELECT 1, 'Product 1', 'SKU001', 'Vendor A', 10 UNION ALL
SELECT 2, 'Product 2', 'SKU002', 'Vendor B', 20 UNION ALL
SELECT 3, 'Product 3', 'SKU003', 'Vendor C', 30 UNION ALL
SELECT 4, 'Product 4', 'SKU004', 'Vendor A', 40 UNION ALL
SELECT 5, 'Product 5', 'SKU005', 'Vendor B', 50 UNION ALL
SELECT 6, 'Product 6', 'SKU060', 'Vendor C', 60;
