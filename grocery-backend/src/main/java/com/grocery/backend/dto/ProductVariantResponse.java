package com.grocery.backend.dto;

import lombok.Data;

@Data
public class ProductVariantResponse {
    private String sku;
    private String name;
    private Double price;
    private Integer quantity;
    private String imageUrls;
    private String attributes;
}

