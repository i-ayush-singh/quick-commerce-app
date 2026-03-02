package com.grocery.backend.service.impl;

import com.grocery.backend.entity.Product;
import com.grocery.backend.repository.ProductRecommendationRepository;
import com.grocery.backend.repository.ProductRepository;
import com.grocery.backend.service.ProductRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class ProductRecommendationServiceImpl implements ProductRecommendationService {

    private final ProductRecommendationRepository repository;
    private final ProductRepository productRepository;
    private final StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "product:rec:";

    @Override
    public List<Product> getRecommendedProduct(UUID productId) {

        List<UUID> recommendedIds = getRecommendedProductIds(productId);
        if (recommendedIds.isEmpty()) {
            return List.of();
        }
        return productRepository.findAllWithVariantsByIdIn(recommendedIds);
    }

    @Override
    public List<UUID> getRecommendedProductIds(UUID productId) {

        String key = KEY_PREFIX + productId;


        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null && !cached.isBlank()) {
            return Arrays.stream(cached.split(","))
                    .map(UUID::fromString)
                    .toList();
        }


        ByteBuffer bb = ByteBuffer.allocate(16);
        bb.putLong(productId.getMostSignificantBits());
        bb.putLong(productId.getLeastSignificantBits());

        List<UUID> recommended = repository.findByProductId(bb.array())
                .stream()
                .map(rec -> {
                    ByteBuffer resBb = ByteBuffer.wrap(rec.getRecommendedProductId());
                    return new UUID(resBb.getLong(), resBb.getLong());
                })
                .toList();


        if (!recommended.isEmpty()) {
            String value = recommended.stream()
                    .map(UUID::toString)
                    .collect(Collectors.joining(","));

            redisTemplate.opsForValue()
                    .set(key, value, Duration.ofMinutes(30));
        }

        return recommended;
    }
}


