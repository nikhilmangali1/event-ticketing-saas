package com.nikhil.ticketflow.users.controller;

import com.nikhil.ticketflow.users.entity.UserEntity;
import com.nikhil.ticketflow.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    private List<UserEntity> getAllUsers(){
        return userService.getAllUsers();
    }
}
