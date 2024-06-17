package com.example.ecommercewebsite.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.ecommercewebsite.constant.Constant;
import com.example.ecommercewebsite.entity.UserProfile;
import com.example.ecommercewebsite.entity.UserProfileOTP;
import com.example.ecommercewebsite.enums.OTPTypeEnum;
import com.example.ecommercewebsite.exception.AuthenticationException;
import com.example.ecommercewebsite.exception.BusinessException;
import com.example.ecommercewebsite.mapper.AuthenticationMapper;
import com.example.ecommercewebsite.model.reponse.LoginResponse;
import com.example.ecommercewebsite.model.reponse.RolesUserResponse;
import com.example.ecommercewebsite.model.request.LoginRequest;
import com.example.ecommercewebsite.model.request.RefreshTokenRequest;
import com.example.ecommercewebsite.model.request.RegisterRequest;
import com.example.ecommercewebsite.proxy.KeycloakProxy;
import com.example.ecommercewebsite.redis.TokenRedisService;
import com.example.ecommercewebsite.repositories.UserProfileOTPRepository;
import com.example.ecommercewebsite.repositories.UserProfileRepository;
import com.example.ecommercewebsite.service.AuthenticationService;
import com.example.ecommercewebsite.service.KeycloakService;
import com.example.ecommercewebsite.service.UserProfileService;
import com.example.ecommercewebsite.utils.JWTUtils;
import com.example.ecommercewebsite.utils.PasswordUtils;
import com.example.ecommercewebsite.utils.StringUtils;
import lombok.RequiredArgsConstructor;

