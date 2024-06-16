package com.example.demo.Repository;

import java.util.List;

import com.example.demo.Entity.category;
import com.example.demo.Entity.subCategory;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Entity.product;

public interface productRepository extends JpaRepository<product, Long> {

    @Modifying
    @Query("update product p set p.pname= :pname, p.price= :price , p.description= :description,p.imgPath= :imgPath where p.pid= :pid")
    void updateProduct(@Param("pname") String pname, @Param("price") String price, @Param("description") String description,
                       @Param("imgPath") String imgPath, @Param("pid") Long pid);

    @Query("select p from product p where p.subCategory.subId= :subID")
    List<product> findProductBySubCategory(@Param("subID") String subID);

    @Query("SELECT p FROM product p WHERE p.subCategory.subId IN :subCategoryIds")
    List<product> findByBrandSubCategorySubIdIn(@Param("subCategoryIds") List<String> subCategoryIds);

    @Query("SELECT p FROM product p WHERE p.subCategory.category.cid IN :categoryIds")
    List<product> findByBrandSubCategoryCategoryCidIn(@Param("categoryIds") List<Integer> categoryIds);

    @Query("SELECT COUNT(p) FROM product p WHERE p.subCategory.category.cid = :categoryId")
    int countByBrandSubCategoryCategoryCid(@Param("categoryId") int categoryId);

    @Query("SELECT COUNT(p) FROM product p WHERE p.subCategory.subId = :subCategoryId")
    int countByBrandSubCategorySubId(@Param("subCategoryId") String subCategoryId);

    @Query("SELECT p.subCategory.category FROM product p JOIN p.subCategory sub JOIN sub.category cat WHERE p.pid = :pid")
    category findCategoryByProductId(@Param("pid") Long pid);

    @Query("SELECT p.subCategory FROM product p JOIN p.subCategory sub WHERE p.pid = :pid")
    subCategory findSubCategoryByProductId(@Param("pid") Long pid);

    @Query("SELECT p FROM product p WHERE p.pname LIKE %:keyword%")
    List<product> findByPnameContaining(@Param("keyword") String keyword);

    @Query("SELECT p FROM product p ORDER BY p.addDate DESC")
    List<product> findTop8ByOrderByAddDateDesc(Pageable pageable);

    @Query("SELECT p FROM product p LEFT JOIN p.reviews r GROUP BY p ORDER BY COUNT(r) DESC")
    List<product> findTop8ByOrderByReviewsCountDesc(Pageable pageable);

    @Query("SELECT p FROM product p WHERE p.subCategory.category.cid = :categoryId")
    List<product> findProductsByCategoryId(@Param("categoryId") String categoryId);
}
