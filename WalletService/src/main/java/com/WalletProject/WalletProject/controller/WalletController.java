package com.WalletProject.WalletProject.controller;

import com.WalletProject.WalletProject.model.Wallet;
import com.WalletProject.WalletProject.service.WalletServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    @Autowired
    private WalletServiceImpl walletService;

    @GetMapping
    public Wallet getWallet(@RequestParam("userId") Integer userId)
    {
        System.out.println("Received request for userId: " + userId);
       return walletService.getWalletByContactNo(userId);
    }
}
