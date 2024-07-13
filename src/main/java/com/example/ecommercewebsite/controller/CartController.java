package com.example.ecommercewebsite.controller;

import com.example.ecommercewebsite.aop.Secured;
import com.example.ecommercewebsite.enums.RoleEnum;
import com.example.ecommercewebsite.model.reponse.CartsResponse;
import com.example.ecommercewebsite.model.request.AddCartRequest;
import com.example.ecommercewebsite.model.request.UpdateCartRequest;
import com.example.ecommercewebsite.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CartController {
    private final CartService cartService;

    @Secured(role = RoleEnum.USER)
    @GetMapping("/cart")
    public ResponseEntity<CartsResponse> carts() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @Secured(role = RoleEnum.USER)
    @PostMapping("/cart")
    public ResponseEntity<Void> addCart(@RequestBody AddCartRequest request) {
        cartService.addCart(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Secured(role = RoleEnum.USER)
    @PutMapping("/cart/{productId}")
    public ResponseEntity<Void> updateCart(@Valid @RequestBody UpdateCartRequest request,
                                           @PathVariable String productId) {
        cartService.updateCart(request, productId);
        return ResponseEntity.ok().build();
    }

    @Secured(role = RoleEnum.USER)
    @DeleteMapping("/cart/{productId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("productId") String productId) {
        cartService.removeCart(productId);
        return ResponseEntity.ok().build();
    }
}
