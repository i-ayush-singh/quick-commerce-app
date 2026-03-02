package com.grocery.backend.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ProductPdpResponse {
    private UUID id;
    private String name;
    private String description;
    private String brand;
    private Double rating;

    private UUID categoryId;
    private Double displayPrice;
    private String imageUrl;
    private List<ProductVariantResponse> variants;
}
