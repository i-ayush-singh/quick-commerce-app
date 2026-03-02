package com.grocery.backend.service;

import com.grocery.backend.dto.CategoryRequest;
import com.grocery.backend.dto.CategoryResponse;
import com.grocery.backend.dto.CategoryTreeResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    UUID createCategory(CategoryRequest request);
    void updateCategory(UUID categoryId, CategoryRequest request);
    void deleteCategory(UUID categoryId);
    List<CategoryResponse> getCategoryTree();
    List<CategoryResponse> getCategoryById(UUID categoryId);

}
