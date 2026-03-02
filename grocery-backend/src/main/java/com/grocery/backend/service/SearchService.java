package com.grocery.backend.service;

import com.grocery.backend.dto.SearchProductResponse;

import java.util.List;

public interface SearchService {
    List<SearchProductResponse> search(String q);
    List<String> suggest(String query);
}
