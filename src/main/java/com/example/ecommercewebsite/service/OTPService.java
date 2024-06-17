package com.example.ecommercewebsite.service;

import com.example.ecommercewebsite.model.request.SendOTPRequest;

public interface OTPService {
    void sendOTP(SendOTPRequest request);
}
