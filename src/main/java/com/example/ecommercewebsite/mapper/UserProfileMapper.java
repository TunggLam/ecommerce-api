package com.example.ecommercewebsite.mapper;

import com.example.ecommercewebsite.entity.UserProfile;
import com.example.ecommercewebsite.model.reponse.UserProfileResponse;
import org.mapstruct.Mapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel =  "spring")
public interface UserProfileMapper {
    UserProfileResponse mapToUserProfileResponse(UserProfile userProfile);

}
