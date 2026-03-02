package com.grocery.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_variant_sku", referencedColumnName = "sku")
    private ProductVariant productVariant;

    private Integer quantity;

    private Double price;
}
