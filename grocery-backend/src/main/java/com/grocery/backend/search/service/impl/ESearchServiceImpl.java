package com.grocery.backend.search.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.grocery.backend.dto.ProductResponse;
import com.grocery.backend.repository.ProductSearchRepository;
import com.grocery.backend.search.document.ProductDocument;
import com.grocery.backend.search.service.ESearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ESearchServiceImpl implements ESearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ProductSearchRepository searchRepository;


    @Override
    public List<ProductResponse> search(String query, int page, int size) {

        log.info("Searching for [{}], page={}, size={}", query, page, size);

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(q -> q
                        .multiMatch(m -> m
                                .query(query)
                                .fields("name", "description", "brand")
                                .type(TextQueryType.BestFields)
                        )
                )
                .withPageable(PageRequest.of(page, size))
                .build();

        SearchHits<ProductDocument> hits = elasticsearchOperations.search(searchQuery, ProductDocument.class);

        return hits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .map(this::mapToResponse)
                .toList();
    }


    private ProductResponse mapToResponse(ProductDocument doc) {
        return ProductResponse.builder()
                .id(doc.getId())
                .name(doc.getName())
                .description(doc.getDescription())
                .categoryId(doc.getCategoryId())
                .Brand(doc.getBrand())
                .Rating(doc.getRating())
                .displayPrice(doc.getDisplayPrice())
                .imageUrl(doc.getImageUrl())
                .build();
    }

    @Override
    public List<String> suggest(String query) {

        log.info("Suggesting for [{}]", query);

        List<String> nameSuggestions = searchRepository.findByNameContainingIgnoreCase(query)
                        .stream()
                        .map(ProductDocument::getName)
                        .toList();

        List<String> brandSuggestions = searchRepository.findByBrandIgnoreCase(query)
                        .stream()
                        .map(ProductDocument::getBrand)
                        .toList();

        return Stream.concat(nameSuggestions.stream(), brandSuggestions.stream())
                .filter(Objects::nonNull)
                .map(String::trim)
                .distinct()
                .limit(20)
                .toList();
    }

}
