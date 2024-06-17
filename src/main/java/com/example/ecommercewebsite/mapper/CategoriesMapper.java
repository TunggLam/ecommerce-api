package com.example.ecommercewebsite.mapper;

import com.example.ecommercewebsite.entity.Category;
import com.example.ecommercewebsite.model.reponse.CategoryResponse;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface CategoriesMapper {
    CategoryResponse mapToCategoryResponse(Category category);
}
