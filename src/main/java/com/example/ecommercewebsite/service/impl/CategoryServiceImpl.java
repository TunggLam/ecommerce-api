package com.example.ecommercewebsite.service.impl;

import com.example.ecommercewebsite.constant.Constant;
import com.example.ecommercewebsite.entity.Category;
import com.example.ecommercewebsite.exception.BusinessException;
import com.example.ecommercewebsite.mapper.CategoriesMapper;
import com.example.ecommercewebsite.model.reponse.CategoriesResponse;
import com.example.ecommercewebsite.model.reponse.CategoryResponse;
import com.example.ecommercewebsite.model.request.CreateCategoryRequest;
import com.example.ecommercewebsite.model.request.UpdateCategoryRequest;
import com.example.ecommercewebsite.repositories.CategoryRepository;
import com.example.ecommercewebsite.service.CategoryService;
import com.example.ecommercewebsite.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoriesMapper categoriesMapper;
    @Override
    public CategoriesResponse findAll() {
        List<Category> categories = categoryRepository.getALLCategoryActive();

        List<CategoryResponse> categoryResponses = categories.stream().map(categoriesMapper::mapToCategoryResponse).toList();

        CategoriesResponse categoriesResponse = new CategoriesResponse();

        categoriesResponse.setCategories(categoryResponses);

        return categoriesResponse;
    }

    @Override
    public void create(CreateCategoryRequest request) {
        if(StringUtils.isNullOrEmpty(request.getName())) {
            throw new BusinessException(Constant.PRODUCT_EMPTY);
        }

        Boolean isExist = categoryRepository.existsByName(request.getName());
        if(Boolean.TRUE.equals(isExist)) {
            throw new BusinessException(Constant.PRODUCT_EXIST);
        }

        Category category = new Category(request.getName(), true);
        categoryRepository.save(category);
    }

    @Override
    public CategoryResponse update(String id, UpdateCategoryRequest request) {
        validateRequest(id, request.getName());

        Category category = categoryRepository.findById(id).orElse(null);

        if(category == null){
            throw new BusinessException("Không tìm thấy thông tin loại sản phẩm này");
        }

        updateCategory(request, category);

        return categoriesMapper.mapToCategoryResponse(category);
    }

    @Override
    public void delete(String id) {
        if(StringUtils.isNullOrEmpty(id)){
            throw new BusinessException(Constant.ID_EMPTY);
        }

        Category category = categoryRepository.findById(id).orElse(null);
        if(category == null){
            throw new BusinessException("Không có loại sản phẩm tương ứng với id này");
        }

        category.setActive(false);
        categoryRepository.save(category);

    }


    private void updateCategory(UpdateCategoryRequest request, Category category){
        category.setName(request.getName());
        category.setActive(request.isActive());
        categoryRepository.save(category);
    }

    private static void validateRequest(String id, String name){
        if(StringUtils.isNullOrEmpty(id)){
            throw new BusinessException(Constant.ID_EMPTY);
        }
        if(StringUtils.isNullOrEmpty(name)){
            throw new BusinessException((Constant.PRODUCT_EMPTY));
        }
    }

}
