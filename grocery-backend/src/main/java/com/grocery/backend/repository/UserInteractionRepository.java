package com.grocery.backend.repository;

import com.grocery.backend.entity.UserInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UserInteractionRepository extends JpaRepository<UserInteraction, Integer> {
//    List<UserInteraction> findTop10ByUserIdOrderByTimestampDesc(UUID userId);
//
//    List<UserInteraction> findTop10ByProductIdOrderByTimestampDesc(UUID productId);

    @Query("""
        SELECT ui.product.id
        FROM UserInteraction ui
        WHERE ui.user.id = :userId
        ORDER BY ui.timestamp DESC
        LIMIT 10
    """)
    List<UUID> findRecentProductIdsByUser(@Param("userId") UUID userId);

    @Query("""
SELECT ui.product.id
FROM UserInteraction ui
GROUP BY ui.product.id
ORDER BY COUNT(ui.id) DESC
""")
    List<UUID> findTrendingProductIds(Pageable pageable);

}
