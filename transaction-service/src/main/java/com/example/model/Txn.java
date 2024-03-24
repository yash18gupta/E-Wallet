package com.example.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class  Txn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String externalTxnId;

    private String receiver;

    private String sender;

    private Double amount;
    
    private String purpose;

    @Enumerated(value = EnumType.STRING)
    private TxnStatus txnStatus;

    @CreationTimestamp
    private Date CreatedTime;
    @UpdateTimestamp
    private Date UpdatedTime;
}
