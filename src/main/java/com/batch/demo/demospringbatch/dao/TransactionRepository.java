package com.batch.demo.demospringbatch.dao;

import com.batch.demo.demospringbatch.entity.TransactionTbl;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface TransactionRepository extends JpaRepository<TransactionTbl, Integer> {
    @Query("select distinct(accountNumber) accountNumber, sum(amount) amountSum from TransactionTbl where isProcessed = false group by accountNumber")
    List<Object[]> generateAggregateData();

    @Query("from TransactionTbl tbl where tbl.accountNumber=:accountNumber and tbl.isProcessed=false order by tbl.transactionDate desc" )
    List<TransactionTbl> getTransactionListByAccountNumber(@Param("accountNumber") int accountNumber);
}
