package com.grocery.backend.repository;

import com.grocery.backend.dto.OrderItemRequest;
import com.grocery.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

}
