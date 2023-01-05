package com.batch.demo.demospringbatch.config.steps.step2;

import com.batch.demo.demospringbatch.dao.AggregateTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClearAggregateTableTasklet implements Tasklet {
    private final AggregateTableRepository aggregateRepo;
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        aggregateRepo.deleteAllInBatch();
        return RepeatStatus.FINISHED;
    }
}
