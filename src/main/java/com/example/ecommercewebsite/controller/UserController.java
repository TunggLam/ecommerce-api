package com.example.ecommercewebsite.controller;

import com.example.ecommercewebsite.aop.Secured;
import com.example.ecommercewebsite.enums.RoleEnum;
import com.example.ecommercewebsite.model.reponse.UserProfileResponse;
import com.example.ecommercewebsite.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserProfileService userProfileService;

    @Secured(role = RoleEnum.USER)
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile() {
        return ResponseEntity.ok(userProfileService.myProfile());
    }
}
