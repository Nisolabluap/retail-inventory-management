package com.nisolabluap.quickstart.application.repositories;

import com.nisolabluap.quickstart.application.models.entities.Item;
import com.nisolabluap.quickstart.application.models.enums.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    boolean existsByIsbn(String isbn);

    boolean existsByIdAndIsbnNot(Long id, String isbn);

    Optional<Item> findByIsbn(String isbn);

    @Query("SELECT i FROM Item i WHERE " +
            "(:name IS NULL OR i.name LIKE %:name%) AND " +
            "(:category IS NULL OR i.productCategory = :category) AND " +
            "(:description IS NULL OR i.description LIKE %:description%) AND " +
            "(:id IS NULL OR i.id = :id) AND " +
            "(:isbn IS NULL OR i.isbn = :isbn) AND " +
            "(:availableQuantity IS NULL OR i.availableQuantity <= :availableQuantity) AND " +
            "(:price IS NULL OR i.price <= :price)")
    List<Item> searchItems(
            @Param("name") String name,
            @Param("category") ProductCategory category,
            @Param("description") String description,
            @Param("id") Long id,
            @Param("isbn") String isbn,
            @Param("availableQuantity") Long availableQuantity,
            @Param("price") Double price
    );
}
