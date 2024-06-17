package com.example.ecommercewebsite.controller;

import com.example.ecommercewebsite.aop.Secured;
import com.example.ecommercewebsite.enums.RoleEnum;
import com.example.ecommercewebsite.model.reponse.CategoriesResponse;
import com.example.ecommercewebsite.model.reponse.CategoryResponse;
import com.example.ecommercewebsite.model.request.CreateCategoryRequest;
import com.example.ecommercewebsite.model.request.UpdateCategoryRequest;
import com.example.ecommercewebsite.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<CategoriesResponse> findALL() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @Secured(role = RoleEnum.ADMIN)
    @PostMapping("/category")
    public ResponseEntity<Void> create(@RequestBody CreateCategoryRequest request){
        categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Secured(role = RoleEnum.ADMIN)
    @PutMapping("/category/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable("id") String id,
                                                   @RequestBody UpdateCategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @Secured(role = RoleEnum.ADMIN)
    @DeleteMapping("/category/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String id){
        return ResponseEntity.ok().build();
    }

}
