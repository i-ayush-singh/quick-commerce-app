package com.grocery.backend.service;

import com.grocery.backend.dto.ProductVariantRequest;
import com.grocery.backend.entity.ProductVariant;

import java.util.List;
import java.util.UUID;

public interface ProductVariantService {
    void addVariant(UUID productId, ProductVariantRequest productVariantRequest);
    List<ProductVariant> getVariantByProductId(UUID productId);
    void deleteVariant(String sku);
}
