package com.example.demo.Repository;

import com.example.demo.Entity.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromocodeRepository extends JpaRepository<PromoCode, Long> {
    PromoCode findByCode(String code);
}
