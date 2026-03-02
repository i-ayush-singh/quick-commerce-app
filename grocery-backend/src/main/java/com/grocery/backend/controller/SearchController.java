package com.grocery.backend.controller;

import com.grocery.backend.search.service.ESearchService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Data
@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final ESearchService searchService;

    @GetMapping
    public ResponseEntity<?> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        q = q.trim();
        log.info("Searching for [{}], page={}, size={}", q, page, size);
        return ResponseEntity.ok(searchService.search(q, page, size));
    }


    @GetMapping("/suggest")
    public ResponseEntity<?> suggest(@RequestParam String q){
        q = q.trim();
        log.info("Searching for {}", q);
        return ResponseEntity.ok(searchService.suggest(q));
    }
}
