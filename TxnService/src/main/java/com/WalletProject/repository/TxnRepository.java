package com.WalletProject.repository;

import com.WalletProject.model.Txn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TxnRepository extends JpaRepository<Txn,Integer> {
   // Optional<Txn> findById();
}
