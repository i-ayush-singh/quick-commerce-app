package com.grocery.backend.repository;

import com.grocery.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByName(String name);
    List<Category> findByParentCategory_Id(UUID parentId);
}
