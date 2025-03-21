package com.WalletProject.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

        private Integer id;

        private Integer userId;

        private Double balance;
}
