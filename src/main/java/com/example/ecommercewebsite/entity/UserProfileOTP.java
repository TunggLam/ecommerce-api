package com.example.ecommercewebsite.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_profile_otp")
public class UserProfileOTP extends BaseEntity{
    private String username;

    private String otp;

    private String type;

    private Boolean status;

    @Column(name = "verify_at")
    private LocalDateTime verifyAt;

    @Column(name = "count_verify_false")
    private Integer countVerifyFalse;

    @Column(name = "is_verified")
    private Boolean isVerified;
}
