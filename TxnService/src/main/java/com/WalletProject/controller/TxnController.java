package com.WalletProject.controller;

import com.WalletProject.dto.TxnRequestDto;
import com.WalletProject.model.Txn;
import com.WalletProject.service.TxnServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/txns")
public class TxnController {

    @Autowired
    private TxnServiceImpl txnService;

    @PostMapping("/update")
    public Txn createTxn(@RequestBody TxnRequestDto txnRequestDto) throws JsonProcessingException {
        return txnService.createTxn(txnRequestDto);
    }
}
