package com.hed.product.service;

import com.hed.product.repository.ProductEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
interface ProductMapper {
    List<Product> mapEntities(Iterable<ProductEntity> entities);

    List<ProductEntity> map(Iterable<Product> products);
}
