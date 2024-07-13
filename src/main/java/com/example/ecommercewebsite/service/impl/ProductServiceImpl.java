package com.example.ecommercewebsite.service.impl;

import com.example.ecommercewebsite.entity.Category;
import com.example.ecommercewebsite.entity.Image;
import com.example.ecommercewebsite.entity.ImgurUpload;
import com.example.ecommercewebsite.entity.Product;
import com.example.ecommercewebsite.exception.AuthenticationException;
import com.example.ecommercewebsite.exception.BusinessException;
import com.example.ecommercewebsite.mapper.CategoriesMapper;
import com.example.ecommercewebsite.mapper.ProductMapper;
import com.example.ecommercewebsite.model.proxy.ImgurUploadData;
import com.example.ecommercewebsite.model.proxy.ImgurUploadResponse;
import com.example.ecommercewebsite.model.reponse.*;
import com.example.ecommercewebsite.model.request.CreateCategoryRequest;
import com.example.ecommercewebsite.model.request.CreateProductRequest;
import com.example.ecommercewebsite.model.request.ImageColorRequest;
import com.example.ecommercewebsite.proxy.ImgurProxy;
import com.example.ecommercewebsite.repositories.CategoryRepository;
import com.example.ecommercewebsite.repositories.ImageColorRepository;
import com.example.ecommercewebsite.repositories.ImgurUploadRepository;
import com.example.ecommercewebsite.repositories.ProductRepository;
import com.example.ecommercewebsite.repositories.spectification.ProductSpecification;
import com.example.ecommercewebsite.service.ProductService;
import com.example.ecommercewebsite.utils.FileUtils;
import com.example.ecommercewebsite.utils.JWTUtils;
import com.example.ecommercewebsite.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductSpecification productSpecification;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final CategoriesMapper categoriesMapper;
    private final ProductRepository productRepository;
    private final ImageColorRepository imageColorRepository;
    private final ImgurProxy imgurProxy;
    private final ImgurUploadRepository imgurUploadRepository;
    private final FileUtils fileUtils;


    @Override
    public ProductsResponse getProducts(int page, int size, String categoryId, String name) {
        List<Product> products = productSpecification.findAllByCategoryIdAndName(categoryId, name);
        List<ProductResponse> productResponses = new ArrayList<>();
        for(Product product : products.stream().skip((long)page * size).limit(size).toList()) {
            ProductResponse productResponse = productMapper.mapToProductResponse(product);
            Category category = categoryRepository.findById(product.getCategoryId()).orElse(null);
            CategoryResponse categoryResponse = categoriesMapper.mapToCategoryResponse(category);
            productResponse.setCategory(categoryResponse);
            productResponses.add(productResponse);
        }
        ProductsResponse productsResponse = new ProductsResponse();
        productsResponse.setProducts(productResponses);
        productsResponse.setTotalElements(products.size());
        return productsResponse;
    }

    @Override
    public ProductResponse getProduct(String id) {
        if(StringUtils.isNullOrEmpty(id)){
            throw new BusinessException("Id sản phẩm không được bỏ trống");
        }
        Product product = productRepository.findProductById(id).orElse(null);
        if(product == null){
            throw new BusinessException("Id sản phẩm này không tồn tại");
        }
        List<Image> images = imageColorRepository.getByProductId(product.getId());

        List<ImageColorResponse> imageColorResponses = new ArrayList<>();

        for (Image image : images) {
            ImageColorResponse imageColorResponse = new ImageColorResponse();
            imageColorResponse.setColor(image.getColor());
            imageColorResponse.setUrl(image.getUrl());
            imageColorResponse.setId(image.getId());
            imageColorResponses.add(imageColorResponse);
        }

        ProductResponse productResponse = productMapper.mapToProductResponse(product);
        productResponse.setImages(imageColorResponses);
        return productResponse;
    }

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {

       Category category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        if(category == null) {
            throw new BusinessException("Product category information not found.");
        }

        String productId = UUID.randomUUID().toString(); // = acv

        List<ImageColorResponse> images = new ArrayList<>();

        for (int i = 0; i < request.getImages().size(); i++) {
            File file = FileUtils.convertMultipartFileToFile(request.getImages().get(i).getImage());

            Map<String, Object> uploadRequest = new HashMap<>();
            uploadRequest.put("image", file);

            ImgurUploadResponse uploadResponse = imgurProxy.upload(uploadRequest);

            if(uploadResponse == null || uploadResponse.getData() == null || !uploadResponse.isSuccess()) {
                throw new BusinessException("An error occurred during the image upload.");
            }

            ImgurUploadData imgurUploadData = uploadResponse.getData();

            saveImgurUpload(imgurUploadData);

            Image image = new Image();
            image.setColor(request.getImages().get(i).getColor());
            image.setUrl(imgurUploadData.getLink());
            image.setProductId(productId);
            imageColorRepository.save(image);

            ImageColorResponse imageColorResponse = new ImageColorResponse();
            imageColorResponse.setUrl(imgurUploadData.getLink());
            imageColorResponse.setColor(request.getImages().get(i).getColor());
            images.add(imageColorResponse);

            fileUtils.deleteFileAsynchronous(file);
        }

        Product product = saveProduct(request, productId);

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setName(category.getName());
        categoryResponse.setActive(category.isActive());

        return mapToProductResponse(product, images, categoryResponse);
    }

    @Override
    public ProductResponse updateProduct(String id, CreateProductRequest request) {
        if(StringUtils.isNullOrEmpty(id)){
            throw new BusinessException("Chưa có thông tin id sản phẩm cập nhật");
        }

        Product product = productRepository.findProductById(id).orElse(null);

        if(product == null){
            throw new BusinessException("Không có thông tin sản phẩm");
        }

        Category category = categoryRepository.findCategoryBy(request.getCategoryId());
        if(category == null){
            throw new BusinessException("Thông tin loại sản phẩm không tồn tại");
        }

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setQuantity(request.getQuantity());
        product.setCategoryId(category.getId());
        productRepository.save(product);

        List<ImageColorResponse> images1 = new ArrayList<>();

        for (int i = 0; i < request.getImages().size(); i++) {
            Image image;
            if(request.getImages().get(i).getId() == null){
                image = new Image();
            }else {
                image = imageColorRepository.findById(request.getImages().get(i).getId()).orElse(null);

                if(image == null){
                    throw new BusinessException("Không tìm thầy id của ảnh cần cập nhật");
                }
            }

            File file = FileUtils.convertMultipartFileToFile(request.getImages().get(i).getImage());

            Map<String, Object> upLoadRequest = new HashMap<>();
            upLoadRequest.put("image", file);

            ImgurUploadResponse uploadResponse = imgurProxy.upload(upLoadRequest);

            if(uploadResponse == null || uploadResponse.getData() == null || !uploadResponse.isSuccess()){
                throw new BusinessException("Có lỗi trong quá trình up ảnh");
            }

            ImgurUploadData imgurUploadData = uploadResponse.getData();

            saveImgurUpload(imgurUploadData);

            image.setColor(request.getImages().get(i).getColor());
            image.setUrl(imgurUploadData.getLink());
            image.setProductId(id);
            imageColorRepository.save(image);

            ImageColorResponse imageColorResponse = new ImageColorResponse();
            imageColorResponse.setUrl(imgurUploadData.getLink());
            imageColorResponse.setColor(request.getImages().get(i).getColor());
            imageColorResponse.setId(image.getId());
            images1.add(imageColorResponse);

            fileUtils.deleteFileAsynchronous(file);
        }
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setName(category.getName());
        categoryResponse.setActive(category.isActive());

        return mapToProductResponse(product, images1 ,categoryResponse);
    }


    private static ProductResponse mapToProductResponse(Product product, List<ImageColorResponse> images, CategoryResponse category){
        ProductResponse productResponse = new ProductResponse();
        productResponse.setName(product.getName());
        productResponse.setPrice(product.getPrice());
        productResponse.setId(product.getId());
        productResponse.setImages(images);
        productResponse.setQuantity(product.getQuantity());
        productResponse.setCategory(category);
        return productResponse;

    }
    private Product saveProduct(CreateProductRequest request, String productId) {
        Product product = new Product();
        product.setId(productId);
        product.setCategoryId(request.getCategoryId());
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setDescription(request.getDescription());
        productRepository.save(product);
        return product;
    }

    private void saveImgurUpload(ImgurUploadData imgurData) {
        ImgurUpload imgurUpload = new ImgurUpload();
        imgurUpload.setImgurId(imgurData.getId());
        imgurUpload.setStatus("UPLOADED");
        imgurUpload.setDeleteHash(imgurData.getDeleteHash());
        imgurUpload.setWidth(imgurData.getWidth());
        imgurUpload.setHeight(imgurData.getHeight());
        imgurUpload.setSize(imgurData.getSize());
        imgurUpload.setType(imgurData.getType());
        imgurUpload.setImgurUrl(imgurData.getLink());
        imgurUploadRepository.save(imgurUpload);
    }


}
