package com.WalletProject.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TxnExcepion extends RuntimeException {
    public TxnExcepion(String message) {
        super(message);
    }
}
