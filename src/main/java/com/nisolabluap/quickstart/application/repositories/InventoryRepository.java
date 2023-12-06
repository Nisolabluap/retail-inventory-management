package com.nisolabluap.quickstart.application.repositories;

import com.nisolabluap.quickstart.application.models.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    boolean existsByIsbn(String isbn);
}
