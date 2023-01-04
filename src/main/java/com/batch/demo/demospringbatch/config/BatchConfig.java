package com.batch.demo.demospringbatch.config;

import com.batch.demo.demospringbatch.config.steps.step0.AggregateTasklet;
import com.batch.demo.demospringbatch.entity.ResultTbl;
import com.batch.demo.demospringbatch.entity.TransactionTbl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class BatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final AggregateTasklet aggregateTasklet;
    @Autowired
    private final DataSource dataSource;

    @PersistenceContext
    private EntityManager em;

   /* @Bean
    public JpaCursorItemReader<TransactionTbl> itemReader() {
        JpaCursorItemReader<TransactionTbl> reader = new JpaCursorItemReader<>();
        reader.setName("JpaItemReader");
        reader.setEntityManagerFactory(em.getEntityManagerFactory());
        reader.setName("Datasource cursor item reader");
        reader.setQueryString("from TransactionTbl");
        reader.setSaveState(true);
        return reader;
    }*/

    @Bean
    public JdbcCursorItemReader itemReader2() {
        return new JdbcCursorItemReaderBuilder()
                .dataSource(this.dataSource)
                .sql("SELECT ID, ACCOUNT_NUMBER, ACCOUNT_TYPE, AMOUNT,TRANSACTION_DATE,IS_LIEN_RELEASED FROM TRANSACTION_TBL")
                .name("ItemReader2")
                .rowMapper(new TransactionTblRowMapper())
                //.fetchSize(4)
                .saveState(false)
                //.verifyCursorPosition(false)
                .build();
    }
    @Bean
    public SynchronizedItemStreamReader itemReader() {
        return new SynchronizedItemStreamReaderBuilder().delegate(itemReader2()).build();
    }

    @Bean
    public JpaItemWriter<ResultTbl> itemWriter() {
        return new JpaItemWriterBuilder<ResultTbl>()
                .entityManagerFactory(em.getEntityManagerFactory())
                .build();

    }

    @Bean
    public ItemProcessor<TransactionTbl,ResultTbl> itemProcessor () {
        return new TransactionProcessor();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1",this.jobRepository)
                .<TransactionTbl,ResultTbl> chunk(5,this.transactionManager)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .taskExecutor(taskExecutor())
                .build();
    }


    @Bean
    public Job doTransactionJob( Step step1) {
        return new JobBuilder("doTransactionJob",this.jobRepository)
                //.flow(step1).end().build();
               .start(step1).build();
    }


    /*new step builder*/
   public Step newSteps() {
       return new StepBuilder("step0", this.jobRepository)
               .tasklet(this.aggregateTasklet,this.transactionManager)
               .build();
   }

    @Bean
    public JobLauncher jobLauncher() {
        TaskExecutorJobLauncher jobLauncher  = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(this.jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return jobLauncher;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(64);
        executor.setMaxPoolSize(64);
        executor.setQueueCapacity(64);
        //executor.setRejectedExecutionHandler(new ThreadPoolExecutor().CallerRunsPolicy());
        executor.setThreadNamePrefix("MultiThreaded-");
        return executor;
    }



}
