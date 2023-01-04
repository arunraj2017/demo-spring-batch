package com.batch.demo.demospringbatch.dao;

import com.batch.demo.demospringbatch.entity.TransactionTbl;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface TransactionRepository extends JpaRepository<TransactionTbl, Integer> {
    @Query("select distinct(accountNumber) accountNumber, sum(amount) amountSum from TransactionTbl where isProcessed = false group by accountNumber")
    List<Object[]> generateAggregateData();
}
