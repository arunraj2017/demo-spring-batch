package com.batch.demo.demospringbatch.config;

import com.batch.demo.demospringbatch.entity.ResultTbl;
import com.batch.demo.demospringbatch.entity.TransactionTbl;
import org.springframework.batch.item.ItemProcessor;

public class TransactionProcessor implements ItemProcessor<TransactionTbl, ResultTbl> {
    @Override
    public ResultTbl process(TransactionTbl item) throws Exception {
        ResultTbl res = new ResultTbl();
        res.setTxnId(item.getId());
        res.setAccountNumber(item.getAccountNumber());
        res.setAccountType(item.getAccountType());
        res.setAccountNumber(item.getAmount());
        res.setLienReleased(true);
        res.setTransactionDate(item.getTransactionDate());
        return res;
    }
}
