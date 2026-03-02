package com.grocery.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String name;


    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;


    private String brand;

    private Double rating;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("product")
    private List<ProductVariant> variants;



}
