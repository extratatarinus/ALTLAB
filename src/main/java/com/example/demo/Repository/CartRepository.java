package com.example.demo.Repository;

import com.example.demo.Entity.Cart;
import com.example.demo.Entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT cart FROM Cart cart WHERE cart.user.id = :userId")
    Cart findByUserId(@Param("userId") int userId);
}
