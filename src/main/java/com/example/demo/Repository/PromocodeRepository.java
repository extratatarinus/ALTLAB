package com.example.demo.Repository;

import com.example.demo.Entity.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PromocodeRepository extends JpaRepository<PromoCode, Long> {
    PromoCode findByCode(String code);
}
