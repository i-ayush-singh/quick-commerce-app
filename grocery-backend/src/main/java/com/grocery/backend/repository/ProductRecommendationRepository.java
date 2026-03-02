package com.grocery.backend.repository;

import com.grocery.backend.entity.Product;
import com.grocery.backend.entity.ProductRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductRecommendationRepository extends JpaRepository<ProductRecommendation, Integer> {
    @Query(value = "SELECT * FROM product_recommendations WHERE product_id = :prodId ORDER BY association_strength DESC LIMIT 4", nativeQuery = true)
    List<ProductRecommendation> findByProductId(@Param("prodId") byte[] prodId);



}
