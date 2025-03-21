package com.WalletProject.dto;

import com.WalletProject.model.TxnStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TxnUpdateDto {
    private Integer senderId;
    private Integer receiverId;
    private Double amount;
}
