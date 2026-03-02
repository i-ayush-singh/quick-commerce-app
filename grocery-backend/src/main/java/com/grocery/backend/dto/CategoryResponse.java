package com.grocery.backend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CategoryResponse {
    private UUID id;
    private String name;
    private UUID categoryParendId;
    private String imageUrl;
}
