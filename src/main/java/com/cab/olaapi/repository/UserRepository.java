package com.cab.olaapi.repository;

import com.cab.olaapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmailId(String emailId);
}
