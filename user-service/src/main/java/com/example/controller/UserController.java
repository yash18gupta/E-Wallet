package com.example.controller;

import com.example.dto.UserCreateRequest;
import com.example.model.User;
import com.example.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;


    @PostMapping("/create")
    public void createUser(@Valid @RequestBody UserCreateRequest userCreateRequest) throws JsonProcessingException {
        userService.createUser(userCreateRequest);
    }

    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/details")
    public User getDetails(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        int id = user.getId();
        return userService.getDetails(id);
    }

    @PreAuthorize("hasAuthority('service')")
    @GetMapping("/username/{username}")
    public User getUserInfo(@PathVariable("username") String username){
        return (User) userService.loadUserByUsername(username);
    }
}
