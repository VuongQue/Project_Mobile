package com.example.s_parking.repository;

import com.example.s_parking.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.avatarUrl = :avatarUrl WHERE u.username = :username")
    int updateAvatarUrlByUsername(@Param("username") String username, @Param("avatarUrl") String avatarUrl);

    String getKeyByUsername(String username);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.isActivate = true WHERE u.username = :username")
    int activateUser(@Param("username") String username);

}
