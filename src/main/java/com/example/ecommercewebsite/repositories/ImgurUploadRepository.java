package com.example.ecommercewebsite.repositories;

import com.example.ecommercewebsite.entity.ImgurUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImgurUploadRepository extends JpaRepository<ImgurUpload, String> {
}
