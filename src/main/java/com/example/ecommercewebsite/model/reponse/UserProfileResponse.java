package com.example.ecommercewebsite.model.reponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private String keycloakId;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String address;

    private String username;

    private String password;

    private Boolean isActive;
}
