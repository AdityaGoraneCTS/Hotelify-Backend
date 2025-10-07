package com.cts.user_service.repository;

import com.cts.user_service.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    @Transactional
    @Modifying
    @Query("update User u set u.password = ?2 where u.email = ?1")
    void updatePassword(String email, String password);

    // ==> NEW METHOD FOR UPDATING LOYALTY POINTS <==
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.loyaltyPoints = :points WHERE u.id = :userId")
    int updateLoyaltyPoints(@Param("userId") String userId, @Param("points") Long points);
}