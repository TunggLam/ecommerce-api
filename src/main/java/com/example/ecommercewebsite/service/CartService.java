package com.example.ecommercewebsite.service;

import com.example.ecommercewebsite.model.reponse.CartsResponse;
import com.example.ecommercewebsite.model.request.AddCartRequest;
import com.example.ecommercewebsite.model.request.UpdateCartRequest;

public interface CartService {
    CartsResponse getCart();

    void addCart(AddCartRequest request);

    void removeCart(String id);

    void updateCart(UpdateCartRequest request, String productId);
}
