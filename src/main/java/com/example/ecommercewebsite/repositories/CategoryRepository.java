package com.example.ecommercewebsite.repositories;

import com.example.ecommercewebsite.entity.Category;
import com.example.ecommercewebsite.model.reponse.CategoryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    boolean existsByName(String name);

    @Query(value = "select * from category where is_active = true order by name", nativeQuery = true)
    List<Category> getALLCategoryActive();

    @Query(value = "select * from category where id = :id", nativeQuery = true)
    Category findCategoryBy(String id);

}
