package com.grocery.backend.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grocery.backend.controller.ProductController;
import com.grocery.backend.dto.*;
import com.grocery.backend.entity.Category;
import com.grocery.backend.entity.Product;
import com.grocery.backend.entity.ProductVariant;
import com.grocery.backend.mapper.ProductMapper;
import com.grocery.backend.repository.CategoryRepository;
import com.grocery.backend.repository.ProductRepository;
import com.grocery.backend.repository.UserInteractionRepository;
import com.grocery.backend.service.ProductService;
import com.grocery.backend.specification.ProductSpecification;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserInteractionRepository userInteractionRepository;
    private final ObjectMapper objectMapper;
    private final ProductMapper productMapper;
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final StringRedisTemplate stringRedisTemplate;
    private static final String KEY_PREFIX = "product:";
    private static final String TOP_RATED_KEY = "home:topRated";
    private static final String CHEAPEST_KEY  = "home:cheapest";
    private static final String TRENDING_KEY  = "home:trending";

    @Override
    public UUID createProduct(ProductRequest productRequest) {
        if(productRepository.existsByName(productRequest.getName())){
            throw new RuntimeException("Product already exists");
        }
        Category category = categoryRepository.findById(productRequest.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setBrand(productRequest.getBrand());
        product.setRating(productRequest.getRating());
        product.setCategory(category);
        product.setCreatedAt(LocalDateTime.now());

        productRepository.save(product);

        return product.getId();

    }

    @Override
    public ProductResponse getProductById(UUID productId) {
        String key = KEY_PREFIX + productId;

        String cached = stringRedisTemplate.opsForValue().get(key);
        if(cached != null){
            try{
                return objectMapper.readValue(cached,ProductResponse.class);
            }catch(Exception e){
                stringRedisTemplate.delete(key);
            }
        }
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        try {
            stringRedisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(product), Duration.ofMinutes(60));
        }catch(Exception ignored){
        }
        return productMapper.toResponse(product);

    }

    @Override
    public List<ProductResponse> getAllProducts(){
        List<Product> products = productRepository.findAll();
        List<ProductResponse> productResponseList = new ArrayList<>();
        products.forEach(product -> productResponseList.add(productMapper.toResponse(product)));
        return productResponseList;
    }

    @Override
    public void deleteProductById(UUID productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.delete(product);
    }

    @Override
    public void updateProductById(UUID productId, ProductRequest productRequest) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        Category category = categoryRepository.findById(productRequest.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setBrand(productRequest.getBrand());
        product.setRating(productRequest.getRating());
        product.setCategory(category);

        productRepository.save(product);
    }
//    @Override
//    public List<ProductResponse> getAllProductsByCategoryId(UUID categoryId) {
//        String key = "category:" + categoryId + ":products";
//        String cached = stringRedisTemplate.opsForValue().get(key);
//        if(cached != null){
//            return Arrays.stream(cached.split(","))
//                    .map(UUID::fromString)
//                    .map(productRepository::findById)
//                    .flatMap(Optional::stream)
//                    .map(productMapper::toResponse)
//                    .toList();
//        }
//        List<Product> products = productRepository.findByCategoryId(categoryId);
//        log.info("Got products of size"+products.size());
//        List<ProductResponse> productResponseList = new ArrayList<>();
//        products.forEach(product -> productResponseList.add(productMapper.toResponse(product)));
//        return productResponseList;
//
//    }
@Override
public List<ProductResponse> getAllProductsByCategoryId(UUID categoryId) {

    String key = "category:" + categoryId + ":products";

    String cached = stringRedisTemplate.opsForValue().get(key);
    if (cached != null && !cached.isBlank()) {

        log.info("Cache hit for category {}", categoryId);

        return Arrays.stream(cached.split(","))
                .map(UUID::fromString)
                .map(productRepository::findById)
                .flatMap(Optional::stream)
                .map(productMapper::toResponse)
                .toList();
    }


    List<Product> products = productRepository.findByCategoryId(categoryId);
    log.info("Got products of size {}", products.size());

    List<ProductResponse> response = products.stream()
            .map(productMapper::toResponse)
            .toList();


    if (!products.isEmpty()) {
        String value = products.stream()
                .map(Product::getId)
                .map(UUID::toString)
                .collect(Collectors.joining(","));

        stringRedisTemplate.opsForValue().set(key, value, Duration.ofMinutes(10));
    }

    return response;
}


