package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Entity.User;

public interface userRepository extends JpaRepository<User, Integer> {
	
	User findByEmail(String email);
	
	List<User> findByStatus(String status);
	
	@Modifying
	@Query("update User u set u.status= :status where u.id= :id")
	void verify(@Param("status")String status,@Param("id")int id);
	
	List<User> findByRole(String role);
	
	@Modifying
	@Query("update User s set s.name= :name, s.address= :address , s.email= :email, s.phone= :phone where s.id= :id")
	void updateUser(@Param("name")String name,@Param("address")String address,@Param("email")String email,
			@Param("phone")String phone,@Param("id")int id);

}
