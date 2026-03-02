package com.grocery.backend.dto;

import lombok.Data;

@Data
public class OrderItemRequest {

    private String sku;
    private Integer quantity;
}

