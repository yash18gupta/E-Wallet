package com.example.dto;


import com.example.model.TxnStatus;
import com.example.model.TxnType;
import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TxnListResponse {

    private String externalTxnId;

    private String receiver;

    private Double amount;

    private String purpose;

    private TxnStatus txnStatus;

    private String txnTime;

    private TxnType txnType;
}
