package com.example.ecommercewebsite.repositories;

import com.example.ecommercewebsite.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageColorRepository  extends JpaRepository<Image, String> {
    @Query(value = "select * from image where product_id = :productId", nativeQuery = true)
    List<Image> getByProductId(String productId);
}
