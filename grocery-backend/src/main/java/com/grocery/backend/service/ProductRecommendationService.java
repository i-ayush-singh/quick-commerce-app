package com.grocery.backend.service;

import com.grocery.backend.entity.Product;
import com.grocery.backend.entity.ProductRecommendation;

import java.util.List;
import java.util.UUID;

public interface ProductRecommendationService {
    List<Product> getRecommendedProduct(UUID productId);
    List<UUID> getRecommendedProductIds(UUID productId);

}
