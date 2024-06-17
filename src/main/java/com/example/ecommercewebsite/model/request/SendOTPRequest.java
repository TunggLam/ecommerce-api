package com.example.ecommercewebsite.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendOTPRequest {
    private String username;
    private String email;
    private String type;
}
