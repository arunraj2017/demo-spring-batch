package com.batch.demo.demospringbatch.config.steps.step1;

import com.batch.demo.demospringbatch.dao.TransactionRepository;
import com.batch.demo.demospringbatch.entity.TransactionTbl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class TxnItemWriter implements ItemWriter<List<TransactionTbl>> {
    private final TransactionRepository repository;

    @Override
    public void write(Chunk<? extends List<TransactionTbl>> chunk) throws Exception {
        List<TransactionTbl> txns = chunk.getItems().get(0);
        this.repository.saveAll(txns);
    }
}
