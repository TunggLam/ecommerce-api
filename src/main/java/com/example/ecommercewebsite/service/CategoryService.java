package com.example.ecommercewebsite.service;

import com.example.ecommercewebsite.model.reponse.CategoriesResponse;
import com.example.ecommercewebsite.model.reponse.CategoryResponse;
import com.example.ecommercewebsite.model.request.CreateCategoryRequest;
import com.example.ecommercewebsite.model.request.UpdateCategoryRequest;

public interface CategoryService {
    CategoriesResponse findAll();

    void create(CreateCategoryRequest request);

    CategoryResponse update(String id, UpdateCategoryRequest request);

    void delete(String id);
}
