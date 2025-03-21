package com.WalletProject.WalletProject.repository;

import com.WalletProject.WalletProject.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface WalletRepo extends JpaRepository<Wallet,Integer> {
    Wallet findByUserId(Integer userId);


    //Given UserId, and i need to update their balance
    @Transactional
    @Modifying
    @Query(value = "update Wallet w set w.balance = w.balance + :amount where w.user_id=:user_id",nativeQuery = true)
    void updateWallet(@Param("user_id") Integer userId,@Param("amount") Double amount);
}
