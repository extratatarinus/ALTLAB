package com.example.demo.Repository;

import com.example.demo.Entity.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface reviewsRepository extends JpaRepository<Reviews, String> {
    @Query("SELECT r FROM Reviews r WHERE r.product.pid = :pid")
    List<Reviews> findByProductId(@Param("pid") String pid);
}
