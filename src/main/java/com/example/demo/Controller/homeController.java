package com.example.demo.Controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Entity.User;
import com.example.demo.service.homService;

import ch.qos.logback.core.model.Model;

@Controller
@RequestMapping("/")
public class homeController {

    private final homService service;

    public homeController(homService service) {
        this.service = service;
    }

    @GetMapping("/index")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "register";
    }

    @PostMapping("/register")
    public String register(Model model, @ModelAttribute User u, @RequestParam("image") MultipartFile file) throws IllegalStateException, IOException {
        service.register(u, file);
        return "login";
    }


}
