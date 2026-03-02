package com.grocery.backend.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OrderCreateRequest {
    private UUID userId;
    private List<OrderItemRequest> items;
}
