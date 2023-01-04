package com.batch.demo.demospringbatch.dao;

import com.batch.demo.demospringbatch.entity.AggregateTbl;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface AggregateTableRepository extends JpaRepository<AggregateTbl,Integer> {
}
