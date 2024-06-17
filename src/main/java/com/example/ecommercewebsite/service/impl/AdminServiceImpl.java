package com.example.ecommercewebsite.service.impl;

import com.example.ecommercewebsite.constant.Constant;
import com.example.ecommercewebsite.entity.UserProfile;
import com.example.ecommercewebsite.exception.BusinessException;
import com.example.ecommercewebsite.mapper.UserProfileMapper;
import com.example.ecommercewebsite.model.reponse.UserProfileResponse;
import com.example.ecommercewebsite.model.reponse.UserProfilesResponse;
import com.example.ecommercewebsite.repositories.UserProfileRepository;
import com.example.ecommercewebsite.service.AdminService;
import com.example.ecommercewebsite.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    @Override
    public UserProfilesResponse getUserProfiles(int page, int size) {
        List<UserProfile> userProfiles = userProfileRepository.findUserProfileOrderByCreatedAtDesc();

        List<UserProfileResponse> userProfileResponses = new ArrayList<>();
        for (UserProfile userProfile : userProfiles.stream().skip((long) page * size).limit(size).toList()) {
            UserProfileResponse userProfileResponse = userProfileMapper.mapToUserProfileResponse(userProfile);
            userProfileResponses.add(userProfileResponse);
        }
        UserProfilesResponse userProfilesResponse = new UserProfilesResponse();
        userProfilesResponse.setPage(page);
        userProfilesResponse.setSize(size);
        userProfilesResponse.setTotal(userProfiles.size());
        userProfilesResponse.setUserProfiles(userProfileResponses);
        return userProfilesResponse;
    }

    private static int getTotalPage(int size, List<UserProfile> userProfiles) {
        int totalPage = (int) Math.ceil((double) userProfiles.size() / size);
        if(totalPage == 0) {
            return 1;
        }
        return totalPage;
    }

    @Override
    public UserProfileResponse getUserByKeycloakId(String keycloakId) {
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByKeycloakId(keycloakId);
        if(userProfileOptional.isEmpty()) {
            throw new BusinessException(Constant.USER_NOT_EXIST);
        }

        UserProfile userProfile = userProfileOptional.get();
        return userProfileMapper.mapToUserProfileResponse(userProfile);
    }
}
