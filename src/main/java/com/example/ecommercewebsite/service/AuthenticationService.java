package com.example.ecommercewebsite.service;

import com.example.ecommercewebsite.model.reponse.LoginResponse;
import com.example.ecommercewebsite.model.request.LoginRequest;
import com.example.ecommercewebsite.model.request.LogoutRequest;
import com.example.ecommercewebsite.model.request.RefreshTokenRequest;
import com.example.ecommercewebsite.model.request.RegisterRequest;

public interface AuthenticationService {
   LoginResponse register(RegisterRequest registerRequest);

   LoginResponse login(LoginRequest loginRequest);

   void logout(String username);

   LoginResponse refresh(RefreshTokenRequest refreshTokenRequest);
}
