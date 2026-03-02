package com.grocery.backend.dto;

import lombok.Data;

@Data
public class ProductVariantRequest {
    private String sku;
    private String name;
    private Double price;
    private Integer quantity;
    private String imageUrl;
    private String attributes;
}
