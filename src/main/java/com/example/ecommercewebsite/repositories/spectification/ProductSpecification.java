package com.example.ecommercewebsite.repositories.spectification;

import com.example.ecommercewebsite.entity.Product;
import com.example.ecommercewebsite.repositories.ProductRepository;
import com.example.ecommercewebsite.utils.StringUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class ProductSpecification {
    private static final String CATEGORY_ID = "categoryId";

    private static final String NAME = "name";

    private final ProductRepository productRepository;

    private static Specification<Product> hasCategoryId(String categoryId) {
        return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (StringUtils.isNullOrEmpty(categoryId)) {
                return null;
            }
            return criteriaBuilder.equal(root.get(CATEGORY_ID), categoryId);
        };
    }

    private static Specification<Product> hasName(String name) {
        return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if(StringUtils.isNullOrEmpty(name)){
                return null;
            }
            return criteriaBuilder.equal(root.get(NAME), name);
        };
    }

    public List<Product> findAllByCategoryIdAndName(String categoryId, String name){
        Specification<Product> conditions = Specification.where(hasCategoryId(categoryId).and(hasName(name)));
        return productRepository.findAll(conditions);
    }
}
