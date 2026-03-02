package com.grocery.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false,unique = true)
    private String email;

    private String location;


}
