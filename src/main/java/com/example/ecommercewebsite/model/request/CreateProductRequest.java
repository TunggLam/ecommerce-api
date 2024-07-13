package com.example.ecommercewebsite.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequest {

    @NotNull(message = "The product name must not be left blank.")
    @NotEmpty(message = "The product name must not be left blank.")
    private String name;

    private String description;

    private List<ImageColorRequest> images;

    @NotNull(message = "The quantity of the product must not be left blank.")
    private int quantity;

    @NotNull(message = "The price of the product must not be left blank.")
    private BigDecimal price;

    @NotNull(message = "The product category code must not be left blank.")
    @NotEmpty(message = "The product category code must not be left blank.")
    private String categoryId;

    @NotNull(message = "The size of the product must not be left blank.")
    private List<String> size;
}
