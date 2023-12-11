package com.nisolabluap.quickstart.application.repositories;

import com.nisolabluap.quickstart.application.models.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
