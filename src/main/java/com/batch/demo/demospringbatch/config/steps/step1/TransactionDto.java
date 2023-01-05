package com.batch.demo.demospringbatch.config.steps.step1;

import com.batch.demo.demospringbatch.entity.TransactionTbl;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TransactionDto {
    private List<TransactionTbl> transactions;
    private int prtBalance;
    private int accountNumber;
}
