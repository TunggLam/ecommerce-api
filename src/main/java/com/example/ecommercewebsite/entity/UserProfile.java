package com.example.ecommercewebsite.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_profile")
public class UserProfile extends BaseEntity{
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
