package com.example.s_parking.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @PreAuthorize("#username == authentication.name or hasRole('ADMIN')")
    @GetMapping("/profile/{username}")
    public String getProfile(@PathVariable String username) {
        return "Profile of user " + username;
    }
}
