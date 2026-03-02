package com.grocery.backend.service;

import com.grocery.backend.dto.OrderCreateRequest;
import com.grocery.backend.dto.OrderCreateResponse;

public interface OrderService {
    OrderCreateResponse createOrder(OrderCreateRequest request);

}
