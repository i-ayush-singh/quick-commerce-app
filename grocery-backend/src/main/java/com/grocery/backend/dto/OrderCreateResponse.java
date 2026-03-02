package com.grocery.backend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class OrderCreateResponse {

    private UUID orderId;
    private Double totalAmount;
    private String status;
}

