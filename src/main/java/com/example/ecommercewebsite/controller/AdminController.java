package com.example.ecommercewebsite.controller;

import com.example.ecommercewebsite.aop.Secured;
import com.example.ecommercewebsite.enums.RoleEnum;
import com.example.ecommercewebsite.model.reponse.UserProfileResponse;
import com.example.ecommercewebsite.model.reponse.UserProfilesResponse;
import com.example.ecommercewebsite.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;



import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @Secured(role = RoleEnum.ADMIN)
    @GetMapping("/users")
    public ResponseEntity<UserProfilesResponse> getUsers(@RequestParam(required = false) int page,
                                                         @RequestParam(required = false) int size) {
        return ResponseEntity.ok(adminService.getUserProfiles(page, size));
    }

    @Secured(role = RoleEnum.ADMIN)
    @GetMapping("/user/{keycloakId}")
    public ResponseEntity<UserProfileResponse> getUserByKeycloakId(@PathVariable(name = "KeycloakId") String keycloakId){
        return ResponseEntity.ok(adminService.getUserByKeycloakId(keycloakId));
    }
}
