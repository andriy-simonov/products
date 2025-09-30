package com.hed.product.service;

import com.hed.product.repository.ProductEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    List<Product> map(Iterable<ProductEntity> products);
}
