package com.grocery.backend.dto;

import lombok.Data;

import java.util.UUID;

//@Data
//public class ProductPlpFilter {
//
//    private UUID categoryId;
//    private String brand;
//    private Double minPrice;
//    private Double maxPrice;
//    private Double minRating;
//    private String sortBy;
//    private String sortDir;
//}

@Data
public class ProductPlpFilter {
    private UUID categoryId;
    private String brand;
    private Double minRating;
    private Double minPrice;
    private Double maxPrice;
    private String sortBy;
    private String sortDir;

    private int page = 0;
    private int size = 20;
}
