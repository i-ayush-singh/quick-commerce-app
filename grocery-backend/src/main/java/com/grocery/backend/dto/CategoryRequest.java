package com.grocery.backend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CategoryRequest {
    private String name;
    private UUID parentCategoryId;
}