import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final int TOTAL_FALSE_OTP = 5;
    private static final String ROLE_USER = "ROLE_USER";

    private final UserProfileService userProfileService;
    private final UserProfileOTPRepository userProfileOTPRepository;
    private final KeycloakService keycloakService;
    private final KeycloakSpringBootProperties keycloakSpringBootProperties;
    private final UserProfileRepository userProfileRepository;
    private final TokenRedisService tokenRedisService;
    private final AuthenticationMapper authenticationMapper;
    private final KeycloakProxy keycloakProxy;

    @Override
    public LoginResponse register(RegisterRequest request){
        String username = request.getUsername();

        UserProfile userProfile = userProfileService.findUserProfileByUsername(username);
        if(Objects.nonNull(userProfile)){
            throw new BusinessException(Constant.USERNAME_EXISTS);
        }

        UserProfileOTP userProfileOTP = userProfileOTPRepository.getLatestOTP(username, OTPTypeEnum.REGISTER.name());

        if(Objects.isNull(userProfileOTP)){
            throw new BusinessException(Constant.OTP_EXPIRED_OR_INVALID_MES);
        }

        int countVerifyFail = Objects.isNull(userProfileOTP.getCountVerifyFalse()) ? 0 : userProfileOTP.getCountVerifyFalse();

        if (Boolean.FALSE.equals(userProfileOTP.getStatus()) && countVerifyFail >= TOTAL_FALSE_OTP) {
            throw new BusinessException(Constant.VERIFY_OTP_5TH);
        }

        saveUserProfileOTP(request, userProfileOTP, countVerifyFail);

        UserRepresentation userRepresentation = buildUserRepresentation(request);

        Keycloak keycloakInstance = keycloakService.getKeycloakByClient();

        UsersResource usersResource = keycloakInstance.realm(keycloakSpringBootProperties.getRealm()).users();

        try (Response userCreateResponse = usersResource.create(userRepresentation)) {

            if(userCreateResponse.getStatus() == 201) {
                String keycloakId = CreatedResponseUtil.getCreatedId(userCreateResponse);

                saveUserProfile(request, keycloakId, username);

                UserResource userResource = usersResource.get(keycloakId);

                userResource.resetPassword(keycloakService.credentialRepresentation(request.getPassword()));

                List<RoleRepresentation> roleRepresentationList = userResource.roles().realmLevel().listAvailable();

                for (RoleRepresentation roleRepresentation : roleRepresentationList) {
                    if (roleRepresentation.getName().equals(ROLE_USER)) {
                        userResource.roles().realmLevel().add(List.of(roleRepresentation));
                        break;
                    }
                }

                userResource.update(new UserRepresentation());
            } else if (userCreateResponse.getStatus() == 409) {
                throw new BusinessException(Constant.USERNAME_EXISTS);
            } else {
                throw new BusinessException(Constant.ERROR_REGISTER_USER);
            }
        }

        userProfileOTPRepository.inactiveAllStatus(username, OTPTypeEnum.REGISTER.name());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(request.getUsername());
        loginRequest.setPassword(request.getPassword());

        return login(loginRequest);
    }

    @Override
    public LoginResponse login(LoginRequest login) {
        if (StringUtils.isNullOrEmpty(login.getUsername()) || StringUtils.isNullOrEmpty(login.getPassword())) {
            throw new BusinessException(Constant.USERNAME_AND_PASSWORD_EMPTY);
        }

        String username = login.getUsername();

        UserProfile userProfile = userProfileService.findUserProfileByUsername(username);

        if(Objects.isNull(userProfile)){
            throw new BusinessException(Constant.USERNAME_AND_PASSWORD_NOT_EXIST);
        }

        if(Boolean.FALSE.equals(userProfile.getIsActive())){
            throw new BusinessException(Constant.USERNAME_AND_PASSWORD_IS_BLOCK);
        }

        if(StringUtils.notEquals(userProfile.getPassword(), PasswordUtils.endCodeMD5(login.getPassword()))){
            throw new BusinessException(Constant.USERNAME_AND_PASSWORD_WRONG);
        }

        logout(username);

        Keycloak keycloak = keycloakService.getKeycloack(username, login.getPassword());

        AccessTokenResponse accessTokenKeycloak = keycloak.tokenManager().getAccessToken();

        DecodedJWT jwtDecode = JWT.decode(accessTokenKeycloak.getToken());
        List<String> roles = jwtDecode.getClaim("realm_access").as(RolesUserResponse.class).getRoles().stream().filter(role -> role.startsWith("ROLE")).toList();

        if(roles.isEmpty()) {
            throw new BusinessException(Constant.ROLE_NO_RIGHT, 403);
        }

        saveTokenToRedis(username, accessTokenKeycloak);
        return authenticationMapper.mapToLoginResponse(accessTokenKeycloak, roles);
    }
    @Override
    public void logout(String username){
        if(StringUtils.isNullOrEmpty(username)) {
            return;
        }

        String keycloakId = userProfileRepository.getKeyCloackIdByUsername(username);

        Keycloak keycloak = keycloakService.getKeycloakByClient();
        UserResource userResource = keycloak.realm(keycloakSpringBootProperties.getRealm()).users().get(keycloakId);
        List<UserSessionRepresentation> userSessions = userResource.getUserSessions();

        if(!userSessions.isEmpty()){
            userResource.logout();
        }

        tokenRedisService.remove(username);

    }
    @Override
    public LoginResponse refresh(RefreshTokenRequest refresh){
        validateRequest(refresh);

        String username = refresh.getUsername();

        validateRefreshToken(refresh, username);

        AccessTokenResponse accessTokenResponse = keycloakProxy.refreshToken(refresh.getRefreshToken());

        if (accessTokenResponse == null || accessTokenResponse.getToken() == null || accessTokenResponse.getRefreshToken() == null) {
            throw new AuthenticationException();
        }

        saveTokenToRedis(refresh.getUsername(), accessTokenResponse);

        List<String> roles = JWTUtils.getRoles(accessTokenResponse.getToken());

        return authenticationMapper.mapToLoginResponse(accessTokenResponse, roles);
    }

    private static void validateRequest(RefreshTokenRequest refresh){
        if (StringUtils.isNullOrEmpty(refresh.getRefreshToken()) || StringUtils.isNullOrEmpty((refresh.getUsername()))){
            throw new BusinessException("Username hoặc refresh không được bỏ trống");
        }
    }

    private void validateRefreshToken(RefreshTokenRequest refresh, String username){
        String refreshTokenRedis = tokenRedisService.getRefreshToken(refresh.getUsername());
        if(StringUtils.notEquals(refreshTokenRedis, refresh.getRefreshToken())){
            throw new AuthenticationException();
        }

        String tokenRedis = tokenRedisService.get(refresh.getUsername());
        if(!StringUtils.isNullOrEmpty(tokenRedis)){
            throw new AuthenticationException();
        }

    }



    private void saveUserProfileOTP(RegisterRequest request, UserProfileOTP userProfileOTP, int countVerifyFail) {
        if(StringUtils.notEquals(userProfileOTP.getOtp(), request.getOtp())){
            userProfileOTP.setCountVerifyFalse(countVerifyFail + 1);
            userProfileOTPRepository.save(userProfileOTP);
            throw new BusinessException(Constant.OTP_EXPIRED_OR_INVALID_MES);
        } else {
            userProfileOTP.setIsVerified(true);
            userProfileOTP.setVerifyAt(LocalDateTime.now());
            userProfileOTPRepository.save(userProfileOTP);
        }
    }

    private static UserRepresentation buildUserRepresentation(RegisterRequest request){
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setUsername(request.getUsername());
        userRepresentation.setFirstName(request.getFirstName());
        userRepresentation.setLastName(request.getLastName());
        userRepresentation.setEmail(request.getEmail());
        userRepresentation.setEmailVerified(true);
        return userRepresentation;
    }

    private void saveUserProfile(RegisterRequest request, String keycloakId, String username) {
        UserProfile userProfile = new UserProfile();
        userProfile.setAddress(request.getAddress());
        userProfile.setEmail(request.getEmail());
        userProfile.setFirstName(request.getFirstName());
        userProfile.setLastName(request.getLastName());
        userProfile.setKeycloakId(keycloakId);
        userProfile.setUsername(username);
        userProfile.setIsActive(true);
        userProfile.setPhoneNumber(request.getPhoneNumber());
        userProfile.setPassword(PasswordUtils.endCodeMD5(request.getPassword()));
        userProfileRepository.save(userProfile);
    }

    private void saveTokenToRedis(String username, AccessTokenResponse accessTokenResponse) {
        Date expiredTimeToken = JWTUtils.getExpiredTime(accessTokenResponse.getToken());

        Date expiredTimeRefreshToken = JWTUtils.getExpiredTime(accessTokenResponse.getRefreshToken());

        tokenRedisService.set(username, accessTokenResponse.getToken(), expiredTimeToken);

        tokenRedisService.setRefreshToken(username, accessTokenResponse.getRefreshToken(), expiredTimeRefreshToken);
    }
}
