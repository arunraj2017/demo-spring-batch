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
public class AggregateTableRowMapper implements RowMapper<TransactionDto> {

    private static String ACCT_NBR="ACCOUNT_NUMBER";
    private static String AMOUNT = "AMOUNT_SUM";

    private final TransactionRepository transactionRepo;
    @Override
    public TransactionDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        log.info("Mapping resultset , ThreadId: {}, ThreadName: {}", Thread.currentThread().getId(), Thread.currentThread().getName());
        int accountNumber = rs.getInt("ACCOUNT_NUMBER");
        int total = rs.getInt("TOTAL");

        //Get account balance here and decide on going forward or not;
        List<TransactionTbl> transactions = transactionRepo.getTransactionListByAccountNumber(accountNumber);

        return TransactionDto.builder()
                .transactions(transactions)
                .accountNumber(accountNumber)
                .prtBalance(total)
                .build();
    }
}
