package com.grocery.backend.service.impl;

import com.grocery.backend.dto.UserInteractionRequest;
import com.grocery.backend.entity.Product;
import com.grocery.backend.entity.User;
import com.grocery.backend.entity.UserInteraction;
import com.grocery.backend.repository.ProductRepository;
import com.grocery.backend.repository.UserInteractionRepository;
import com.grocery.backend.repository.UserRepository;
import com.grocery.backend.service.UserInteractionService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Data
@Slf4j
@Service
public class UserInteractionServiceImpl implements UserInteractionService {
    private final UserInteractionRepository userInteractionRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public void trackInteraction(UserInteractionRequest request){
      User user = userRepository.findById(request.getUserId()).orElseThrow(()->new RuntimeException("User not found"));
      Product product = productRepository.findById(request.getProductId()).orElseThrow(()->new RuntimeException("Product not found"));

      UserInteraction userInteraction = new UserInteraction();
      userInteraction.setUser(user);
      userInteraction.setProduct(product);
      userInteraction.setInteractionType(request.getInteractionType());
      userInteraction.setTimestamp(LocalDateTime.now());

      userInteractionRepository.save(userInteraction);
    }
}
