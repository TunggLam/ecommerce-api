package com.example.ecommercewebsite.service;

import com.example.ecommercewebsite.model.reponse.ProductResponse;
import com.example.ecommercewebsite.model.reponse.ProductsResponse;
import com.example.ecommercewebsite.model.request.CreateCategoryRequest;
import com.example.ecommercewebsite.model.request.CreateProductRequest;

public interface ProductService {
    ProductsResponse getProducts(int page, int size, String categoryId, String name);

    ProductResponse getProduct(String id);

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse updateProduct(String id, CreateProductRequest request);
}
