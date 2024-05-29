package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Entity.subCategory;

public interface sunCatRepository extends JpaRepository<subCategory, String> {
	
	@Query("SELECT s FROM subCategory s WHERE s.category.cid = :categoryId ORDER BY s.createdAt DESC LIMIT 1")
	Optional<subCategory> findLatestSubCategoryByCategoryId(@Param("categoryId") int categoryId);
	subCategory findBySubName(String sname);
	
	@Query("select s from subCategory s where s.subName= :sname order by s.createdAt DESC LIMIT 1")
	subCategory findLatestSubCategoryBySubName(@Param("sname")String sname);
	
	
	@Modifying
	@Query("update subCategory s set s.subName= :sname, s.imgPath= :img where s.subId= :id")
	public void updateSubCategory(@Param("sname")String sname,@Param("img")String imgPath,@Param("id")String id);
	
	public subCategory findSubCategoryBySubName(String subName);
	
	@Modifying
	@Query("delete  from subCategory s where s.category.cid= :cid")
	public void deleteByCid(@Param("cid")int cid);
}
