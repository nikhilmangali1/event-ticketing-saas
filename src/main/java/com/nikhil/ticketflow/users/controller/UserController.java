package com.nikhil.ticketflow.users.controller;

import com.nikhil.ticketflow.users.entity.UserEntity;
import com.nikhil.ticketflow.users.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
        log.info("---User service injected---");
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN','ORGANIZER')")
    @GetMapping
    public List<UserEntity> getAllUsers(){
        return userService.getAllUsers();
    }
}
