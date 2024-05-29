package com.example.demo.Controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.Entity.User;
import com.example.demo.service.adminService;

import jakarta.servlet.http.HttpSession;

@Controller

@RequestMapping("/USER")
public class userController {
	
	@Autowired
	private adminService aservice;
	
	@GetMapping("/home")
	public String home(Model model,HttpSession session,Principal principal) {
		   User user= aservice.findByEmail(principal.getName());
		   session.setAttribute("user", user);
		  if(user.getStatus()=="Unverified") {
			  model.addAttribute("user", user);
			  return "login";
		  }
		  else {
		  model.addAttribute("user", user);
		  session.setAttribute("user", user);
		  model.addAttribute("categories", aservice.getCategories());
		  session.setAttribute("categories", aservice.getCategories());
		return "user_home";
		  }
	}
	
	@GetMapping("/subCategories/{id}")
	public String subCategories(@PathVariable("id")String subId  ,Model model,HttpSession session) {
		model.addAttribute("products", aservice.findProductFromSubCategory(subId));
		model.addAttribute("user", session.getAttribute("user"));
		model.addAttribute("categories", session.getAttribute("categories"));
		return "subCategories";
	}
}
