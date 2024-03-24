package com.example.repository;

import com.example.model.Wallet;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

@Transactional
public interface WalletRepository extends JpaRepository<Wallet,Integer> {
    Wallet findByUsername(String username);

    @Modifying
    @Query("update Wallet w set w.balance = w.balance+:amount where w.username = :username")
    void updateWallet(String username, Double amount);
}
