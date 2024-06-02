package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Entity.product;


public interface productRepository extends JpaRepository<product, String> {

    product findByPname(String pname);

    product findTopByPnameOrderByAddDateDesc(String name);

    @Query("select s from product s where s.brand.bid= :bid")
    List<product> findProductByBId(@Param("bid") String bid);

    @Query("select p from product p where p.brand.bid= :bid order by p.addDate DESC LIMIT 1")
    product findProductByBIdOrderByCreatedAt(@Param("bid") String bid);

    @Modifying
    @Query("update product p set p.pname= :pname, p.price= :price , p.description= :description,p.imgPath= :imgPath where p.pid= :pid")
    void updateProduct(@Param("pname") String pname, @Param("price") String price, @Param("description") String description,
                       @Param("imgPath") String imgPath, @Param("pid") String pid);

    @Query("select p from product p where p.brand.subCategory.subId= :subID")
    List<product> findProductBySubCategory(@Param("subID") String subID);

    @Query("SELECT p FROM product p WHERE p.brand.subCategory.subId IN :subCategoryIds")
    List<product> findByBrandSubCategorySubIdIn(@Param("subCategoryIds") List<String> subCategoryIds);

    @Query("SELECT p FROM product p WHERE p.brand.subCategory.category.cid IN :categoryIds")
    List<product> findByBrandSubCategoryCategoryCidIn(@Param("categoryIds") List<Integer> categoryIds);

    @Query("SELECT COUNT(p) FROM product p WHERE p.brand.subCategory.category.cid = :categoryId")
    int countByBrandSubCategoryCategoryCid(@Param("categoryId") int categoryId);

    @Query("SELECT COUNT(p) FROM product p WHERE p.brand.subCategory.subId = :subCategoryId")
    int countByBrandSubCategorySubId(@Param("subCategoryId") String subCategoryId);

}
