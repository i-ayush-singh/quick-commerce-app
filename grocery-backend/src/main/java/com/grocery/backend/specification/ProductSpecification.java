
package com.grocery.backend.specification;

import com.grocery.backend.entity.Product;
import com.grocery.backend.entity.ProductVariant;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductSpecification {

    public static Specification<Product> hasCategoryId(UUID categoryId) {
        return (root, query, cb) ->
                categoryId == null ? null : cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> hasBrand(String brand) {
        return (root, query, cb) ->
                (brand == null || brand.isEmpty()) ? null : cb.equal(root.get("brand"), brand);
    }

    public static Specification<Product> hasMinRating(Double rating) {
        return (root, query, cb) ->
                rating == null ? null : cb.greaterThanOrEqualTo(root.get("rating"), rating);
    }


    public static Specification<Product> hasPriceBetween(Double minPrice, Double maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) {
                return null;
            }

            Join<Product, ProductVariant> variants = root.join("variants", JoinType.INNER);

            List<Predicate> predicates = new ArrayList<>();

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(variants.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(variants.get("price"), maxPrice));
            }

            query.distinct(true);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}