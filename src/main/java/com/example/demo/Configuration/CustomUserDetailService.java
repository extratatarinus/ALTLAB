package com.example.demo.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.example.demo.Entity.User;
import com.example.demo.Repository.userRepository;

@Component
public class CustomUserDetailService implements UserDetailsService{
	
	@Autowired
	private userRepository repo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		User u=repo.findByEmail(username);
		if(u==null) {
			throw new UsernameNotFoundException("Username not found!");
		}
		else {
			return new CustomUser(u);
		}
		
	}

}
