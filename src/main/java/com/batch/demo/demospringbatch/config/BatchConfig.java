package com.batch.demo.demospringbatch.config;

import com.batch.demo.demospringbatch.config.steps.step0.AggregateTasklet;
import com.batch.demo.demospringbatch.config.steps.step1.AggregateTableRowMapper;
import com.batch.demo.demospringbatch.config.steps.step1.BatchDataProcessor;
import com.batch.demo.demospringbatch.config.steps.step1.TxnItemWriter;
import com.batch.demo.demospringbatch.config.steps.step2.ClearAggregateTableTasklet;
import com.batch.demo.demospringbatch.entity.ResultTbl;
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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class BatchConfig {

    @Bean
    public JpaItemWriter<List<TransactionTbl>> itemWriter(EntityManager em) {
        return new JpaItemWriterBuilder<List<TransactionTbl>>()
                .entityManagerFactory(em.getEntityManagerFactory())
                .build();

    }


    @Bean
    public ItemProcessor<TransactionTbl,ResultTbl> itemProcessor () {
        return new TransactionProcessor();
    }



    /*new step builder start*/
    @Bean
    public Job groupingAndProcessingJob(Step step0, Step step1, Step step2, JobRepository jobRepository) {
        return new JobBuilder("group and process", jobRepository)
                .start(step0)
                .next(step1)
                .next(step2)
                .incrementer(new RunIdIncrementer()) //TODO: Test validity
                .build();
    }
    @Bean
   public Step step0(JobRepository jobRepository, AggregateTasklet aggregateTasklet, PlatformTransactionManager transactionManager) {
       return new StepBuilder("step0: populate aggregate table", jobRepository)
               .tasklet(aggregateTasklet,transactionManager)
               .build();
   }

   @Bean
   public Step step1(JobRepository jobRepository, BatchDataProcessor batchDataProcessor, TxnItemWriter txnItemWriter, PlatformTransactionManager transactionManager, @Qualifier("aggregateItemReader") SynchronizedItemStreamReader aggregateItemReader){
       return new StepBuilder("step1: read from aggregateTable", jobRepository)
               .<int[],List<TransactionTbl>>chunk(1,transactionManager)
               .reader(aggregateItemReader)
               .processor(batchDataProcessor)
               .writer(txnItemWriter)
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

   /*mew step builder end*/

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



}
