package com.example.demo.Repository;

import com.example.demo.Entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface favoriteRepository extends JpaRepository<Favorite, Integer> {
    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId")
    Favorite findByUserId(@Param("userId") int userId);
}
