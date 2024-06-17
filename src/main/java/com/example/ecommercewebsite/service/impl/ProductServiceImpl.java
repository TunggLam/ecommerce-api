package com.example.ecommercewebsite.service.impl;

import com.example.ecommercewebsite.entity.Category;
import com.example.ecommercewebsite.entity.Product;
import com.example.ecommercewebsite.exception.BusinessException;
import com.example.ecommercewebsite.mapper.CategoriesMapper;
import com.example.ecommercewebsite.mapper.ProductMapper;
import com.example.ecommercewebsite.model.reponse.CategoriesResponse;
import com.example.ecommercewebsite.model.reponse.CategoryResponse;
import com.example.ecommercewebsite.model.reponse.ProductResponse;
import com.example.ecommercewebsite.model.reponse.ProductsResponse;
import com.example.ecommercewebsite.repositories.CategoryRepository;
import com.example.ecommercewebsite.repositories.ProductRepository;
import com.example.ecommercewebsite.repositories.spectification.ProductSpecification;
import com.example.ecommercewebsite.service.ProductService;
import com.example.ecommercewebsite.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductSpecification productSpecification;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final CategoriesMapper categoriesMapper;
    private final ProductRepository productRepository;


    @Override
    public ProductsResponse getProducts(int page, int size, String categoryId, String name) {
        List<Product> products = productSpecification.findAllByCategoryIdAndName(categoryId, name);
        List<ProductResponse> productResponses = new ArrayList<>();
        for(Product product : products.stream().skip((long)page * size).limit(size).toList()) {
            ProductResponse productResponse = productMapper.mapToProductResponse(product);
            Category category = categoryRepository.findById(product.getCategoryId()).orElse(null);
            CategoryResponse categoryResponse = categoriesMapper.mapToCategoryResponse(category);
            productResponse.setCategory(categoryResponse);
            productResponses.add(productResponse);
        }
        ProductsResponse productsResponse = new ProductsResponse();
        productsResponse.setProducts(productResponses);
        productsResponse.setTotalElements(products.size());
        return productsResponse;
    }

    @Override
    public ProductResponse getProduct(String id) {
        if(StringUtils.isNullOrEmpty(id)){
            throw new BusinessException("Id sản phẩm không được bỏ trống");
        }
        Product product = productRepository.findProductById(id).orElse(null);
        if(product == null){
            throw new BusinessException("Id sản phẩm này không tồn tại");
        }

        ProductResponse productResponse = productMapper.mapToProductResponse(product);

        return productResponse;
    }


}
