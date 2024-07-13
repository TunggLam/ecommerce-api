package com.example.ecommercewebsite.model.proxy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImgurUploadResponse {

    private float status;

    private boolean success;

    private ImgurUploadData data;
}
