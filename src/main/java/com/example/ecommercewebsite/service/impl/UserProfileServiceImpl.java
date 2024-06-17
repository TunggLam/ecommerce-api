package com.example.ecommercewebsite.service.impl;

import com.example.ecommercewebsite.entity.UserProfile;
import com.example.ecommercewebsite.mapper.UserProfileMapper;
import com.example.ecommercewebsite.model.reponse.UserProfileResponse;
import com.example.ecommercewebsite.repositories.UserProfileRepository;
import com.example.ecommercewebsite.service.UserProfileService;
import com.example.ecommercewebsite.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService{
    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;


    public UserProfile findUserProfileByUsername(String username){
        return userProfileRepository.findByUsername(username).orElse(null);
    }

    @Override
    public UserProfileResponse myProfile() {
        String username = JWTUtils.getUsername();

        UserProfile userProfile = findUserProfileByUsername(username);

        return userProfileMapper.mapToUserProfileResponse(userProfile);

    }
}
