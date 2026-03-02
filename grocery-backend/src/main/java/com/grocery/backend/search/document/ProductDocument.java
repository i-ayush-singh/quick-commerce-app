package com.grocery.backend.search.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.UUID;

@Data
@Document(indexName = "products")
public class ProductDocument {

    @Id
    private UUID id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Keyword)
    private String brand;

    @Field(type = FieldType.Keyword)
    private UUID categoryId;

    @Field(type = FieldType.Double)
    private Double rating;

    @Field(type = FieldType.Double)
    private Double displayPrice;

    @Field(type = FieldType.Keyword)
    private String imageUrl;
}
