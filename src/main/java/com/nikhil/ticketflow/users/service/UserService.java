package com.nikhil.ticketflow.users.service;

import com.nikhil.ticketflow.users.entity.UserEntity;
import com.nikhil.ticketflow.users.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JpaUserRepository userRepository;

    public List<UserEntity> getAllUsers(){
        return userRepository.findAll();
    }
}
