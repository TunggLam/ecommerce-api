package com.example.ecommercewebsite.controller;

import com.example.ecommercewebsite.aop.Secured;
import com.example.ecommercewebsite.enums.RoleEnum;
import com.example.ecommercewebsite.model.reponse.ProductResponse;
import com.example.ecommercewebsite.model.reponse.ProductsResponse;
import com.example.ecommercewebsite.model.request.CreateProductRequest;
import com.example.ecommercewebsite.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<ProductsResponse> getProducts(@RequestParam("page") int page,
                                                        @RequestParam("size") int size,
                                                        @RequestParam(value = "categoryId", required = false) String categoryId,
                                                        @RequestParam(value = "name", required = false) String name){
        return ResponseEntity.ok(productService.getProducts(page, size, categoryId, name));
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable("id") String id){
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @Secured(role = RoleEnum.ADMIN)
    @PostMapping(value = "/product")
    public ResponseEntity<ProductResponse> createProduct(@Valid @ModelAttribute CreateProductRequest request){
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @Secured(role = RoleEnum.ADMIN)
    @PutMapping("/product/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable("id") String id, @Valid @ModelAttribute CreateProductRequest request){
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }
}
