package com.example.appleStore.Repository;

import com.example.appleStore.Model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser_IdOrderByTimeOfOrderDesc(@Param("userId") Long userId);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :id")
    Order findByIdWithItems(@Param("id") Long id);

    Long countByUserId(Long userId);

    @Query("SELECT SUM(o.price) FROM Order o WHERE o.user.id= :userId and o.status != 'CANCELLED'")
    Double getTotalSpentByUser(@Param("userId") Long userId);

}
