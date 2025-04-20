package com.example.s_parking.implement;

import com.example.s_parking.dto.response.UserInfoResponse;
import com.example.s_parking.entity.User;
import com.example.s_parking.repository.UserRepository;
import com.example.s_parking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserImp implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(String username, User updatedUser) {
        if (userRepository.existsById(username)) {
            return userRepository.save(updatedUser);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public void deleteUser(String username) {
        userRepository.deleteById(username);
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean validate(String username, String password) {
        return userRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public boolean register(String username, String password, String role) {
        if (userRepository.existsById(username)) {
            return false;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        userRepository.save(user);
        return true;
    }
    @Override
    public Optional<User> getUserInfo(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserInfoResponse convertToDto(User entity) {
        return UserInfoResponse.builder()
                .username(entity.getUsername())
                .fullName(entity.getFullname())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .securityKey(entity.getSecurity_key())
                .avatarUrl(entity.getAvatarUrl())
                .build();
    }

    @Override
    public boolean updateAvatarUrl(String username, String avatarUrl) {
        // Gọi phương thức repository để cập nhật avatarUrl
        int updatedCount = userRepository.updateAvatarUrlByUsername(username, avatarUrl);

        // Kiểm tra xem có bao nhiêu bản ghi được cập nhật
        return updatedCount > 0; // Nếu số bản ghi được cập nhật lớn hơn 0, trả về true
    }

    @Override
    public String getKeyByUsername(String username) {
        return userRepository.getKeyByUsername(username);
    }
}
