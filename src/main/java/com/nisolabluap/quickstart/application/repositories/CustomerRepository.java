package com.nisolabluap.quickstart.application.repositories;

import com.nisolabluap.quickstart.application.models.dtos.CustomerDTO;
import com.nisolabluap.quickstart.application.models.entities.Customer;
import com.nisolabluap.quickstart.application.models.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("SELECT c FROM Customer c JOIN c.favoriteItems fi WHERE fi = :item")
    List<Customer> findByFavoriteItemsContaining(Item item);

    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.favoriteItems WHERE c.id = :customer_id")
    List<CustomerDTO> findCustomerWithFavoritesById(@Param("customer_id") Long customerId);
}
