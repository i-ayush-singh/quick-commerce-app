package com.grocery.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;


    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;
}
