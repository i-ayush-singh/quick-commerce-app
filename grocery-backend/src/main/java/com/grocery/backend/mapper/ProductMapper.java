package com.grocery.backend.mapper;

import com.grocery.backend.dto.ProductResponse;
import com.grocery.backend.dto.SearchProductResponse;
import com.grocery.backend.entity.Product;
import com.grocery.backend.entity.ProductVariant;
import org.springframework.transaction.annotation.Transactional;
import lombok.Builder;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;


@Component
@Transactional(readOnly = true)
public class ProductMapper {

//    public ProductResponse toResponse(Product product) {
//        return ProductResponse.builder()
//                .id(product.getId())
//                .name(product.getName())
//                .Brand(product.getBrand())
//                .Rating(product.getRating())
//                .description(product.getDescription())
//                .build();
//    }

    public ProductResponse toResponse(Product product) {

        List<ProductVariant> variants = product.getVariants();

        Double minPrice = null;
        String imageUrl = null;

        if (variants != null && !variants.isEmpty()) {
            ProductVariant cheapestVariant = variants.stream()
                    .min(Comparator.comparing(ProductVariant::getPrice))
                    .orElse(null);

            if (cheapestVariant != null) {
                minPrice = cheapestVariant.getPrice();

                if (cheapestVariant.getImageUrls() != null && !cheapestVariant.getImageUrls().isBlank()) {

                    imageUrl = cheapestVariant.getImageUrls().split(",")[0];
                }
            }
        }

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .categoryId(product.getCategory().getId())
                .Brand(product.getBrand())
                .Rating(product.getRating())
                .displayPrice(minPrice)
                .imageUrl(imageUrl)
                .build();
    }

    public SearchProductResponse toSearchResponse(Product product) {
        return SearchProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .rating(product.getRating())
                .build();
    }
}
