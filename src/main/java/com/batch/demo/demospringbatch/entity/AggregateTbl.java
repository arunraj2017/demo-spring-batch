package com.batch.demo.demospringbatch.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AggregateTbl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    int accountNumber;
    int total;
}
