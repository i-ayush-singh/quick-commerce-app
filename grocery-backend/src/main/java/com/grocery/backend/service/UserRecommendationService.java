package com.grocery.backend.service;

import com.grocery.backend.entity.Product;

import java.util.List;
import java.util.UUID;

public interface UserRecommendationService {
    public List<UUID> getRecommendationsForUser(UUID userId);

}
