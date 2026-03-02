package com.grocery.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchProductResponse {

    private UUID id;
    private String name;
    private String brand;
    private Double rating;
}

