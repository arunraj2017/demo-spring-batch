package com.batch.demo.demospringbatch.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "RESULT_TBL")
@Getter
@Setter
@NoArgsConstructor
public class ResultTbl {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int txnId;
    private int accountNumber;
    private String accountType;
    private LocalDateTime transactionDate;
    private int amount;
    private boolean isLienReleased;
}
