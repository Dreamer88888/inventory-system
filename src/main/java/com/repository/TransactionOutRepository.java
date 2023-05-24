package com.repository;

import com.entity.TransactionOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionOutRepository extends JpaRepository<TransactionOut, Long> {

    Optional<TransactionOut> findById(Long id);

    List<TransactionOut> findByKodeBarang(String kodeBarang);

    @Query("SELECT i FROM TransactionOut i WHERE i.date >=:from AND i.date <=:to")
    List<TransactionOut> findByDateBetweenOrEquals(Date from, Date to);

}
