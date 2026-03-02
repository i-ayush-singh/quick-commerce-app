package com.grocery.backend.service;

import com.grocery.backend.dto.*;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    UUID createProduct (ProductRequest productRequest);

    ProductResponse getProductById(UUID productId);

    List<ProductResponse> getAllProducts();

    void deleteProductById(UUID productId);

    void updateProductById(UUID productId, ProductRequest productRequest);

    List<ProductResponse> getAllProductsByCategoryId(UUID categoryId);

    List<ProductResponse> getProductForPlp(ProductPlpFilter filter);

    ProductPdpResponse getProductDetails(UUID productId);

    List<ProductResponse> getTrendingProducts();

    List<ProductResponse> getCheapestProducts();

    List<ProductResponse> getTopRatedProducts();

    SimilarProductResponse getSimilarProductsGrouped(UUID productId);


}
