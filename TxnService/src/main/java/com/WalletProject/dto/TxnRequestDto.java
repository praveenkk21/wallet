package com.WalletProject.dto;

import com.WalletProject.model.TxnStatus;
import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TxnRequestDto {
    private Integer senderId;
    private Integer receiverId;
    TxnStatus txnStatus;
    private Double amount;
}
