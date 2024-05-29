package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Entity.Brand;

public interface brandRepository extends JpaRepository<Brand, String> {
	@Query("select b from Brand b where b.subCategory.subId= :subId")
	public List<Brand> getBrands(@Param("subId")String subID);
	
	@Query("select b from Brand b where b.subCategory.subId= :subId order by b.createdAt DESC LIMIT 1")
	public Brand findLastAddedBrand(@Param("subId")String subId);
	
	@Modifying
	@Query("update Brand b set b.bname= :bname, b.imgPath= :imgPath where b.bid= :bid")
	public void updateBrand(@Param("bname")String bname,@Param("imgPath")String imgPath,@Param("bid")String bid); 

}
