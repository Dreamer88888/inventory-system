package com.repository;

import com.entity.StokReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StokReportRepository extends JpaRepository<StokReport, Long> {

    Optional<StokReport> findById(Long id);

    void deleteById(Long id);

}
