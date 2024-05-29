package com.example.demo.Entity;


import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class product {
	
	@Id
	private String pid;
	
	private String pname;
	
	private String description;
	
	private String imgPath;
	
	private String price;
	
	private String status;
	
	private LocalDate addDate;
	
	
	
	
	@ManyToOne
	@JoinColumn(name="bid")
	private Brand brand;
	
	public product(String pid, String pname, String description, String imgPath, String price,
			LocalDate addDate,String status, Brand brand, User u) {
		super();
		this.pid = pid;
		this.pname = pname;
		this.description = description;
		this.imgPath = imgPath;
		this.price = price;
		this.status=status;
		this.addDate = addDate;
		this.brand = brand;
		this.u = u;
	}





	public String getStatus() {
		return status;
	}





	public void setStatus(String status) {
		this.status = status;
	}





	public String getPid() {
		return pid;
	}





	public void setPid(String pid) {
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





	public Brand getBrand() {
		return brand;
	}





	public void setBrand(Brand brand) {
		this.brand = brand;
	}





	public User getU() {
		return u;
	}





	public void setU(User u) {
		this.u = u;
	}





	@ManyToOne
	@JoinColumn(name = "uid")
	private User u;
	
	



	public product() {
		super();
		// TODO Auto-generated constructor stub
	}
	


	
}
