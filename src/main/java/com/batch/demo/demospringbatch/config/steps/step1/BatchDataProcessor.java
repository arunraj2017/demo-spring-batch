package com.batch.demo.demospringbatch.config.steps.step1;

import com.batch.demo.demospringbatch.config.steps.StepFailureException;
import com.batch.demo.demospringbatch.dao.TransactionRepository;
import com.batch.demo.demospringbatch.entity.TransactionTbl;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Slf4j
@RequiredArgsConstructor
public class BatchDataProcessor implements ItemProcessor<List<TransactionTbl>,List<TransactionTbl>> {
    private final TransactionRepository txnRepo;

    @Override
    public List<TransactionTbl> process(List<TransactionTbl> items) throws Exception {
        log.info("Processing {} Records on ThreadId: {}, ThreadName: {}",items.size(), Thread.currentThread().getId(), Thread.currentThread().getName());
        Random random = new Random();
        List<TransactionTbl> transactions = new ArrayList<>();
        for(int i=0;i<transactions.size();i++) {
            transactions.get(i).setProcessed(true);
            if(i==5)
                throw new StepFailureException("error");
        }

        Thread.sleep(5000);
        return transactions;
    }
}
