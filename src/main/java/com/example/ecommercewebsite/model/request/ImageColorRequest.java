package com.example.ecommercewebsite.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageColorRequest {
    private String color;

    private MultipartFile image;

    private String id;
}
