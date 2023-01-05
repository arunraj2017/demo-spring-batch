package com.batch.demo.demospringbatch.config.steps.step0;

import com.batch.demo.demospringbatch.dao.AggregateTableRepository;
import com.batch.demo.demospringbatch.dao.TransactionRepository;
import com.batch.demo.demospringbatch.entity.AggregateTbl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
/**
 * This task is to generate the Aggregate Table. This is single threaded step
 * **/
@Component
@RequiredArgsConstructor
@Slf4j
public class AggregateTasklet implements Tasklet {

    private final TransactionRepository transactionRepository;
    private final AggregateTableRepository aggregateTableRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<Object[]> aggregateList = this.transactionRepository.generateAggregateData();
        this.aggregateTableRepository.saveAll(convertToAggregateTable(aggregateList));
        return RepeatStatus.FINISHED;
    }

    private List<AggregateTbl> convertToAggregateTable(List<Object[]> result) {
        List<AggregateTbl> resultList = new ArrayList<>();
        for (Object[] obj: result) {
            AggregateTbl aggregateTbl = new AggregateTbl();
            aggregateTbl.setAccountNumber((Integer) obj[0]);
            aggregateTbl.setTotal(((Long) obj[1]).intValue());
            resultList.add(aggregateTbl);
        }
        return resultList;
    }
}
