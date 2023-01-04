package com.batch.demo.demospringbatch.init;

import com.batch.demo.demospringbatch.dao.TransactionRepository;
import com.batch.demo.demospringbatch.entity.TransactionTbl;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Configuration
@AllArgsConstructor
@Slf4j
public class InitializationScript {
    final TransactionRepository transactionRepository;

    @PostConstruct
    public void initRepository(){
        log.info("DB Initialization started");
        TransactionTbl repo1 = new TransactionTbl(1000,"DDA", LocalDateTime.now(), 100);
        TransactionTbl repo2 = new TransactionTbl(1001,"DDA", LocalDateTime.now(), 10);
        TransactionTbl repo3 = new TransactionTbl(1000,"SVG", LocalDateTime.now(), 100);
        TransactionTbl repo4 = new TransactionTbl(1002,"DDA", LocalDateTime.now(), 10);
        TransactionTbl repo5 = new TransactionTbl(1000,"SVG", LocalDateTime.now(), 100);
        TransactionTbl repo6 = new TransactionTbl(1001,"DDA", LocalDateTime.now(), 10);
        TransactionTbl repo7 = new TransactionTbl(1000,"SVG", LocalDateTime.now(), 100);
        TransactionTbl repo8 = new TransactionTbl(1002,"DDA", LocalDateTime.now(), 10);
        TransactionTbl repo9 = new TransactionTbl(1002,"DDA", LocalDateTime.now(), 100);
        TransactionTbl repo10 = new TransactionTbl(1003,"DDA", LocalDateTime.now(), 10);
        TransactionTbl repo11 = new TransactionTbl(1004,"DDA", LocalDateTime.now(), 100);
        TransactionTbl repo12 = new TransactionTbl(1004,"DDA", LocalDateTime.now(), 10);
        TransactionTbl repo13 = new TransactionTbl(1004,"SVG", LocalDateTime.now(), 100);
        TransactionTbl repo14 = new TransactionTbl(1002,"DDA", LocalDateTime.now(), 10);
        TransactionTbl repo15 = new TransactionTbl(1000,"SVG", LocalDateTime.now(), 100);
        TransactionTbl repo16 = new TransactionTbl(1005,"DDA", LocalDateTime.now(), 10);
        TransactionTbl repo17 = new TransactionTbl(1006,"SVG", LocalDateTime.now(), 100);
        TransactionTbl repo18 = new TransactionTbl(1007,"DDA", LocalDateTime.now(), 10);
        TransactionTbl repo19 = new TransactionTbl(1000,"SVG", LocalDateTime.now(), 100);
        TransactionTbl repo20 = new TransactionTbl(1008,"DDA", LocalDateTime.now(), 10);
        TransactionTbl repo21 = new TransactionTbl(1009,"SVG", LocalDateTime.now(), 100);
        TransactionTbl repo22 = new TransactionTbl(1009,"DDA", LocalDateTime.now(), 10);
        TransactionTbl repo23 = new TransactionTbl(1008,"SVG", LocalDateTime.now(), 100);
        TransactionTbl repo24 = new TransactionTbl(1007,"SVG", LocalDateTime.now(), 100);
        TransactionTbl repo25 = new TransactionTbl(1000,"DDA", LocalDateTime.now(), 100);
        List<TransactionTbl> traList = Arrays.asList(repo1, repo2,repo3,repo4,repo5,repo6,repo7,repo8,repo9,repo10,
                repo11,repo12,repo13,repo14,repo15,repo16,repo17,repo18,repo19,repo20,repo21,repo22,repo23,repo24,repo25);
        this.transactionRepository.saveAll(traList);
        log.info("DB initialization complete");
    }

}
