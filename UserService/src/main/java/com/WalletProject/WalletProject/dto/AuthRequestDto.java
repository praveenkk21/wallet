package com.WalletProject.WalletProject.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AuthRequestDto {
        private String name;
        private String password;
}
