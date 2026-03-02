package com.grocery.backend.search.service;

import com.grocery.backend.entity.Product;
import com.grocery.backend.mapper.ProductMapper;
import com.grocery.backend.repository.ProductRepository;
import com.grocery.backend.repository.ProductSearchRepository;
import com.grocery.backend.search.document.ProductDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductSearchSyncService implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final ProductSearchRepository searchRepository;
    private final ProductMapper productMapper;

    @Override
    public void run(String... args) {
        log.info(" Syncing products to Elasticsearch...");

        List<Product> products = productRepository.findAll();

        List<ProductDocument> docs = products.stream().map(p -> {
            ProductDocument doc = new ProductDocument();
            doc.setId(p.getId());
            doc.setName(p.getName());
            doc.setDescription(p.getDescription());
            doc.setBrand(p.getBrand());
            doc.setCategoryId(p.getCategory().getId());
            doc.setRating(p.getRating());

            var response = productMapper.toResponse(p);
            doc.setDisplayPrice(response.getDisplayPrice());
            doc.setImageUrl(response.getImageUrl());

            return doc;
        }).toList();

        searchRepository.saveAll(docs);

        log.info(" Synced {} products to Elasticsearch", docs.size());
    }
}
