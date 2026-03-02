package com.grocery.backend.repository;

import com.grocery.backend.search.document.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.UUID;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, UUID> {

    List<ProductDocument> findByNameContainingIgnoreCase(String name);

    List<ProductDocument> findByBrandIgnoreCase(String brand);
}