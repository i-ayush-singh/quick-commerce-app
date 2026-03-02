package com.grocery.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class ProductResponse {
    private UUID id;
    private String name;
    private String description;
    private UUID categoryId;
    private String Brand;
    private Double Rating;
//    private List<ProductVariantResponse> variants;
    private Double displayPrice;
    private String imageUrl;
}
