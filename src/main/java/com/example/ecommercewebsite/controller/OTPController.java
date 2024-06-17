package com.example.ecommercewebsite.controller;

import com.example.ecommercewebsite.model.request.SendOTPRequest;
import com.example.ecommercewebsite.service.OTPService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/otp")
public class OTPController {

    private final OTPService otpService;

    @PostMapping("/send")
    public ResponseEntity<Void> sendOTP(@RequestBody SendOTPRequest request) {
        otpService.sendOTP(request);
        return ResponseEntity.ok().build();
    }
}
