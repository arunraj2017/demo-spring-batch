package com.batch.demo.demospringbatch.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "TRANSACTION_TBL")
public class TransactionTbl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NonNull private int accountNumber;
    @NonNull private String accountType;
    @NonNull private LocalDateTime transactionDate;
    @NonNull private int amount;
    private boolean isLienReleased=false;
    private boolean isProcessed=false;
}
