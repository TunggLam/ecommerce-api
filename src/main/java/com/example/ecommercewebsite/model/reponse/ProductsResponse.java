package com.example.ecommercewebsite.model.reponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductsResponse {

    private int totalElements;

    private List<ProductResponse> products;
}
