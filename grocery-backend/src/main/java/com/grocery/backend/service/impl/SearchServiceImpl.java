package com.grocery.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grocery.backend.dto.SearchProductResponse;
import com.grocery.backend.entity.Product;
import com.grocery.backend.mapper.ProductMapper;
import com.grocery.backend.repository.ProductRepository;
import com.grocery.backend.service.SearchService;
import lombok.Data;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Service
public class SearchServiceImpl implements SearchService {
    private final ProductRepository productRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final ProductMapper productMapper;

    private static final String KEY_PREFIX = "search:";

    @Override
    public List<SearchProductResponse> search(String q) {
        String key = KEY_PREFIX+q.toLowerCase();
        String cached =  redisTemplate.opsForValue().get(key);
        if(cached!=null){
            return Arrays.stream(cached.split(","))
                    .map(UUID::fromString)
                    .map(productRepository::findById)
                    .flatMap(Optional::stream)
                    .map(productMapper::toSearchResponse)
                    .toList();
        }
        List<Product>products = productRepository.searchProduct(q);

        redisTemplate.opsForValue().set(key, products.stream()
                        .map(p -> p.getId().toString())
                        .collect(Collectors.joining(",")),
                Duration.ofMinutes(5)
        );

        return products.stream()
                .map(productMapper::toSearchResponse)
                .toList();
    }

    @Override
    public List<String> suggest(String q) {
        return productRepository.suggestProductName(q);
    }
}
