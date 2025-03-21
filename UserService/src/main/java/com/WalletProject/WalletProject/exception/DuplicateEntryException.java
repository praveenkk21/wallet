package com.WalletProject.WalletProject.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class DuplicateEntryException extends RuntimeException {
    private final Map<String, String> errors;

    public DuplicateEntryException(Map<String, String> errors) {
        super("Duplicate entry error");
        this.errors = errors;
    }

}
