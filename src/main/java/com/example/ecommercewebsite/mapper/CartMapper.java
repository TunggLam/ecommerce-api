package com.example.ecommercewebsite.mapper;

import com.example.ecommercewebsite.entity.Cart;
import com.example.ecommercewebsite.entity.Product;
import com.example.ecommercewebsite.model.reponse.CartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "quantity", source = "cart.quantity")
    CartResponse map(Product product, Cart cart);
}
