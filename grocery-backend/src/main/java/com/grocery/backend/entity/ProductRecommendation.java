package com.grocery.backend.entity;

import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "product_recommendations")
public class ProductRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(
            name = "product_id",
            columnDefinition = "BINARY(16)",
            nullable = false
    )
    private byte[] productId;

    @Column(
            name = "recommended_product_id",
            columnDefinition = "BINARY(16)",
            nullable = false
    )
    private byte[] recommendedProductId;

    @Column(name = "association_strength")
    private int associationStrength;
}