@Override
public List<ProductResponse> getProductForPlp(ProductPlpFilter filter) {

    String cacheKey = buildPlpCacheKey(filter);


    String cached = stringRedisTemplate.opsForValue().get(cacheKey);
    if (cached != null) {
        try {
            return objectMapper.readValue(cached, new TypeReference<List<ProductResponse>>() {});
        } catch (Exception e) {
            log.warn("Redis cache parse failed for key {}", cacheKey, e);
        }
    }


    Specification<Product> spec = Specification.allOf(
            ProductSpecification.hasCategoryId(filter.getCategoryId()),
            ProductSpecification.hasBrand(filter.getBrand()),
            ProductSpecification.hasMinRating(filter.getMinRating())
    );

    List<Product> products;

    if ("displayPrice".equalsIgnoreCase(filter.getSortBy()) || "price".equalsIgnoreCase(filter.getSortBy())) {

        products = productRepository.findAll(spec);

        products.sort((p1, p2) -> {
            Double p1Price = getMinPrice(p1);
            Double p2Price = getMinPrice(p2);

            if (p1Price == null && p2Price == null) return 0;
            if (p1Price == null) return 1;
            if (p2Price == null) return -1;

            return "desc".equalsIgnoreCase(filter.getSortDir())
                    ? p2Price.compareTo(p1Price)
                    : p1Price.compareTo(p2Price);
        });

    } else if (filter.getSortBy() != null) {

        Sort sort = Sort.by("desc".equalsIgnoreCase(filter.getSortDir())
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC,
                filter.getSortBy()
        );
        products = productRepository.findAll(spec, sort);

    } else {
        products = productRepository.findAll(spec);
    }


    List<ProductResponse> filtered = products.stream()
            .map(productMapper::toResponse)
            .filter(p -> p.getDisplayPrice() != null)
            .filter(p -> filter.getMinPrice() == null || p.getDisplayPrice() >= filter.getMinPrice())
            .filter(p -> filter.getMaxPrice() == null || p.getDisplayPrice() <= filter.getMaxPrice())
            .toList();


    int fromIndex = filter.getPage() * filter.getSize();
    int toIndex = Math.min(fromIndex + filter.getSize(), filtered.size());

    if (fromIndex >= filtered.size()) {
        return List.of();
    }

    List<ProductResponse> pagedResult = filtered.subList(fromIndex, toIndex);

    try {
        stringRedisTemplate.opsForValue().set(
                cacheKey,
                objectMapper.writeValueAsString(pagedResult),
                Duration.ofMinutes(5)
        );
    } catch (Exception e) {
        log.warn("Redis cache write failed for key {}", cacheKey, e);
    }

    return pagedResult;
}




    @Override
    public ProductPdpResponse getProductDetails(UUID productId) {

        String key = "pdp:" + productId;


        String cached = stringRedisTemplate.opsForValue().get(key);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, ProductPdpResponse.class);
            } catch (Exception e) {

            }
        }


        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product Not found"));

        ProductPdpResponse response = new ProductPdpResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setBrand(product.getBrand());
        response.setRating(product.getRating());
        response.setCategoryId(product.getCategory().getId());

        Double minPrice = null;
        String imageUrl = null;

        List<ProductVariantResponse> variantResponse =
                product.getVariants().stream()
                        .map(this::mapVariant)
                        .toList();

        if (!variantResponse.isEmpty()) {
            ProductVariantResponse cheapestVariant =
                    variantResponse.stream()
                            .min(Comparator.comparing(ProductVariantResponse::getPrice))
                            .orElse(null);

            if (cheapestVariant != null) {
                minPrice = cheapestVariant.getPrice();

                if (cheapestVariant.getImageUrls() != null &&
                        !cheapestVariant.getImageUrls().isBlank()) {
                    imageUrl = cheapestVariant.getImageUrls().split(",")[0];
                }
            }
        }

        response.setDisplayPrice(minPrice);
        response.setImageUrl(imageUrl);
        response.setVariants(variantResponse);

        try {
            stringRedisTemplate.opsForValue().set(
                    key,
                    objectMapper.writeValueAsString(response),
                    Duration.ofMinutes(30)
            );
        } catch (Exception e) {

        }

        return response;
    }


    @Override
    public List<ProductResponse> getTopRatedProducts() {
        try {

            String cached = stringRedisTemplate.opsForValue().get(TOP_RATED_KEY);
            if (cached != null) {
                return objectMapper.readValue(cached, new TypeReference<List<ProductResponse>>() {}
                );
            }


            List<ProductResponse> response =
                    productRepository.findTop10ByOrderByRatingDesc()
                            .stream()
                            .map(productMapper::toResponse)
                            .toList();


            stringRedisTemplate.opsForValue().set(
                    TOP_RATED_KEY,
                    objectMapper.writeValueAsString(response),
                    Duration.ofMinutes(10)
            );

            return response;

        } catch (Exception e) {
            log.error("Error fetching top rated products", e);
            return List.of();
        }
    }



    @Override
    public List<ProductResponse> getCheapestProducts() {
        try {

            String cached = stringRedisTemplate.opsForValue().get(CHEAPEST_KEY);
            if (cached != null) {
                return objectMapper.readValue(cached, new TypeReference<List<ProductResponse>>() {}
                );
            }

            List<UUID> ids = productRepository.findCheapestProductIds(PageRequest.of(0, 10));

            List<ProductResponse> response = productRepository.findAllById(ids)
                            .stream()
                            .map(productMapper::toResponse)
                            .toList();


            stringRedisTemplate.opsForValue().set(CHEAPEST_KEY, objectMapper.writeValueAsString(response), Duration.ofMinutes(10));

            return response;

        } catch (Exception e) {
            log.error("Error fetching cheapest products", e);
            return List.of();
        }
    }

    @Override
    public List<ProductResponse> getTrendingProducts() {
        try {

            String cached = stringRedisTemplate.opsForValue().get(TRENDING_KEY);
            if (cached != null) {
                return objectMapper.readValue(cached, new TypeReference<List<ProductResponse>>() {}
                );
            }


            List<UUID> ids = userInteractionRepository.findTrendingProductIds(PageRequest.of(0, 10));

            List<ProductResponse> response = ids.stream()
                            .map(this::getProductById)
                            .toList();


            stringRedisTemplate.opsForValue().set(TRENDING_KEY, objectMapper.writeValueAsString(response), Duration.ofMinutes(10)
            );

            return response;

        } catch (Exception e) {
            log.error("Error fetching trending products", e);
            return List.of();
        }
    }

    @Override
    public SimilarProductResponse getSimilarProductsGrouped(UUID productId) {

        Product baseProduct = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        Double basePrice = getMinPrice(baseProduct);
        if (basePrice == null) {
            return new SimilarProductResponse();
        }

        double minPrice = basePrice * 0.8;
        double maxPrice = basePrice * 1.2;


        List<ProductResponse> sameCategory = productRepository
                .findByCategory_IdAndIdNot(baseProduct.getCategory().getId(), productId)
                .stream()
                .filter(p -> isWithinPriceRange(p, minPrice, maxPrice))
                .limit(10)
                .map(productMapper::toResponse)
                .toList();


        List<ProductResponse> sameBrand = productRepository.findByBrandIgnoreCaseAndIdNot(baseProduct.getBrand(), productId)
                .stream()
                .filter(p -> isWithinPriceRange(p, minPrice, maxPrice))
                .limit(10)
                .map(productMapper::toResponse)
                .toList();

        SimilarProductResponse response = new SimilarProductResponse();
        response.setSameCategory(sameCategory);
        response.setSameBrand(sameBrand);

        return response;
    }
    private boolean isWithinPriceRange(Product product, double min, double max) {
        Double price = getMinPrice(product);
        return price != null && price >= min && price <= max;
    }

    private Double getMinPrice(Product product) {
        if (product == null || product.getVariants() == null || product.getVariants().isEmpty()) {
            return 0.0;
        }

        return product.getVariants().stream()
                .map(ProductVariant::getPrice)
                .filter(Objects::nonNull)
                .min(Double::compare)
                .orElse(0.0);
    }



    private ProductVariantResponse mapVariant(ProductVariant variant) {

        ProductVariantResponse vr = new ProductVariantResponse();
        vr.setSku(variant.getSku());
        vr.setName(variant.getName());
        vr.setPrice(variant.getPrice());
        vr.setQuantity(variant.getQuantity());
        vr.setImageUrls(variant.getImageUrls());
        vr.setAttributes(variant.getAttributes());

        return vr;
    }


    private Double getMinVariantPrice(Product product) {

        return product.getVariants()
                .stream()
                .map(ProductVariant::getPrice)
                .min(Double::compareTo)
                .orElse(null);
    }
    private String buildPlpCacheKey(ProductPlpFilter f) {
        return String.format(
                "plp:%s:%s:%s:%s:%s:%s:%s:p%d:s%d",
                f.getCategoryId(),
                f.getBrand(),
                f.getMinRating(),
                f.getMinPrice(),
                f.getMaxPrice(),
                f.getSortBy(),
                f.getSortDir(),
                f.getPage(),
                f.getSize()
        );
    }

//    private String buildPlpCacheKey(ProductPlpFilter filter) {
//        return String.format(
//                "plp:%s:%s:%s:%s:%s:%s:%s",
//                filter.getCategoryId(),
//                filter.getBrand(),
//                filter.getMinRating(),
//                filter.getMinPrice(),
//                filter.getMaxPrice(),
//                filter.getSortBy(),
//                filter.getSortDir()
//        );
//    }


//    private ProductResponse toResponse(Product product) {
//
//        ProductResponse response = new ProductResponse();
//        response.setId(product.getId());
//        response.setName(product.getName());
//        response.setDescription(product.getDescription());
//        response.setCategoryId(product.getCategory().getId());
//        response.setBrand(product.getBrand());
//        response.setRating(product.getRating());
//
//        response.setDisplayPrice(getMinVariantPrice(product));
//        return response;
//    }
}
