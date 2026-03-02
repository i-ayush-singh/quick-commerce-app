package com.grocery.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class SimilarProductResponse {
    private List<ProductResponse> sameCategory;
    private List<ProductResponse> sameBrand;
}
