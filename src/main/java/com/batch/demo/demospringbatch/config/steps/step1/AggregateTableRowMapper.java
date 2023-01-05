package com.batch.demo.demospringbatch.config.steps.step1;

import com.batch.demo.demospringbatch.dao.TransactionRepository;
import com.batch.demo.demospringbatch.entity.AggregateTbl;
import com.batch.demo.demospringbatch.entity.TransactionTbl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AggregateTableRowMapper implements RowMapper<List<TransactionTbl>> {

    private static String ACCT_NBR="ACCOUNT_NUMBER";
    private static String AMOUNT = "AMOUNT_SUM";

    private final TransactionRepository transactionRepo;
    @Override
    public List<TransactionTbl> mapRow(ResultSet rs, int rowNum) throws SQLException {
        log.info("Mapping resultset , ThreadId: {}, ThreadName: {}", Thread.currentThread().getId(), Thread.currentThread().getName());
        int accountNumber = rs.getInt("ACCOUNT_NUMBER");
        int total = rs.getInt("TOTAL");

        List<TransactionTbl> transactions = null;
        //Get account balance here and decide on going forward or not;
        transactions = transactionRepo.getTransactionListByAccountNumber(accountNumber);
        if(accountNumber!=1009) {
            for(TransactionTbl txn: transactions){
               txn.setProcessed(true);
           }
            transactionRepo.saveAll(transactions);
        }else{
            transactions = null;
        }

        return transactions;
    }
}
