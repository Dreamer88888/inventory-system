package com.repository;

import com.entity.TransactionIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionInRepository extends JpaRepository<TransactionIn, Long> {

    Optional<TransactionIn> findById(Long id);

    List<TransactionIn> findByKodeBarang(String kodeBarang);

    @Query("SELECT i FROM TransactionIn i WHERE i.date >=:from AND i.date <=:to")
    List<TransactionIn> findByDateBetweenOrEquals(Date from, Date to);

    void deleteById(Long id);

}
