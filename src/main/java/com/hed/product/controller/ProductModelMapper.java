package com.hed.product.controller;

import com.hed.product.service.Product;
import org.mapstruct.Mapper;
import sync.stock.product.controller.ProductModel;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductModelMapper {
    ProductModel map(Product product);

    List<ProductModel> map(List<Product> modelList);
}
