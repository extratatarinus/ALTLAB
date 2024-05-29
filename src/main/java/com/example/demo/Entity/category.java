package com.example.demo.Entity;

import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class category {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cid;
	
	private String cname;
	
	private String imgPath;
	

	public String getImgPath() {
		return imgPath;
	}





	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}





	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
	private List<subCategory>subCategory;
	
	

	

	public category(int cid, String cname, List<com.example.demo.Entity.subCategory> subCategory,String imgPath) {
		super();
		this.cid = cid;
		this.cname = cname;
		this.subCategory = subCategory;
		this.imgPath=imgPath;
	}





	public int getCid() {
		return cid;
	}





	public void setCid(int cid) {
		this.cid = cid;
	}





	public String getCname() {
		return cname;
	}





	public void setCname(String cname) {
		this.cname = cname;
	}





	public List<subCategory> getSubCategory() {
		return subCategory;
	}





	public void setSubCategory(List<subCategory> subCategory) {
		this.subCategory = subCategory;
	}





	public category() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
