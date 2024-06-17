package com.example.ecommercewebsite.model.reponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfilesResponse {
    private int page;

    private int size;

    private int total;

    private List<UserProfileResponse> userProfiles;
}
