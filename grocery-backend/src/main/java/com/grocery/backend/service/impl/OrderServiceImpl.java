package com.grocery.backend.service.impl;

import com.grocery.backend.dto.OrderCreateRequest;
import com.grocery.backend.dto.OrderCreateResponse;
import com.grocery.backend.dto.OrderItemRequest;
import com.grocery.backend.entity.*;
import com.grocery.backend.exceptions.InsufficientStockException;
import com.grocery.backend.repository.OrderItemRepository;
import com.grocery.backend.repository.OrderRepository;
import com.grocery.backend.repository.ProductVariantRepository;
import com.grocery.backend.repository.UserRepository;
import com.grocery.backend.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.Data;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;

    @Override
    public OrderCreateResponse createOrder(OrderCreateRequest request){
        User user = userRepository.findById(request.getUserId()).orElseThrow(()-> new RuntimeException("User not found"));
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        double totalAmount = 0.0;
        for(OrderItemRequest item : request.getItems()) {
        ProductVariant variant = productVariantRepository.findById(item.getSku()).orElseThrow(()-> new RuntimeException("Product variant not found"));
        if(variant.getQuantity()< item.getQuantity()){
            throw new InsufficientStockException(
                    "Insufficient stock for SKU: " + item.getSku());
        }

        variant.setQuantity(variant.getQuantity()- item.getQuantity());
        productVariantRepository.save(variant);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setPrice(variant.getPrice());
        orderItem.setQuantity(item.getQuantity());
        orderItem.setProductVariant(variant);

        orderItemRepository.save(orderItem);

        totalAmount += variant.getPrice() * item.getQuantity();

        }
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        OrderCreateResponse orderCreateResponse = new OrderCreateResponse();
        orderCreateResponse.setOrderId(savedOrder.getId());
        orderCreateResponse.setTotalAmount(totalAmount);
        orderCreateResponse.setStatus(OrderStatus.CREATED.toString());
        return orderCreateResponse;

    }

}
