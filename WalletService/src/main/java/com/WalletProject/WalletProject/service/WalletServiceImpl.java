package com.WalletProject.WalletProject.service;

import com.WalletProject.WalletProject.dto.WalletDto;
import com.WalletProject.WalletProject.model.Wallet;
import com.WalletProject.WalletProject.repository.WalletRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WalletServiceImpl {


    @Autowired
    private WalletRepo walletRepo;

    @Value("${wallet.start.balance}")
    private Double startBalance;

    public Wallet addWallet(WalletDto walletDto){
        Wallet wallet=Wallet.builder()
                .userId(walletDto.getUserId())
                .balance(startBalance)
                .build();

        walletRepo.save(wallet);
        return wallet;
    }

    public Wallet getWalletByContactNo(Integer contactNo) {
       return walletRepo.findByUserId(contactNo);
    }
}
