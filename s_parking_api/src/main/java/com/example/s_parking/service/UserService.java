package com.example.s_parking.service;

import com.example.s_parking.dto.response.UserInfoResponse;
import com.example.s_parking.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    User createUser(User user);
    User updateUser(String username, User updatedUser);
    void deleteUser(String username);

    boolean validate(String username, String password);
    Optional<User> findByUsername(String username);
    boolean register(String username, String password, String role);

    Optional<User> getUserInfo(String username);

    UserInfoResponse convertToDto(User entity);

    boolean updateAvatarUrl(String username, String avatarUrl);
    String getKeyByUsername(String username);
}

