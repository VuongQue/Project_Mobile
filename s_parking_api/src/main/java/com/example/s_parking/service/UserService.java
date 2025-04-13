package com.example.s_parking.service;

import com.example.s_parking.entity.User;
import com.example.s_parking.implement.UserImp;
import com.example.s_parking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public interface UserService {
    public List<User> getAllUsers();
    User createUser(User user);
    User updateUser(String username, User updatedUser);
    void deleteUser(String username);

    boolean validate(String username, String password);
    Optional<User> findByUsername(String username);
    boolean register(String username, String password, String role);
}

