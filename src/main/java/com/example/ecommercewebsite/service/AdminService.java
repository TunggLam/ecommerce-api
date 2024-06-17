package com.example.ecommercewebsite.service;

import com.example.ecommercewebsite.model.reponse.UserProfileResponse;
import com.example.ecommercewebsite.model.reponse.UserProfilesResponse;

public interface AdminService {
    UserProfilesResponse getUserProfiles(int page, int size);

    UserProfileResponse getUserByKeycloakId(String keycloakId);
}
