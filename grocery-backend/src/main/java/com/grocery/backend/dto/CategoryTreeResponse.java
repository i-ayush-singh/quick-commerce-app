package com.grocery.backend.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class CategoryTreeResponse {

    private UUID id;
    private String name;
    private List<CategoryTreeResponse> children = new ArrayList<>();

}
