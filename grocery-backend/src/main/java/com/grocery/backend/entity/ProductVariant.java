package com.grocery.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "product_variant")
public class ProductVariant {

    @Id
    @Column(name = "sku")
    private String sku;

    @Column(nullable = false)
    private String name;

    private Double price;

    private Integer quantity;


//    @ManyToOne
//    @JoinColumn(name = "product_id")
//    private Product product;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;



    @Column(name = "image_urls",columnDefinition = "json")
    private String imageUrls;

    @Column(name = "attributes",columnDefinition = "json")
    private String attributes;

}
