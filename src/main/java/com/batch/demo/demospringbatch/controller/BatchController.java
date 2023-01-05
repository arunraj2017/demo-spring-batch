package com.batch.demo.demospringbatch.controller;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
@AllArgsConstructor
public class BatchController {
    private final Job batchJob;
    private final JobLauncher jobLauncher;
    @PostMapping("/start")
    public ResponseEntity startJob() throws Exception{
        final JobParameters jobParameters = new JobParametersBuilder()
                .addLong("run.id",System.currentTimeMillis()).toJobParameters();

        this.jobLauncher.run(this.batchJob,jobParameters);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

}
