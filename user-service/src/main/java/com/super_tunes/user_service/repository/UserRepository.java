package com.super_tunes.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.super_tunes.user_service.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long>{

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}