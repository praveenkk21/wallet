package com.WalletProject.WalletProject.repository;

import com.WalletProject.WalletProject.model.UserProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserProfileImageRepo extends JpaRepository<UserProfileImage, Integer> {
    Optional<UserProfileImage> findByUserId(Long userId);

    boolean existsByUserId(Long id);

    @Transactional
    void deleteByUserId(Long id);
}
