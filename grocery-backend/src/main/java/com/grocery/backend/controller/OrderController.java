package com.grocery.backend.controller;

import com.grocery.backend.dto.OrderCreateRequest;
import com.grocery.backend.exceptions.InsufficientStockException;
import com.grocery.backend.service.OrderService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Data

@RestController
@RequestMapping("api/orders")
public class OrderController {
  private final OrderService orderService;
  private static final Logger log = LoggerFactory.getLogger(OrderController.class);


  @PostMapping("/createOrder")
    public ResponseEntity<?> createOrder(@RequestBody OrderCreateRequest request) {
      try {
          log.info("Creating order for user {}", request.getUserId());
          return ResponseEntity.ok(orderService.createOrder(request));
      } catch (InsufficientStockException e) {
          log.warn("Stock issue: {}", e.getMessage());
          return ResponseEntity
                  .badRequest()
                  .body(e.getMessage());

      } catch (Exception e) {
          log.error("Order creation failed", e);
          return ResponseEntity
                  .status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body("Something went wrong");
      }
  }
 }
