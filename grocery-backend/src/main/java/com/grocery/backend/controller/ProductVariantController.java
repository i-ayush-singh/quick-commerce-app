package com.grocery.backend.controller;

import com.grocery.backend.dto.ProductVariantRequest;
import com.grocery.backend.entity.ProductVariant;
import com.grocery.backend.service.ProductVariantService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Data
@RestController
@RequestMapping("/api")
public class ProductVariantController {
    private static final Logger log = LoggerFactory.getLogger(ProductVariantController.class);

    private final ProductVariantService productVariantService;

    @PostMapping("/products/{productId}/variant")
    public ResponseEntity<?> addVariant(@PathVariable UUID productId, @RequestBody ProductVariantRequest request) {
        try{
            log.info("Adding variant {} to product {}", request.getSku(), productId);
            productVariantService.addVariant(productId, request);
            return ResponseEntity.ok("Variant Added successfully");
         }
        catch(Exception e){
            log.error("Error adding variant", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/products/{productId}/variants")
    public ResponseEntity<?> getVariantsByProduct(@PathVariable UUID productId) {
        try{
            log.info("Getting variants for product {}", productId);
            List<ProductVariant> result = productVariantService.getVariantByProductId(productId);
            return ResponseEntity.ok(result);
        }
        catch(Exception e){
            log.error("Error getting variants for product {}", productId, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/variant/{sku}")
    public ResponseEntity<?> deleteVariant(@PathVariable String sku) {
        try{
            log.info("Deleting variant {}", sku);
            productVariantService.deleteVariant(sku);
            return ResponseEntity.ok("Variant Deleted successfully");
        }
        catch(Exception e){
            log.error("Error deleting variant", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
