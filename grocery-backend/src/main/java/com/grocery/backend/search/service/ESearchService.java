package com.grocery.backend.search.service;

import com.grocery.backend.dto.ProductResponse;

import java.util.List;

public interface ESearchService {
    List<ProductResponse> search(String query, int page, int size);
    List<String> suggest(String query);
}
