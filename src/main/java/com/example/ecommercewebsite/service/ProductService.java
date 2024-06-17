package com.example.ecommercewebsite.service;

import com.example.ecommercewebsite.model.reponse.ProductResponse;
import com.example.ecommercewebsite.model.reponse.ProductsResponse;

public interface ProductService {
    ProductsResponse getProducts(int page, int size, String categoryId, String name);

    ProductResponse getProduct(String id);
}
