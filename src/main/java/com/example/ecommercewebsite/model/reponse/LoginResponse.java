package com.example.ecommercewebsite.model.reponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;

    private long expiresIn;

    private long refreshExpiresIn;

    private String refreshToken;

//  private String idToken;

    private String tokenType;

    private String sessionId;

    private List<String> roles;

}
