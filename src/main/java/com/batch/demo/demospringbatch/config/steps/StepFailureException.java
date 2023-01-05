package com.batch.demo.demospringbatch.config.steps;

import lombok.RequiredArgsConstructor;

public class StepFailureException extends RuntimeException{
   StepFailureException(String message, Exception e){
       super(message, e);
   }
    public StepFailureException(String message){
        super(message);
    }


}
