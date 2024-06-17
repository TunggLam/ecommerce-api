package com.example.ecommercewebsite.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = " product")
@EqualsAndHashCode(callSuper = false)
public class Product extends BaseEntity{

    private String name;

    private String description;

    private String imageUrl;

    private BigDecimal price;

    @Column(name = "category_id", nullable = false)
    private String categoryId;

    @Column(name = "quantity")
    private Integer quantity;

}
