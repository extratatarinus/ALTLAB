package com.example.demo.Entity;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String name;
	
	private String address;
	
	private String email;
	
	private String password;
	
	private String role;
	
	private String phone;
	
	private String imgPath;
	
	private String status;
	
	
	
	@OneToMany(mappedBy = "pid",cascade = CascadeType.ALL )
	public Set<product> product;

	
	
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	

	public Set<product> getProduct() {
		return product;
	}

	public void setProduct(Set<product> product) {
		this.product = product;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", address=" + address + ", email=" + email + ", password="
				+ password + ", role=" + role + ", phone=" + phone + ", imgPath=" + imgPath + ", product=" + product + "]";
	}

	public User(int id, String name, String address, String email, String password, String role, String phone,
			String imgPath ,String status, Set<com.example.demo.Entity.product> product) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.email = email;
		this.password = password;
		this.role = role;
		this.phone = phone;
		this.imgPath = imgPath;
		this.product = product;
		this.status=status;
	}

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	

	

}
