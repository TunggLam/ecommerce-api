package com.example.ecommercewebsite.controller;

import com.example.ecommercewebsite.model.reponse.LoginResponse;
import com.example.ecommercewebsite.model.request.LoginRequest;
import com.example.ecommercewebsite.model.request.LogoutRequest;
import com.example.ecommercewebsite.model.request.RefreshTokenRequest;
import com.example.ecommercewebsite.model.request.RegisterRequest;
import com.example.ecommercewebsite.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest login) {
        return ResponseEntity.ok(authenticationService.login(login));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest logout) {
        authenticationService.logout(logout.getUsername());
        return ResponseEntity.ok().build();
    }
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshTokenRequest refresh){
        return ResponseEntity.ok(authenticationService.refresh(refresh));
    }
}
