package com.grocery.backend.controller;

import com.grocery.backend.dto.ProductPdpResponse;
import com.grocery.backend.dto.ProductPlpFilter;
import com.grocery.backend.dto.ProductRequest;
import com.grocery.backend.service.ProductService;
import com.grocery.backend.dto.ProductResponse;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@RestController
    @RequestMapping("/api/product")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<?>  createProduct(@RequestBody ProductRequest productRequest) {
        try{
            UUID productId = productService.createProduct(productRequest);
            Map<String ,Object> response = new HashMap<>();
            response.put("productId", productId);
            response.put("status", "success");
            return  ResponseEntity.ok(response);
        }
        catch (Exception e){
            log.error("Error creating product",e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/all")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {

        try {
            log.info("Fetching all products");
            List<ProductResponse> products = productService.getAllProducts();
            return ResponseEntity.ok(products);

        } catch (Exception e) {
            log.error("Error fetching products", e);
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable UUID categoryId) {

        try {
            log.info("Fetching products for category: {}", categoryId);
            List<ProductResponse> products = productService.getAllProductsByCategoryId(categoryId);
            return ResponseEntity.ok(products);

        } catch (Exception e) {
            log.error("Error fetching products by category", e);
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(
            @PathVariable UUID id,
            @RequestBody ProductRequest request) {

        try {
            log.info("Updating product with id: {}", id);
            productService.updateProductById(id, request);
            return ResponseEntity.ok("Product updated successfully");

        } catch (Exception e) {
            log.error("Error updating product", e);
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable UUID id) {

        try {
            log.info("Deleting product with id: {}", id);
            productService.deleteProductById(id);
            return ResponseEntity.ok("Product deleted successfully");

        } catch (Exception e) {
            log.error("Error deleting product", e);
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }


    @GetMapping("/plp")
    public ResponseEntity<List<ProductResponse>> getProductsForPlp(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        ProductPlpFilter filter = new ProductPlpFilter();
        filter.setCategoryId(categoryId);
        filter.setBrand(brand);
        filter.setMinRating(minRating);
        filter.setMinPrice(minPrice);
        filter.setMaxPrice(maxPrice);
        filter.setSortBy(sortBy);
        filter.setSortDir(sortDir);
        filter.setPage(page);
        filter.setSize(size);

        return ResponseEntity.ok(productService.getProductForPlp(filter));
    }


    @GetMapping("/{productId}")
    public ResponseEntity<ProductPdpResponse> getProductDetails(@PathVariable UUID productId){

        try {
            log.info("Fetching product details with id: {}", productId);
            ProductPdpResponse response = productService.getProductDetails(productId);
            return ResponseEntity.ok(response);
        }
        catch(Exception e){
            log.error("Error fetching product details", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/trending")
    public ResponseEntity<List<ProductResponse>> trending() {
        try {
            log.info("Fetching trending");
            List<ProductResponse> response =  productService.getTrendingProducts();
            return ResponseEntity.ok(response);
        }
        catch(Exception e){
            log.error("Error fetching trending", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/cheapest")
    public ResponseEntity<List<ProductResponse>> cheapest() {
        try {
            List<ProductResponse> response = productService.getCheapestProducts();
            return ResponseEntity.ok(response);
        }catch(Exception e){
            log.error("Error fetching cheapest", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<ProductResponse>> topRated() {
        try{
            List<ProductResponse> response =  productService.getTopRatedProducts();
            return ResponseEntity.ok(response);
        }
        catch(Exception e){
            log.error("Error fetching top rated", e);
            return ResponseEntity.internalServerError().build();
        }
    }



}


