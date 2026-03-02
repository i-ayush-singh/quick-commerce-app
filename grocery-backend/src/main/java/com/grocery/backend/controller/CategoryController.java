package com.grocery.backend.controller;

import com.grocery.backend.dto.CategoryRequest;
import com.grocery.backend.dto.CategoryResponse;
import com.grocery.backend.dto.CategoryTreeResponse;
import com.grocery.backend.service.CategoryService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private static final Logger log =  LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createCategory(@RequestBody CategoryRequest request) {

        try {
            log.info("Creating category with name: {}", request.getName());
            UUID categoryId = categoryService.createCategory(request);

            Map<String, Object> response = new HashMap<>();
            response.put("id", categoryId);
            response.put("message", "Category created successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error while creating category", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }



    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable UUID id, @RequestBody CategoryRequest request) {
        try {
            log.info("Updating category with id: {}", id);
            categoryService.updateCategory(id, request);
            return ResponseEntity.ok("Category updated successfully");
        } catch (Exception e) {
            log.error("Error while updating category", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable UUID id) {

        try {
            log.info("Deleting category with id: {}", id);
            categoryService.deleteCategory(id);
            return ResponseEntity.ok("Category deleted successfully");
        } catch (Exception e) {
            log.error("Error while deleting category", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/show/tree")
    public ResponseEntity<List<CategoryResponse>> getCategoryTree() {

        try {
            log.info("Fetching category tree");
            List<CategoryResponse> tree = categoryService.getCategoryTree();
            return ResponseEntity.ok(tree);
        } catch (Exception e) {
            log.error("Error while fetching category tree", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<CategoryResponse>> getCategoryById(@PathVariable UUID id) {
        try {
            log.info("Fetching category with id: {}", id);
            List<CategoryResponse> categoryResponse = categoryService.getCategoryById(id);
            return ResponseEntity.ok(categoryResponse);
        }
        catch (Exception e) {
            log.error("Error while fetching category with id", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
