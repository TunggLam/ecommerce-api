package com.example.ecommercewebsite.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;

public interface KeycloakService {
    Keycloak getKeycloakByClient();

    Keycloak getKeycloack(String username, String password);

    CredentialRepresentation credentialRepresentation(String password);
}
