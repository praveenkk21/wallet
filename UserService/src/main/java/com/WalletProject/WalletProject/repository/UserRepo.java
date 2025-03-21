package com.WalletProject.WalletProject.repository;

import com.WalletProject.WalletProject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Integer> {

    User findByContactNo(String contactNo);
    User findByEmail(String email);
    Optional<User> findById(Integer id);
    Optional<User> findByName(String username);
}
