package com.ifeanyi.Oauthserver.repository;

import com.ifeanyi.Oauthserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Userrepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);
}
