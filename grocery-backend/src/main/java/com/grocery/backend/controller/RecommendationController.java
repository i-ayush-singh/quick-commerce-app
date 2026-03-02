package com.grocery.backend.controller;

import com.grocery.backend.dto.ProductResponse;
import com.grocery.backend.dto.SimilarProductResponse;
import com.grocery.backend.entity.Product;
import com.grocery.backend.entity.ProductVariant;
import com.grocery.backend.mapper.ProductMapper;
import com.grocery.backend.service.ProductRecommendationService;
import com.grocery.backend.service.ProductService;
import com.grocery.backend.service.UserRecommendationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Data
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RecommendationController {
    private final ProductRecommendationService productRecommendationService;
    private final UserRecommendationService userRecommendationService;
    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping("/product/{productId}/recommendations")
    public ResponseEntity<?> getProductRecommendations(@PathVariable UUID productId) {
        try{
            log.info("Fetching recommendations for product {}", productId);

            List<ProductResponse> response = productRecommendationService.getRecommendedProduct(productId).stream().map(productMapper::toResponse).toList();

            return ResponseEntity.ok(response);

        }
        catch(Exception e){
            log.error("Error while fetching recommendations for product {}", productId, e);
            return ResponseEntity.badRequest().build();
        }

    }
    @GetMapping("/product/{productId}/similar-grouped")
    public ResponseEntity<SimilarProductResponse> getSimilarProductsGrouped(
            @PathVariable UUID productId) {

        try {
            return ResponseEntity.ok(
                    productService.getSimilarProductsGrouped(productId)
            );
        } catch (Exception e) {
            log.error("Error fetching grouped similar products", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}/recommendations")
    public ResponseEntity<?> getUSerRecommendations(@PathVariable UUID userId) {
        try{
            log.info("Fetching recommendations for user {}", userId);
            List<ProductResponse> response = userRecommendationService.getRecommendationsForUser(userId).stream().map(productService::getProductById).toList();
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            log.error("Error while fetching recommendations for user {}", userId, e);
            return ResponseEntity.badRequest().build();
        }
    }




}
