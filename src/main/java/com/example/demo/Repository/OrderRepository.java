package com.example.demo.Repository;

import com.example.demo.Entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatusOrderByOrderDateAsc(String status);

    @Query("SELECT SUM(o.totalSum) FROM Order o WHERE DATE(o.orderDate) = :date")
    Double sumTotalByOrderDate(@Param("date") LocalDate date);

    @Query("SELECT SUM(o.totalSum) FROM Order o WHERE YEAR(o.orderDate) = :year AND MONTH(o.orderDate) = :month")
    Double sumTotalByMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT SUM(o.totalSum) FROM Order o WHERE YEAR(o.orderDate) = :year")
    Double sumTotalByYear(@Param("year") int year);

    @Query("SELECT SUM(o.totalSum) FROM Order o")
    Double sumTotal();

    @Query("SELECT o FROM Order o WHERE o.status = 'Pending' ORDER BY o.orderDate ASC")
    List<Order> findTop8ByStatusPendingOrderByOrderDateAsc(Pageable pageable);
}
