package com.example.ecommercewebsite.service.impl;

import com.example.ecommercewebsite.constant.Constant;
import com.example.ecommercewebsite.service.KeycloakService;
import com.example.ecommercewebsite.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
public class KeycloakServiceImpl implements KeycloakService{

    private final KeycloakSpringBootProperties keycloakSpringBootProperties;

    @Override
    public Keycloak getKeycloakByClient() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakSpringBootProperties.getAuthServerUrl())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .realm(keycloakSpringBootProperties.getRealm())
                .clientId(keycloakSpringBootProperties.getResource())
                .clientSecret(keycloakSpringBootProperties.getCredentials().get("secret").toString()).build();
    }

    @Override
        public Keycloak getKeycloack(String username, String password) {
            return Keycloak.getInstance(
                    keycloakSpringBootProperties.getAuthServerUrl(),
                    keycloakSpringBootProperties.getRealm(),
                    username,
                    password,
                    keycloakSpringBootProperties.getResource(),
                    keycloakSpringBootProperties.getCredentials().get("secret").toString()
            );
    }

    @Override
    public CredentialRepresentation credentialRepresentation(String password) {
        CredentialRepresentation passwordCred = new CredentialRepresentation();

        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);

        if (StringUtils.isNullOrEmpty(password)){
            passwordCred.setValue(Constant.PASSWORD_DEFAULT);
        } else {
            passwordCred.setValue(password);
        }
        return passwordCred;
    }
}
