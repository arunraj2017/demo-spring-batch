package com.batch.demo.demospringbatch.config.steps;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class StepContextModel implements Serializable {
    String stepContextId;
    int retryCount;
    int currentPage;
    int accountNumber;
    int currentBalance;
}
