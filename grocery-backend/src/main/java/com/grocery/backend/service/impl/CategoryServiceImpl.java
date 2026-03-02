package com.grocery.backend.service.impl;

import com.grocery.backend.dto.CategoryRequest;
import com.grocery.backend.dto.CategoryResponse;
import com.grocery.backend.dto.CategoryTreeResponse;
import com.grocery.backend.entity.Category;
import com.grocery.backend.repository.CategoryRepository;
import com.grocery.backend.service.CategoryService;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.*;

@Data
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public UUID createCategory(CategoryRequest request) {

        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Category already exists");
        }

        Category category = new Category();
        category.setName(request.getName());

        if (request.getParentCategoryId() != null) {
            Category parent = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParentCategory(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        return savedCategory.getId();
    }

    @Override
    public void updateCategory(UUID categoryId, CategoryRequest request) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(request.getName());

        if (request.getParentCategoryId() != null) {
            Category parent = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParentCategory(parent);
        } else {
            category.setParentCategory(null);
        }

        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public List<CategoryResponse> getCategoryTree() {

        return categoryRepository.findAll().stream()
                .filter(category -> category.getParentCategory() == null)
                .map(category -> {
                    CategoryResponse dto = new CategoryResponse();
                    dto.setId(category.getId());
                    dto.setName(category.getName());
                    dto.setImageUrl(category.getImageUrl());
                    return dto;
                })
                .toList();
    }


    @Override
    public List<CategoryResponse> getCategoryById(UUID categoryId) {

        List<Category> subCategories = categoryRepository.findByParentCategory_Id(categoryId);

        return subCategories.stream().map(category -> {
            CategoryResponse response = new CategoryResponse();
            response.setId(category.getId());
            response.setName(category.getName());
            response.setImageUrl(category.getImageUrl());

            if (category.getParentCategory() != null) {
                response.setCategoryParendId(category.getParentCategory().getId());
            }

            return response;
        }).toList();
    }



}
