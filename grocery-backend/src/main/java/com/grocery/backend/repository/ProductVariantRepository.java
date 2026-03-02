package com.grocery.backend.repository;

import com.grocery.backend.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, String> {
    List <ProductVariant> findByProductId(UUID productId);
    boolean existsBySku(String sku);
}
