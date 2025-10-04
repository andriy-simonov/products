package com.hed.product.job.vendora;

import com.hed.product.service.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
interface VendorAProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vendor", constant = "Vendor A")
    abstract Product map(VendorAProduct product);

    abstract List<Product> map(Iterable<VendorAProduct> product);
}
