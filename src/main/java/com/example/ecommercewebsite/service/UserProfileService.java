package com.example.ecommercewebsite.service;

import com.example.ecommercewebsite.entity.UserProfile;
import com.example.ecommercewebsite.model.reponse.UserProfileResponse;

public interface UserProfileService {
    UserProfile findUserProfileByUsername(String username);

    UserProfileResponse myProfile();
}
