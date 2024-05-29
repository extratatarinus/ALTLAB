package com.example.demo.Entity;

	import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class subCategory {
	
	
	@Id
	private String subId;
	
	private String subName;
	
	private LocalDateTime createdAt;
	
	@Column(name = "imgPath")
	private String imgPath;
	
	
	@OneToMany(mappedBy = "subCategory",cascade = CascadeType.ALL)
	public List<Brand> brand;
	
	
	
	
	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime localDate) {
		this.createdAt = localDate;
	}

	@ManyToOne
	@JoinColumn(name = "cid", referencedColumnName = "cid")
	@JsonIgnore
	private category category;

	public String getSubId() {
		return subId;
	}

	public void setSubId(String subId) {
		this.subId = subId;
	}

	public String getSubName() {
		return subName;
	}

	public void setSubName(String subName) {
		this.subName = subName;
	}

	public category getCategory() {
		return category;
	}

	public void setCategory(category category) {
		this.category = category;
	}

	public subCategory() {
		super();
		// TODO Auto-generated constructor stub
	}

	public subCategory(String subId, String subName, com.example.demo.Entity.category category,LocalDateTime createdAt,
			String imgPath) {
		super();
		this.subId = subId;
		this.subName = subName;
		this.category = category;
		this.createdAt=createdAt;
		this.imgPath=imgPath;
	}

	@Override
	public String toString() {
		return "subCategory [subId=" + subId + ", subName=" + subName + ", createdAt=" + createdAt + ", category="
				+ category + "]";
	}
	
	
	
	

}
