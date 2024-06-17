package com.example.ecommercewebsite.mapper;

import com.example.ecommercewebsite.entity.Product;
import com.example.ecommercewebsite.model.reponse.ProductResponse;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponse mapToProductResponse(Product product);
}
