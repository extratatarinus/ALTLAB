package com.example.demo.Entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;

    private String pname;

    private String description;

    private String imgPath;

    private String price;

    private String status;

    private LocalDate addDate;

    private String shortDescription;

    private String Information;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Reviews> reviews;

    @ManyToOne
    @JoinColumn(name = "subCategoryId", referencedColumnName = "subId")
    private subCategory subCategory;

    @ManyToMany(mappedBy = "products")
    private List<Favorite> favoriteLists;

    public product(Long pid, String pname, String description, String imgPath, String price,
                   LocalDate addDate, String status) {
        super();
        this.pid = pid;
        this.pname = pname;
        this.description = description;
        this.imgPath = imgPath;
        this.price = price;
        this.status = status;
        this.addDate = addDate;
    }

    public List<Favorite> getFavoriteLists() {
        return favoriteLists;
    }

    public void setFavoriteLists(List<Favorite> favoriteLists) {
        this.favoriteLists = favoriteLists;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public LocalDate getAddDate() {
        return addDate;
    }

    public void setAddDate(LocalDate addDate) {
        this.addDate = addDate;
    }

    public List<Reviews> getReviews() {
        return reviews;
    }

    public void setReviews(List<Reviews> reviews) {
        this.reviews = reviews;
    }

    public com.example.demo.Entity.subCategory getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(com.example.demo.Entity.subCategory subCategory) {
        this.subCategory = subCategory;
    }

    public String getInformation() {
        return Information;
    }

    public void setInformation(String information) {
        Information = information;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public product() {
        super();
    }
}
