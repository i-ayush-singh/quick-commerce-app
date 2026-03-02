package com.grocery.backend.repository;

import com.grocery.backend.entity.Product;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    boolean existsByName(String name);
    List<Product> findByCategoryId(UUID categoryId);

    @Query("""
       SELECT DISTINCT p FROM Product p
       LEFT JOIN FETCH p.variants v
       WHERE p.category.id = :categoryId
      """)
    List<Product> findForPlp(@Param("categoryId") UUID categoryId);

    @Query("""
        SELECT p from Product p
            WHERE lower(p.name) LIKE lower(concat('%', :q, '%'))
            OR (p.brand IS NOT NULL AND lower(p.brand) LIKE lower(concat('%', :q, '%')))
          """)
    List<Product> searchProduct(@Param("q") String q);

    @Query("""
        SELECT DISTINCT p.name from Product p
          WHERE LOWER(p.name) like LOWER(CONCAT(:q,'%'))
        order by p.name
          """)
    List<String> suggestProductName(@Param("q") String q);

    List<Product> findTop10ByOrderByRatingDesc();

    @Query("""
      SELECT DISTINCT p FROM Product p
     LEFT JOIN FETCH p.variants
     WHERE p.id IN :ids
""")
    List<Product> findAllWithVariantsByIdIn(@org.springframework.data.repository.query.Param("ids") List<UUID> ids);

    @Query("""
SELECT pv.product.id
FROM ProductVariant pv
GROUP BY pv.product.id
ORDER BY MIN(pv.price)
""")
    List<UUID> findCheapestProductIds(Pageable pageable);

    List<Product> findByCategory_IdAndIdNot(UUID categoryId, UUID excludeId);

    List<Product> findByBrandIgnoreCaseAndIdNot(String brand, UUID excludeId);

}
