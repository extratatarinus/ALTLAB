package com.example.demo.Entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Brand {
	
	@Id
	private String bid;
	
	private String bname;
	
	private String imgPath;
	
	private LocalDateTime createdAt;
	
	@ManyToOne
	@JoinColumn(name = "subId",referencedColumnName = "subId")
	@JsonIgnore
	public subCategory subCategory;

	

	public Brand(String bid, String bname, String imgPath, LocalDateTime createdAt,
			com.example.demo.Entity.subCategory subCategory) {
		super();
		this.bid = bid;
		this.bname = bname;
		this.imgPath = imgPath;
		this.createdAt = createdAt;
		this.subCategory = subCategory;
	}



	public String getBid() {
		return bid;
	}



	public void setBid(String bid) {
		this.bid = bid;
	}



	public String getBname() {
		return bname;
	}



	public void setBname(String bname) {
		this.bname = bname;
	}



	public String getImgPath() {
		return imgPath;
	}



	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}



	public LocalDateTime getCreatedAt() {
		return createdAt;
	}



	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}



	public subCategory getSubCategory() {
		return subCategory;
	}



	public void setSubCategory(subCategory subCategory) {
		this.subCategory = subCategory;
	}



	public Brand() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
