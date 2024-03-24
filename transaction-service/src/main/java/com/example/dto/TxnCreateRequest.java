package com.example.dto;

import com.example.model.Txn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TxnCreateRequest {

    @NotBlank
    private String receiver;

    @NotNull
    private Double amount;

    private String purpose;

    public Txn to(){
        return Txn.builder()
                .receiver(this.receiver)
                .amount(this.amount)
                .purpose(this.purpose)
                .build();
    }
}
