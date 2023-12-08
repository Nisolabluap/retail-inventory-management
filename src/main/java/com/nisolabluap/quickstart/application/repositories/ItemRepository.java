package com.nisolabluap.quickstart.application.repositories;

import com.nisolabluap.quickstart.application.models.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    boolean existsByIsbn(String isbn);

    boolean existsByIdAndIsbnNot(Long id, String isbn);

    Optional<Item> findByIsbn(String isbn);
}
