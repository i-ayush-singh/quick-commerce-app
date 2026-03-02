package com.grocery.backend.repository;

import com.grocery.backend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

}
