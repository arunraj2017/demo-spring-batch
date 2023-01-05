package com.batch.demo.demospringbatch.config;

import com.batch.demo.demospringbatch.config.steps.StepFailureException;
import com.batch.demo.demospringbatch.config.steps.step0.AggregateTasklet;
import com.batch.demo.demospringbatch.config.steps.step1.AggregateTableRowMapper;
import com.batch.demo.demospringbatch.config.steps.step1.BatchDataProcessor;
import com.batch.demo.demospringbatch.config.steps.step1.TransactionDto;
import com.batch.demo.demospringbatch.config.steps.step1.TxnItemWriter;
import com.batch.demo.demospringbatch.config.steps.step2.ClearAggregateTableTasklet;
import com.batch.demo.demospringbatch.dao.TransactionRepository;
import com.batch.demo.demospringbatch.entity.TransactionTbl;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.AlwaysRetryPolicy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class BatchConfig {

    @Bean
    public Job groupingAndProcessingJob(Step step0, Step step1, Step step2, JobRepository jobRepository) {
        return new JobBuilder("group and process", jobRepository)
                .start(step0)
                .next(step1)
                .next(step2)
                .incrementer(new RunIdIncrementer()) //TODO: Test validity
                .preventRestart()
                .build();
    }
    @Bean
   public Step step0(JobRepository jobRepository, AggregateTasklet aggregateTasklet, PlatformTransactionManager transactionManager) {
       return new StepBuilder("step0: populate aggregate table", jobRepository)
               .tasklet(aggregateTasklet,transactionManager)
               .build();
   }

   @Bean
   public Step step1(JobRepository jobRepository,
                     PlatformTransactionManager transactionManager,
                     @Qualifier("itemWriter") ItemWriter<List<TransactionTbl>> txnItemWriter,
                     @Qualifier("itemProcessor") ItemProcessor<TransactionDto,List<TransactionTbl>> itemProcessor,
                     @Qualifier("aggregateItemReader") SynchronizedItemStreamReader aggregateItemReader,
                     @Qualifier("backOffPolicy") FixedBackOffPolicy backOffPolicy){
       return new StepBuilder("process Aggregate Data", jobRepository)
               .<int[], TransactionDto>chunk(1,transactionManager)
               .reader(aggregateItemReader)
               .processor(itemProcessor)
               .writer(txnItemWriter)
               .faultTolerant().backOffPolicy(backOffPolicy)
               //.skip(StepFailureException.class)
               //.skipLimit(1000)
               .retry(StepFailureException.class)
               .retryPolicy(new AlwaysRetryPolicy())
               //.retryLimit(3)
               .taskExecutor(taskExecutor())
               .build();
   }

   @Bean
   public Step step2(JobRepository jobRepository, ClearAggregateTableTasklet clearTableTasklet, PlatformTransactionManager transactionManager){
       return new StepBuilder("step2: clear tables", jobRepository)
               .tasklet(clearTableTasklet, transactionManager)
               .build();
   }


    @Bean
    public JdbcCursorItemReader aggregateTableReader(DataSource dataSource, AggregateTableRowMapper aggregateRowMapper) {
        return new JdbcCursorItemReaderBuilder()
                .dataSource(dataSource)
                .sql("SELECT ACCOUNT_NUMBER,TOTAL FROM AGGREGATE_TBL")
                .name("aggregateReader")
                .rowMapper(aggregateRowMapper)
                .saveState(false)
                .build();
    }


    @Bean
    public SynchronizedItemStreamReader aggregateItemReader(@Qualifier("aggregateTableReader") JdbcCursorItemReader aggregateTableReader) {
        return new SynchronizedItemStreamReaderBuilder().delegate(aggregateTableReader).build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(10);
        //executor.setRejectedExecutionHandler(new ThreadPoolExecutor().CallerRunsPolicy());
        executor.setThreadNamePrefix("MultiThreaded-");
        return executor;
    }

    @Bean
    public FixedBackOffPolicy backOffPolicy() {
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(10000);
        return backOffPolicy;
    }

    @Bean
    public ItemProcessor<TransactionDto,List<TransactionTbl>> itemProcessor(TransactionRepository transactionRepository){
        return new BatchDataProcessor(transactionRepository);
    }
    @Bean
    public ItemWriter<List<TransactionTbl>> itemWriter(TransactionRepository repository) {
        return new TxnItemWriter(repository);
    }


}
