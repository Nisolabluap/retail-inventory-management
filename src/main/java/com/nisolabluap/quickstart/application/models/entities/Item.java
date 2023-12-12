package com.nisolabluap.quickstart.application.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nisolabluap.quickstart.application.models.enums.ProductCategory;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "available_quantity", nullable = false)
    private Long availableQuantity;

    @Column(name = "product_category")
    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    @Column(name = "price")
    private Double price;

    @Column(name = "isbn", unique = true)
    private String isbn;

    @ManyToMany(mappedBy = "favoriteItems", fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<Customer> customers;

    @PrePersist
    private void generateIsbn() {
        isbn = UUID.randomUUID().toString().replaceAll("\\D", "").substring(0, 13);
    }
}

