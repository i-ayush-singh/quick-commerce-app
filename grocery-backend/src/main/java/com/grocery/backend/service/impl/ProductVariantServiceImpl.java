package com.grocery.backend.service.impl;

import com.grocery.backend.dto.ProductVariantRequest;
import com.grocery.backend.entity.Product;
import com.grocery.backend.entity.ProductVariant;
import com.grocery.backend.repository.ProductRepository;
import com.grocery.backend.repository.ProductVariantRepository;
import com.grocery.backend.service.ProductVariantService;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Data
@Service
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;

    @Override
    public void addVariant(UUID productId, ProductVariantRequest request) {
        if(productVariantRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("Variant with sku alredy exist");
        }
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        ProductVariant productVariant = new ProductVariant();
        productVariant.setSku(request.getSku());
        productVariant.setName(product.getName());
        productVariant.setPrice(request.getPrice());
        productVariant.setQuantity(request.getQuantity());
        productVariant.setImageUrls(request.getImageUrl());
        productVariant.setAttributes(request.getAttributes());
        productVariant.setProduct(product);

        productVariantRepository.save(productVariant);
    }

    @Override
    public List<ProductVariant> getVariantByProductId(UUID productId) {
        return productVariantRepository.findByProductId(productId);
    }

    @Override
    public void deleteVariant(String sku){
         productVariantRepository.deleteById(sku);
    }

}
