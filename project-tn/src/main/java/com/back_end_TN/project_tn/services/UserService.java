package com.back_end_TN.project_tn.services;

import com.back_end_TN.project_tn.entitys.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDetailsService userDetailsService();
    List<String> getAllUsers();
    Optional<UserEntity> findUserByUsername(String username);
}
