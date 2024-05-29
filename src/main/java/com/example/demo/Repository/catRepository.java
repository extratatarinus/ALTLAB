package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Entity.category;

public interface catRepository extends JpaRepository<category, Integer> {
	@Modifying
	@Query("update category c set c.cname= :cname , c.imgPath= :imgPath where c.cid= :cid")
	public void updateCategory(@Param("cname")String cname,@Param("imgPath")String imgPath,@Param("cid")int cid);

}
