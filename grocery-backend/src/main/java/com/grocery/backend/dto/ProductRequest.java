package com.grocery.backend.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private UUID categoryId;
    private String Brand;
    private Double Rating;
}
