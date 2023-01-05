package com.batch.demo.demospringbatch.config.steps.step1;

import com.batch.demo.demospringbatch.config.steps.StepContextModel;
import com.batch.demo.demospringbatch.config.steps.StepFailureException;
import com.batch.demo.demospringbatch.dao.TransactionRepository;
import com.batch.demo.demospringbatch.entity.TransactionTbl;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
public class BatchDataProcessor implements ItemProcessor<TransactionDto,List<TransactionTbl>> {
    private final TransactionRepository txnRepo;
    private JobExecution jobExecution;
    private StepExecution stepExecution;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
        this.jobExecution = stepExecution.getJobExecution();
    }

    @Override
    public List<TransactionTbl> process(TransactionDto itemDto) throws Exception {
        log.info("Processing {} Records on ThreadId: {}, ThreadName: {}",itemDto.getTransactions().size(), Thread.currentThread().getId(), Thread.currentThread().getName());
        Long jobId = jobExecution.getJobId();
        Long jobInstance = jobExecution.getJobInstance().getInstanceId();
       ExecutionContext executionContext = jobExecution.getExecutionContext();
       String key = jobId+"-"+jobInstance+itemDto.getAccountNumber()+"-currentLine";

        StepContextModel model = (StepContextModel) executionContext.get(key);
        if(model==null) {
            model = StepContextModel.builder()
                    .stepContextId(key)
                    .accountNumber(itemDto.getAccountNumber())
                    .currentBalance(itemDto.getPrtBalance())
                    .retryCount(-1)
                    .currentPage(0).build();
        }

        log.info("ExecutionContext: {}",model);
        int i= model.getCurrentPage();
        int prtBalance = itemDto.getPrtBalance();

        if(model.getRetryCount() > 3) {
            log.info("Retry limit exceeded -> Generating SNOW request for acct # {}",itemDto.getAccountNumber());
        }else {
            for(;i<itemDto.getTransactions().size();i++) {
                //do business logic here
                model.setCurrentPage(i);

                if(i==5) {
                    model.setRetryCount(model.getRetryCount()+1);
                    model.setCurrentBalance(prtBalance);
                    executionContext.put(key,model);
                    throw new StepFailureException("error");
                }else{
                    itemDto.getTransactions().get(i).setProcessed(true);
                    prtBalance -= itemDto.getTransactions().get(i).getAmount();
                }
            }
           // Thread.sleep(5000);
        }



        executionContext.remove(key);
        return itemDto.getTransactions();
    }
}
