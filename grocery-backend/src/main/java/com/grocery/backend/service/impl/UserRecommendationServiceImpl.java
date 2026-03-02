package com.grocery.backend.service.impl;

import com.grocery.backend.entity.Product;
import com.grocery.backend.repository.ProductRepository;
import com.grocery.backend.repository.UserInteractionRepository;
import com.grocery.backend.service.UserRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserRecommendationServiceImpl implements UserRecommendationService {

    private final UserInteractionRepository userInteractionRepository;
    private final ProductRecommendationServiceImpl productRecommendationServiceImpl;
    private final ProductRepository productRepository;
    private final StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "user:recommendations";

    @Override
    public List<UUID> getRecommendationsForUser(UUID userId) {

        String key = KEY_PREFIX + ":" + userId;

        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null && !cached.isBlank()) {
            return Arrays.stream(cached.split(","))
                    .map(UUID::fromString)
                    .toList();
        }


        List<UUID> recentProducts = userInteractionRepository.findRecentProductIdsByUser(userId);

        if (recentProducts.isEmpty()) {
            return productRepository.findTop10ByOrderByRatingDesc().stream().map(Product::getId).collect(Collectors.toList());
        }


        Set<UUID> recommended = new LinkedHashSet<>();
        for (UUID productId : recentProducts) {
            recommended.addAll(
                    productRecommendationServiceImpl.getRecommendedProductIds(productId)
            );
        }


        recommended.removeAll(recentProducts);

        List<UUID> result = recommended.stream().limit(10).toList();


        String value = result.stream().map(UUID::toString).collect(Collectors.joining(","));

        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(10));

        return result;
    }
}




















//package com.grocery.backend.service.impl;
//
//import com.grocery.backend.repository.ProductRepository;
//import com.grocery.backend.repository.UserInteractionRepository;
//import com.grocery.backend.service.UserRecommendationService;
//import lombok.Data;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//import java.util.LinkedHashSet;





//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//
//@Data
//@Service
//public class UserRecommendationServiceImpl implements UserRecommendationService {
//    private final UserInteractionRepository userInteractionRepository;
//    private final ProductRecommendationServiceImpl productRecommendationServiceImpl;
//    private final ProductRepository productRepository;
//
//    private final StringRedisTemplate redisTemplate;
//
//    private static final String KEY_PREFIX =  "user:recommendations";
//
//    @Override
//    public List<UUID> getRecommendationsForUser(UUID userId){
//
//        String key = KEY_PREFIX + ":" + userId;
////        List<String> cached = (List<String>) redisTemplate.opsForValue().get(key);
////
////            if (cached != null) {
////                return cached.stream().map(UUID::fromString).toList();
////            }
//        String cached = redisTemplate.opsForValue().get(key);
//        if (cached != null) {
//            return List.of(cached.split(","))
//                    .stream()
//                    .map(UUID::fromString)
//                    .toList();
//        }
//
//        //fallback
//        List<UUID> recentProducts = userInteractionRepository.findRecentProductIdsByUser(userId);
//        if (recentProducts.isEmpty()) {
//            return productRepository.findTop10ByOrderByRatingDesc();
//        }
//        Set<UUID> recommended = new LinkedHashSet<>();
//        for (UUID productId : recentProducts) {
//            recommended.addAll(productRecommendationServiceImpl.getRecommendedProductIds(productId));
//        }
//
//        recommended.removeAll(recentProducts);
//        //updating cache
//        List<UUID>result =  recommended.stream().limit(10).toList();
//        String value = result.stream().map(UUID::toString).collect(Collectors.joining(","));
//        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(10));
//
//        return result;
//    }
//}
