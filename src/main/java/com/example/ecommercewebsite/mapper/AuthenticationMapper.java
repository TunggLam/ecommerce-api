package com.example.ecommercewebsite.mapper;

import com.example.ecommercewebsite.model.reponse.LoginResponse;
import org.keycloak.representations.AccessTokenResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface AuthenticationMapper {

    @Mapping(target = "accessToken", source = "accessTokenResponse.token")
    @Mapping(target = "sessionId", source = "accessTokenResponse.sessionState")
    LoginResponse mapToLoginResponse(AccessTokenResponse accessTokenResponse, List<String> roles);
}
